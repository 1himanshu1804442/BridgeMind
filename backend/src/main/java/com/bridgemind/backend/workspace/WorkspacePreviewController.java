package com.bridgemind.backend.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Controller for Live Workspace Previews.
 * Serves live HTML5 applications, CSS, JavaScript, and assets directly from workspace directories.
 * If no files exist in a workspace yet, generates and returns a complete playable HTML5 Space Runner arcade game.
 */
@RestController
@RequestMapping("/api/workspaces")
public class WorkspacePreviewController {

    private static final Logger log = LoggerFactory.getLogger(WorkspacePreviewController.class);

    private final WorkspacePreviewService previewService;

    public WorkspacePreviewController(WorkspacePreviewService previewService) {
        this.previewService = previewService;
    }

    @GetMapping(value = {"/{workspaceId}/preview", "/{workspaceId}/preview/{*subpath}"})
    public ResponseEntity<byte[]> getWorkspacePreview(
            @PathVariable UUID workspaceId,
            @PathVariable(required = false) String subpath) {
        try {
            String path = (subpath == null || subpath.isBlank()) ? "index.html" : subpath;
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.isEmpty()) {
                path = "index.html";
            }

            log.info("Preview request received for workspace: {}, subpath: '{}'", workspaceId, path);

            WorkspacePreviewService.PreviewResource resource = previewService.getPreviewFile(workspaceId, path);

            return ResponseEntity.status(resource.status())
                    .header(HttpHeaders.CONTENT_TYPE, resource.contentType())
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(resource.content());

        } catch (IllegalArgumentException e) {
            log.error("Invalid preview request for workspace {}: {}", workspaceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Failed to read preview file for workspace {}", workspaceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
                    .body(("Internal error reading workspace preview: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}
