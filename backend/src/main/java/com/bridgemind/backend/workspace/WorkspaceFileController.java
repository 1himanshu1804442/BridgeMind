package com.bridgemind.backend.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/files")
public class WorkspaceFileController {
    private static final Logger log = LoggerFactory.getLogger(WorkspaceFileController.class);
    private final WorkspaceFileService fileService;

    public WorkspaceFileController(WorkspaceFileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceFileNode>> listFiles(@PathVariable UUID workspaceId) {
        log.info("Listing files for workspace: {}", workspaceId);
        List<WorkspaceFileNode> files = fileService.listFiles(workspaceId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/content")
    public ResponseEntity<Map<String, String>> getFileContent(
            @PathVariable UUID workspaceId,
            @RequestParam("path") String relativePath) {
        log.info("Reading file content for workspace: {}, path: {}", workspaceId, relativePath);
        String content = fileService.getFileContent(workspaceId, relativePath);
        return ResponseEntity.ok(Map.of("path", relativePath, "content", content));
    }

    @PutMapping("/content")
    public ResponseEntity<?> saveFileContent(
            @PathVariable UUID workspaceId,
            @RequestBody Map<String, String> body) {
        String path = body.get("path");
        String content = body.get("content");
        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Path is required"));
        }
        log.info("Saving file content for workspace: {}, path: {}", workspaceId, path);
        fileService.saveFileContent(workspaceId, path, content);
        return ResponseEntity.ok(Map.of("status", "SAVED", "path", path));
    }
}
