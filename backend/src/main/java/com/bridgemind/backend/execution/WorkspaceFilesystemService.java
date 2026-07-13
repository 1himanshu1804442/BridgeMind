package com.bridgemind.backend.execution;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class WorkspaceFilesystemService {
    private final DockerExecutionProperties properties;

    public WorkspaceFilesystemService(DockerExecutionProperties properties) {
        this.properties = properties;
    }

    public Path provision(UUID workspaceId) throws IOException {
        Path root = properties.getWorkspaceRoot().toAbsolutePath().normalize();
        Path workspace = root.resolve(workspaceId.toString()).normalize();
        if (!workspace.startsWith(root)) {
            throw new IOException("Workspace path escapes configured root");
        }
        return Files.createDirectories(workspace);
    }
}
