package com.bridgemind.backend.execution;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DockerExecutionServiceIntegrationTest {

    @Autowired
    private DockerExecutionService dockerExecutionService;

    @Autowired
    private DockerExecutionProperties properties;

    @Test
    public void testExecuteCommand() throws IOException {
        org.junit.jupiter.api.Assumptions.assumeTrue(isDockerAvailable(), "Docker daemon is not running on host; skipping container integration test");

        UUID workspaceId = UUID.randomUUID();
        // Create a temporary directory that simulates the workspace using the configured root
        Path workspaceDir = properties.getWorkspaceRoot().resolve(workspaceId.toString());
        Files.createDirectories(workspaceDir);
        
        try {
            // Write a test file into the workspace
            Files.writeString(workspaceDir.resolve("test.txt"), "hello from test");

            // Execute a command inside the container that reads the file
            ExecutionResult result = dockerExecutionService.execute(workspaceId, "cat /workspace/test.txt", "alpine:3.20");

            if (result.exitCode() != 0 && result.error() != null && (result.error().contains("docker API") || result.error().contains("daemon is running"))) {
                org.junit.jupiter.api.Assumptions.assumeTrue(false, "Docker daemon connection failed: " + result.error());
            }

            assertThat(result.exitCode()).isEqualTo(0);
            assertThat(result.output().trim()).isEqualTo("hello from test");
        } finally {
            // Clean up
            Files.deleteIfExists(workspaceDir.resolve("test.txt"));
            Files.deleteIfExists(workspaceDir);
        }
    }

    private boolean isDockerAvailable() {
        try {
            Process process = new ProcessBuilder("docker", "info").redirectErrorStream(true).start();
            return process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
