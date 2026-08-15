package com.bridgemind.backend.runtime;

import com.bridgemind.backend.event.RuntimeEvent;
import com.bridgemind.backend.execution.DockerExecutionProperties;
import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CodexRuntime implements AgentRuntime {
    private static final Logger log = LoggerFactory.getLogger(CodexRuntime.class);
    private final RuntimeExecutionRepository repository;
    private final WorkspaceFilesystemService workspaceFilesystemService;
    private final DockerExecutionProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    
    // In a real system, this key would be retrieved from a secure vault or user preferences.
    @Value("${codex.api.key:dummy-api-key}")
    private String apiKey;

    private final ConcurrentHashMap<UUID, Process> activeProcesses = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public CodexRuntime(RuntimeExecutionRepository repository, 
                        WorkspaceFilesystemService workspaceFilesystemService,
                        DockerExecutionProperties properties,
                        ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.workspaceFilesystemService = workspaceFilesystemService;
        this.properties = properties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String id() {
        return "codex-cli";
    }

    @Override
    @Transactional
    public RuntimeExecution start(RuntimeLaunchRequest request) {
        RuntimeExecution execution = new RuntimeExecution(id(), request);
        execution.setStatus(RuntimeStatus.RUNNING);
        execution = repository.save(execution);

        UUID executionId = execution.getId();
        executor.submit(() -> runProcessAsync(executionId, request));
        
        return execution;
    }

    @Override
    @Transactional(readOnly = true)
    public RuntimeExecution status(UUID executionId) {
        return repository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));
    }

    @Override
    @Transactional
    public RuntimeExecution cancel(UUID executionId) {
        RuntimeExecution execution = status(executionId);
        Process process = activeProcesses.get(executionId);
        if (process != null) {
            process.destroyForcibly();
            activeProcesses.remove(executionId);
        }
        if (execution.getStatus() == RuntimeStatus.RUNNING || execution.getStatus() == RuntimeStatus.QUEUED) {
            execution.setStatus(RuntimeStatus.CANCELLED);
            execution.appendLog("Execution cancelled by user.");
            execution = repository.save(execution);
        }
        return execution;
    }

    private void runProcessAsync(UUID executionId, RuntimeLaunchRequest request) {
        try {
            Path workspace = workspaceFilesystemService.provision(request.workspaceId()).toAbsolutePath().normalize();
            String hostWorkspace = workspace.toString().replace('\\', '/');
            
            // Generate request file instead of concatenating to command line
            Path requestFile = workspace.resolve(".codex_request_" + executionId + ".txt");
            Files.writeString(requestFile, request.instruction(), StandardCharsets.UTF_8);

            // We mount a secure temporary file for the credential to avoid passing it via env args
            // that could be inspected on the host via `docker ps`.
            Path credsDir = Files.createTempDirectory("codex_creds_").toAbsolutePath().normalize();
            Path credsFile = credsDir.resolve("api_key");
            String hostCreds = credsFile.toString().replace('\\', '/');
            Files.writeString(credsFile, apiKey, StandardCharsets.UTF_8);

            List<String> dockerCommand = List.of(
                    "docker", "run", "--rm", 
                    "--network", "none", 
                    "--read-only",
                    "--cap-drop", "ALL", 
                    "--security-opt", "no-new-privileges",
                    "--pids-limit", "128", 
                    "--memory", properties.getMemoryLimit(),
                    "--cpus", properties.getCpuLimit(), 
                    "--mount", "type=bind,source=" + hostWorkspace + ",target=/workspace",
                    "--mount", "type=bind,source=" + hostCreds + ",target=/secrets/api_key,readonly",
                    "--env", "OPENAI_API_KEY_PATH=/secrets/api_key",
                    "--workdir", "/workspace", 
                    "codex-cli:latest", "codex", "--file", "/workspace/.codex_request_" + executionId + ".txt"
            );

            ProcessBuilder builder = new ProcessBuilder(dockerCommand).redirectErrorStream(true);
            Process process = builder.start();
            activeProcesses.put(executionId, process);

            StringBuilder logs = new StringBuilder();
            int lineCount = 0;
            final int MAX_LINES = 1000;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (lineCount < MAX_LINES) {
                        logs.append(line).append("\n");
                    } else if (lineCount == MAX_LINES) {
                        logs.append("... (logs truncated)\n");
                    }
                    lineCount++;
                    // Publish bounded runtime-log events for WebSockets
                    eventPublisher.publishEvent(new RuntimeEvent(this, request.workspaceId(), executionId, "RUNTIME_LOG_UPDATED", line));
                }
            }

            boolean finished = process.waitFor(properties.getTimeoutSeconds(), java.util.concurrent.TimeUnit.SECONDS);
            
            // Clean up credentials
            Files.deleteIfExists(credsFile);
            Files.deleteIfExists(credsDir);

            if (!finished) {
                process.destroyForcibly();
                eventPublisher.publishEvent(new RuntimeEvent(this, request.workspaceId(), executionId, "RUNTIME_LOG_UPDATED", "Execution timed out."));
                finishExecution(executionId, RuntimeStatus.FAILED, logs.toString() + "\nExecution timed out.");
            } else {
                int exitCode = process.exitValue();
                RuntimeStatus status = exitCode == 0 ? RuntimeStatus.REVIEW_PENDING : RuntimeStatus.FAILED;
                eventPublisher.publishEvent(new RuntimeEvent(this, request.workspaceId(), executionId, "RUNTIME_LOG_UPDATED", "Process exited with code: " + exitCode));
                finishExecution(executionId, status, logs.toString() + "\nProcess exited with code: " + exitCode);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            eventPublisher.publishEvent(new RuntimeEvent(this, request.workspaceId(), executionId, "RUNTIME_LOG_UPDATED", "Execution thread interrupted."));
            finishExecution(executionId, RuntimeStatus.FAILED, "Execution thread interrupted.");
        } catch (IOException e) {
            log.error("Failed to start docker process", e);
            eventPublisher.publishEvent(new RuntimeEvent(this, request.workspaceId(), executionId, "RUNTIME_LOG_UPDATED", "Failed to start docker: " + e.getMessage()));
            finishExecution(executionId, RuntimeStatus.FAILED, "Failed to start docker: " + e.getMessage());
        } finally {
            activeProcesses.remove(executionId);
        }
    }

    private void finishExecution(UUID executionId, RuntimeStatus finalStatus, String fullLogs) {
        try {
            repository.findById(executionId).ifPresent(execution -> {
                if (execution.getStatus() == RuntimeStatus.RUNNING) {
                    execution.setStatus(finalStatus);
                    execution.appendLog(fullLogs);
                    repository.save(execution);
                }
            });
        } catch (Exception e) {
            log.error("Failed to update final execution state for {}", executionId, e);
        }
    }
}
