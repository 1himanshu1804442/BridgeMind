package com.bridgemind.backend.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DockerExecutionService {

    private static final Logger log = LoggerFactory.getLogger(DockerExecutionService.class);

    /**
     * Executes a given shell command inside an ephemeral Docker container.
     * The workspace directory is mounted to /workspace inside the container.
     * 
     * @param workspaceId The unique ID of the workspace.
     * @param command     The shell command to execute.
     * @param dockerImage The docker image to use (e.g. "alpine:latest").
     * @return ExecutionResult containing exit code, standard output, and standard error.
     */
    public ExecutionResult execute(UUID workspaceId, String command, String dockerImage) {
        log.info("Starting docker execution for workspaceId: {}, image: {}", workspaceId, dockerImage);
        log.info("Command to execute: {}", command);

        String hostWorkspacePath = "C:/Users/hy180/BridgeMind/workspaces/" + workspaceId;
        String volumeMount = hostWorkspacePath + ":/workspace";

        // Using ProcessBuilder to construct the docker run command safely.
        // We use --rm so the container is automatically removed after execution (ephemeral).
        // The command is passed to "sh -c" so it executes properly as a shell command.
        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "-v", volumeMount,
                dockerImage,
                "sh", "-c", command
        );

        try {
            log.info("Executing process: {}", String.join(" ", processBuilder.command()));
            Process process = processBuilder.start();

            // Read standard output
            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            // Read standard error
            String error;
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                error = errorReader.lines().collect(Collectors.joining("\n"));
            }

            // Wait for the process to complete and get the exit code
            int exitCode = process.waitFor();
            log.info("Docker execution completed with exit code: {}", exitCode);
            
            if (exitCode != 0) {
                log.error("Docker execution failed for workspaceId: {}. Error: {}", workspaceId, error);
            }

            return new ExecutionResult(exitCode, output, error);

        } catch (Exception e) {
            log.error("Exception occurred while trying to execute docker command for workspaceId: {}", workspaceId, e);
            // Return an ExecutionResult with exit code -1 to indicate an internal exception.
            return new ExecutionResult(-1, "", e.getMessage());
        }
    }
}
