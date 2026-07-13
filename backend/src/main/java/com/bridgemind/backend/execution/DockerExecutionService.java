package com.bridgemind.backend.execution;

import com.bridgemind.backend.event.ExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerExecutionService {
    private static final Logger log = LoggerFactory.getLogger(DockerExecutionService.class);
    private final DockerExecutionProperties properties;
    private final WorkspaceFilesystemService workspaceFilesystemService;
    private final ApplicationEventPublisher eventPublisher;

    public DockerExecutionService(DockerExecutionProperties properties, WorkspaceFilesystemService workspaceFilesystemService,
                                  ApplicationEventPublisher eventPublisher) {
        this.properties = properties;
        this.workspaceFilesystemService = workspaceFilesystemService;
        this.eventPublisher = eventPublisher;
    }

    public ExecutionResult execute(UUID workspaceId, String command, String dockerImage) {
        if (command == null || command.isBlank() || command.indexOf('\u0000') >= 0) {
            return new ExecutionResult(-1, "", "A non-empty command is required");
        }
        if (!properties.allowedImages().contains(dockerImage)) {
            return new ExecutionResult(-1, "", "Docker image is not allowed: " + dockerImage);
        }

        try {
            eventPublisher.publishEvent(new ExecutionEvent(this, workspaceId, "EXECUTION_STARTED", dockerImage));
            Path workspace = workspaceFilesystemService.provision(workspaceId);
            List<String> dockerCommand = List.of(
                    "docker", "run", "--rm", "--network", "none", "--read-only",
                    "--cap-drop", "ALL", "--security-opt", "no-new-privileges",
                    "--pids-limit", "128", "--memory", properties.getMemoryLimit(),
                    "--cpus", properties.getCpuLimit(), "--volume", workspace + ":/workspace:rw",
                    "--workdir", "/workspace", dockerImage, "sh", "-c", command
            );
            // Do NOT use redirectErrorStream(true) — Docker pull messages go to
            // stderr and would pollute the actual command stdout if merged.
            ProcessBuilder builder = new ProcessBuilder(dockerCommand);
            Process process = builder.start();
            boolean finished = process.waitFor(properties.getTimeoutSeconds(), TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new ExecutionResult(-1, "", "Execution timed out after " + properties.getTimeoutSeconds() + " seconds");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            ExecutionResult result = new ExecutionResult(process.exitValue(), output, stderr);
            eventPublisher.publishEvent(new ExecutionEvent(this, workspaceId, "EXECUTION_COMPLETED", "Exit code: " + result.exitCode()));
            return result;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new ExecutionResult(-1, "", "Execution was interrupted");
        } catch (IOException exception) {
            log.error("Unable to run Docker for workspace {}", workspaceId, exception);
            return new ExecutionResult(-1, "", "Docker execution could not start: " + exception.getMessage());
        }
    }

}
