package com.bridgemind.backend.execution;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class DockerExecutionServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private DockerExecutionService dockerExecutionService;

    @Autowired
    private DockerExecutionProperties properties;

    @Test
    public void testExecuteCommand() throws IOException {
        UUID workspaceId = UUID.randomUUID();
        // Create a temporary directory that simulates the workspace using the configured root
        Path workspaceDir = properties.getWorkspaceRoot().resolve(workspaceId.toString());
        Files.createDirectories(workspaceDir);
        
        try {
            // Write a test file into the workspace
            Files.writeString(workspaceDir.resolve("test.txt"), "hello from test");

            // Execute a command inside the container that reads the file
            ExecutionResult result = dockerExecutionService.execute(workspaceId, "cat /workspace/test.txt", "alpine:latest");

            assertThat(result.exitCode()).isEqualTo(0);
            assertThat(result.output().trim()).isEqualTo("hello from test");
        } finally {
            // Clean up
            Files.deleteIfExists(workspaceDir.resolve("test.txt"));
            Files.deleteIfExists(workspaceDir);
        }
    }
}
