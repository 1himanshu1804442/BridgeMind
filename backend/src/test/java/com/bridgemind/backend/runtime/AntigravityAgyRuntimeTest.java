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
public class AntigravityAgyRuntimeTest {

    @Mock private RuntimeExecutionRepository repository;
    @Mock private WorkspaceFilesystemService workspaceService;
    @Mock private ApplicationEventPublisher eventPublisher;

    private DockerExecutionProperties properties;
    private AntigravityAgyRuntime runtime;
    private UUID workspaceId;
    private UUID executionId;
    private RuntimeLaunchRequest request;

    @BeforeEach
    void setUp() {
        properties = new DockerExecutionProperties();
        runtime = new AntigravityAgyRuntime(repository, workspaceService, properties, eventPublisher);
        workspaceId = UUID.randomUUID();
        executionId = UUID.randomUUID();
        request = new RuntimeLaunchRequest(UUID.randomUUID(), UUID.randomUUID(), workspaceId, UUID.randomUUID(), "Test AGY instruction");
    }

    @Test
    void testId() {
        assertThat(runtime.id()).isEqualTo("antigravity-agy");
    }

    @Test
    void testStartPersistsExecutionAndSetsStatusToRunning() throws Exception {
        RuntimeExecution mockExecution = new RuntimeExecution("antigravity-agy", request);
        java.lang.reflect.Field idField = RuntimeExecution.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockExecution, executionId);
        mockExecution.setStatus(RuntimeStatus.RUNNING);

        when(repository.save(any(RuntimeExecution.class))).thenReturn(mockExecution);

        Path fakeWorkspace = Files.createTempDirectory("test_workspace_agy_");
        when(workspaceService.provision(workspaceId)).thenReturn(fakeWorkspace);

        RuntimeExecution execution = runtime.start(request);

        assertThat(execution.getStatus()).isEqualTo(RuntimeStatus.RUNNING);

        ArgumentCaptor<RuntimeExecution> captor = ArgumentCaptor.forClass(RuntimeExecution.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(RuntimeStatus.RUNNING);

        Thread.sleep(100);

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
        RuntimeExecution mockExecution = new RuntimeExecution("antigravity-agy", request);
        java.lang.reflect.Field idField = RuntimeExecution.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockExecution, executionId);
        mockExecution.setStatus(RuntimeStatus.RUNNING);

        when(repository.findById(executionId)).thenReturn(Optional.of(mockExecution));
        when(repository.save(any(RuntimeExecution.class))).thenReturn(mockExecution);

        RuntimeExecution cancelled = runtime.cancel(executionId);

        assertThat(cancelled.getStatus()).isEqualTo(RuntimeStatus.CANCELLED);
        assertThat(cancelled.getLogs()).contains("Execution cancelled by user.");
    }
}
