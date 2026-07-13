package com.bridgemind.backend.review;

import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import com.bridgemind.backend.memory.WorkspaceMemory;
import com.bridgemind.backend.memory.WorkspaceMemoryRepository;
import com.bridgemind.backend.runtime.RuntimeExecution;
import com.bridgemind.backend.runtime.RuntimeExecutionRepository;
import com.bridgemind.backend.runtime.RuntimeLaunchRequest;
import com.bridgemind.backend.runtime.RuntimeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitReviewServiceTest {

    @Mock private WorkspaceFilesystemService filesystemService;
    @Mock private RuntimeExecutionRepository executionRepository;
    @Mock private WorkspaceMemoryRepository memoryRepository;

    private GitReviewService service;

    @BeforeEach
    void setUp() {
        service = new GitReviewService(filesystemService, executionRepository, memoryRepository);
    }

    @Test
    void testGetDiff() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        Path fakeWorkspace = Files.createTempDirectory("workspace_");
        
        ProcessBuilder pb = new ProcessBuilder("git", "init");
        pb.directory(fakeWorkspace.toFile());
        pb.start().waitFor();
        
        new ProcessBuilder("git", "config", "user.email", "test@test.com").directory(fakeWorkspace.toFile()).start().waitFor();
        new ProcessBuilder("git", "config", "user.name", "Test").directory(fakeWorkspace.toFile()).start().waitFor();

        Files.writeString(fakeWorkspace.resolve("test.txt"), "A");
        new ProcessBuilder("git", "add", ".").directory(fakeWorkspace.toFile()).start().waitFor();
        new ProcessBuilder("git", "commit", "-m", "init").directory(fakeWorkspace.toFile()).start().waitFor();

        Files.writeString(fakeWorkspace.resolve("test.txt"), "B");

        when(filesystemService.provision(workspaceId)).thenReturn(fakeWorkspace);
        when(memoryRepository.findByWorkspaceIdAndMemoryKey(workspaceId, "latest_diff")).thenReturn(Optional.empty());

        String diff = service.getDiff(workspaceId);
        
        assertThat(diff).contains("-A");
        assertThat(diff).contains("+B");
        verify(memoryRepository).save(any(WorkspaceMemory.class));
    }

    @Test
    void testApprove() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        Path fakeWorkspace = Files.createTempDirectory("workspace_");
        
        ProcessBuilder pb = new ProcessBuilder("git", "init");
        pb.directory(fakeWorkspace.toFile());
        pb.start().waitFor();
        new ProcessBuilder("git", "config", "user.email", "test@test.com").directory(fakeWorkspace.toFile()).start().waitFor();
        new ProcessBuilder("git", "config", "user.name", "Test").directory(fakeWorkspace.toFile()).start().waitFor();

        Files.writeString(fakeWorkspace.resolve("test.txt"), "A");

        when(filesystemService.provision(workspaceId)).thenReturn(fakeWorkspace);
        
        RuntimeExecution execution = mock(RuntimeExecution.class);
        when(execution.getStatus()).thenReturn(RuntimeStatus.REVIEW_PENDING);
        
        when(executionRepository.findById(executionId)).thenReturn(Optional.of(execution));
        
        service.approve(workspaceId, executionId, "test commit message");
        
        verify(execution).setStatus(RuntimeStatus.COMPLETED);
        verify(executionRepository).save(execution);
    }
}
