package com.bridgemind.backend.review;

import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import com.bridgemind.backend.memory.WorkspaceMemory;
import com.bridgemind.backend.memory.WorkspaceMemoryRepository;
import com.bridgemind.backend.runtime.RuntimeExecution;
import com.bridgemind.backend.runtime.RuntimeExecutionRepository;
import com.bridgemind.backend.runtime.RuntimeStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GitReviewService {

    private final WorkspaceFilesystemService filesystemService;
    private final RuntimeExecutionRepository executionRepository;
    private final WorkspaceMemoryRepository memoryRepository;

    public GitReviewService(WorkspaceFilesystemService filesystemService,
                            RuntimeExecutionRepository executionRepository,
                            WorkspaceMemoryRepository memoryRepository) {
        this.filesystemService = filesystemService;
        this.executionRepository = executionRepository;
        this.memoryRepository = memoryRepository;
    }

    public String getDiff(UUID workspaceId) {
        try {
            Path workspacePath = filesystemService.provision(workspaceId);
            ProcessBuilder pb = new ProcessBuilder("git", "diff");
            pb.directory(workspacePath.toFile());
            Process p = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String diff = reader.lines().collect(Collectors.joining("\n"));
                p.waitFor();
                
                // Save diff to memory
                Optional<WorkspaceMemory> mem = memoryRepository.findByWorkspaceIdAndMemoryKey(workspaceId, "latest_diff");
                if (mem.isPresent()) {
                    mem.get().setMemoryValue(diff);
                    memoryRepository.save(mem.get());
                } else {
                    memoryRepository.save(new WorkspaceMemory(workspaceId, "latest_diff", diff));
                }
                
                return diff;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get git diff", e);
        }
    }

    @Transactional
    public void approve(UUID workspaceId, UUID executionId, String commitMessage) {
        try {
            Path workspacePath = filesystemService.provision(workspaceId);
            
            // git add .
            new ProcessBuilder("git", "add", ".").directory(workspacePath.toFile()).start().waitFor();
            
            // git commit -m
            new ProcessBuilder("git", "commit", "-m", commitMessage).directory(workspacePath.toFile()).start().waitFor();
            
            RuntimeExecution execution = executionRepository.findById(executionId)
                    .orElseThrow(() -> new IllegalArgumentException("Execution not found"));
            
            if (execution.getStatus() == RuntimeStatus.REVIEW_PENDING) {
                execution.setStatus(RuntimeStatus.COMPLETED);
                executionRepository.save(execution);
            }
            
            // Clear diff from memory
            memoryRepository.findByWorkspaceIdAndMemoryKey(workspaceId, "latest_diff").ifPresent(mem -> {
                mem.setMemoryValue("");
                memoryRepository.save(mem);
            });
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to approve and commit", e);
        }
    }
}
