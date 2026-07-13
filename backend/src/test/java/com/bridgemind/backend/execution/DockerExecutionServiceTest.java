package com.bridgemind.backend.execution;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DockerExecutionServiceTest {

    @Test
    void rejectsImagesOutsideTheConfiguredAllowlist() {
        DockerExecutionProperties properties = new DockerExecutionProperties();
        properties.setAllowedImages("alpine:3.20");
        DockerExecutionService service = service(properties);

        ExecutionResult result = service.execute(UUID.randomUUID(), "echo unsafe", "ubuntu:latest");

        assertThat(result.exitCode()).isEqualTo(-1);
        assertThat(result.error()).contains("not allowed");
    }

    @Test
    void rejectsBlankCommandsBeforeStartingDocker() {
        DockerExecutionService service = service(new DockerExecutionProperties());

        ExecutionResult result = service.execute(UUID.randomUUID(), " ", "alpine:3.20");

        assertThat(result.exitCode()).isEqualTo(-1);
        assertThat(result.error()).contains("non-empty command");
    }

    private DockerExecutionService service(DockerExecutionProperties properties) {
        return new DockerExecutionService(properties, new WorkspaceFilesystemService(properties), event -> { });
    }
}
