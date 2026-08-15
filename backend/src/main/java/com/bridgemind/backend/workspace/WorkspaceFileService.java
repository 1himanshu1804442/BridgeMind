package com.bridgemind.backend.workspace;

import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class WorkspaceFileService {
    private static final Logger log = LoggerFactory.getLogger(WorkspaceFileService.class);
    private final WorkspaceFilesystemService workspaceFilesystemService;

    public WorkspaceFileService(WorkspaceFilesystemService workspaceFilesystemService) {
        this.workspaceFilesystemService = workspaceFilesystemService;
    }

    public List<WorkspaceFileNode> listFiles(UUID workspaceId) {
        try {
            Path workspaceRoot = workspaceFilesystemService.provision(workspaceId);
            return buildTree(workspaceRoot, workspaceRoot);
        } catch (IOException e) {
            log.error("Failed to list files for workspace {}", workspaceId, e);
            return Collections.emptyList();
        }
    }

    private List<WorkspaceFileNode> buildTree(Path currentDir, Path workspaceRoot) {
        List<WorkspaceFileNode> nodes = new ArrayList<>();
        try (Stream<Path> stream = Files.list(currentDir)) {
            stream.sorted((p1, p2) -> {
                boolean d1 = Files.isDirectory(p1);
                boolean d2 = Files.isDirectory(p2);
                if (d1 != d2) return d1 ? -1 : 1; // Directories first
                return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
            }).forEach(path -> {
                String fileName = path.getFileName().toString();
                // Skip hidden files/directories except essential config files
                if (fileName.startsWith(".") && !fileName.equals(".gitignore") && !fileName.equals(".env.example")) {
                    return;
                }

                String relativePath = workspaceRoot.relativize(path).toString().replace('\\', '/');
                boolean isDir = Files.isDirectory(path);
                long size = 0;
                String ext = "";

                if (!isDir) {
                    try {
                        size = Files.size(path);
                    } catch (IOException ignored) {}
                    int dotIdx = fileName.lastIndexOf('.');
                    if (dotIdx > 0 && dotIdx < fileName.length() - 1) {
                        ext = fileName.substring(dotIdx + 1).toLowerCase();
                    }
                }

                List<WorkspaceFileNode> children = isDir ? buildTree(path, workspaceRoot) : Collections.emptyList();
                nodes.add(new WorkspaceFileNode(fileName, relativePath, isDir, size, ext, children));
            });
        } catch (IOException e) {
            log.error("Error reading directory: {}", currentDir, e);
        }
        return nodes;
    }

    public String getFileContent(UUID workspaceId, String relativePath) {
        try {
            Path workspaceRoot = workspaceFilesystemService.provision(workspaceId).toAbsolutePath().normalize();
            Path filePath = workspaceRoot.resolve(relativePath).normalize();

            // Strict security check against path traversal
            if (!filePath.startsWith(workspaceRoot)) {
                log.warn("Path traversal attempted in workspace {}: {}", workspaceId, relativePath);
                throw new IllegalArgumentException("Path traversal attempt detected");
            }

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                return "";
            }

            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error reading file content: {} in workspace {}", relativePath, workspaceId, e);
            throw new RuntimeException("Failed to read file content: " + e.getMessage(), e);
        }
    }

    public void saveFileContent(UUID workspaceId, String relativePath, String content) {
        try {
            Path workspaceRoot = workspaceFilesystemService.provision(workspaceId).toAbsolutePath().normalize();
            Path filePath = workspaceRoot.resolve(relativePath).normalize();

            // Strict security check against path traversal
            if (!filePath.startsWith(workspaceRoot)) {
                log.warn("Path traversal attempted on save in workspace {}: {}", workspaceId, relativePath);
                throw new IllegalArgumentException("Path traversal attempt detected");
            }

            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            Files.writeString(filePath, content != null ? content : "", StandardCharsets.UTF_8);
            log.info("Saved file {} in workspace {}", relativePath, workspaceId);
        } catch (IOException e) {
            log.error("Error writing file content: {} in workspace {}", relativePath, workspaceId, e);
            throw new RuntimeException("Failed to write file content: " + e.getMessage(), e);
        }
    }
}
