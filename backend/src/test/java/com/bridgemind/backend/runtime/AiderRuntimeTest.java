package com.bridgemind.backend.runtime;

import com.bridgemind.backend.execution.DockerExecutionProperties;
import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AiderRuntimeTest {

    @Mock private RuntimeExecutionRepository repository;
    @Mock private WorkspaceFilesystemService workspaceService;
    @Mock private ApplicationEventPublisher eventPublisher;

    private DockerExecutionProperties properties;
    private AiderRuntime runtime;
    private UUID workspaceId;
    private UUID executionId;
    private RuntimeLaunchRequest request;

    @BeforeEach
    void setUp() {
        properties = new DockerExecutionProperties();
        runtime = new AiderRuntime(repository, workspaceService, properties, eventPublisher);
        workspaceId = UUID.randomUUID();
        executionId = UUID.randomUUID();
        request = new RuntimeLaunchRequest(UUID.randomUUID(), UUID.randomUUID(), workspaceId, UUID.randomUUID(), "Test instruction");
    }

    @Test
    void testId() {
        // Verify that the runtime correctly identifies itself as "aider"
        assertThat(runtime.id()).isEqualTo("aider");
    }

    @Test
    void testStartPersistsExecutionAndSetsStatusToRunning() throws Exception {
        RuntimeExecution mockExecution = new RuntimeExecution("aider", request);
        // Force the ID via reflection because JPA would normally generate it
        java.lang.reflect.Field idField = RuntimeExecution.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockExecution, executionId);
        mockExecution.setStatus(RuntimeStatus.RUNNING);

        when(repository.save(any(RuntimeExecution.class))).thenReturn(mockExecution);

        Path fakeWorkspace = Files.createTempDirectory("test_workspace_");
        when(workspaceService.provision(workspaceId)).thenReturn(fakeWorkspace);

        RuntimeExecution execution = runtime.start(request);

        // The returned execution should be in RUNNING state after start()
        assertThat(execution.getStatus()).isEqualTo(RuntimeStatus.RUNNING);

        ArgumentCaptor<RuntimeExecution> captor = ArgumentCaptor.forClass(RuntimeExecution.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(RuntimeStatus.RUNNING);

        // Let the async thread run briefly — it will hit an IOException because Docker isn't available
        Thread.sleep(100);

        // Clean up the temporary workspace directory
        java.io.File dir = fakeWorkspace.toFile();
        if (dir.exists()) {
            java.io.File[] files = dir.listFiles();
            if (files != null) {
                for (java.io.File file : files) file.delete();
            }
            dir.delete();
        }
    }

    @Test
    void testCancelUpdatesStatus() throws Exception {
        RuntimeExecution mockExecution = new RuntimeExecution("aider", request);
        java.lang.reflect.Field idField = RuntimeExecution.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockExecution, executionId);
        mockExecution.setStatus(RuntimeStatus.RUNNING);

        when(repository.findById(executionId)).thenReturn(Optional.of(mockExecution));
        when(repository.save(any(RuntimeExecution.class))).thenReturn(mockExecution);

        RuntimeExecution cancelled = runtime.cancel(executionId);

        // Cancel should transition to CANCELLED and log the cancellation message
        assertThat(cancelled.getStatus()).isEqualTo(RuntimeStatus.CANCELLED);
        assertThat(cancelled.getLogs()).contains("Execution cancelled by user.");
    }
}
