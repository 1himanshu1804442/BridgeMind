package com.bridgemind.backend.workspace;

import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

/**
 * Service responsible for reading and serving workspace files for live iframe preview.
 * Ensures security against path traversal and synthesizes default playable game files
 * if a workspace is newly initialized or empty.
 */
@Service
public class WorkspacePreviewService {

    private static final Logger log = LoggerFactory.getLogger(WorkspacePreviewService.class);

    private final WorkspaceFilesystemService filesystemService;
    private final WorkspaceRepository workspaceRepository;
    private final com.bridgemind.backend.mission.MissionRepository missionRepository;

    public record PreviewResource(byte[] content, String contentType, HttpStatus status) {}

    public WorkspacePreviewService(WorkspaceFilesystemService filesystemService,
                                   WorkspaceRepository workspaceRepository,
                                   com.bridgemind.backend.mission.MissionRepository missionRepository) {
        this.filesystemService = filesystemService;
        this.workspaceRepository = workspaceRepository;
        this.missionRepository = missionRepository;
    }

    /**
     * Reads a file from the given workspace or synthesizes default playable game files if missing.
     *
     * @param workspaceId ID of the target workspace
     * @param subpath Relative path requested (e.g. index.html, game.js, style.css)
     * @return PreviewResource with byte content and MIME type
     * @throws IOException on filesystem error
     */
    public PreviewResource getPreviewFile(UUID workspaceId, String subpath) throws IOException {
        log.info("Serving preview for workspace {} path: '{}'", workspaceId, subpath);

        Path workspaceDir = filesystemService.provision(workspaceId);

        String normalizedSubpath = (subpath == null || subpath.isBlank() || subpath.equals("/"))
                ? "index.html"
                : subpath.trim();

        if (normalizedSubpath.startsWith("/")) {
            normalizedSubpath = normalizedSubpath.substring(1);
        }

        Path target = workspaceDir.resolve(normalizedSubpath).normalize();

        // Path traversal defense
        if (!target.startsWith(workspaceDir)) {
            log.warn("Path traversal attempt detected for workspace {}: {}", workspaceId, subpath);
            throw new IllegalArgumentException("Invalid subpath — directory traversal forbidden");
        }

        // If target file exists, read and return it
        if (Files.exists(target) && Files.isRegularFile(target)) {
            byte[] bytes = Files.readAllBytes(target);
            String contentType = determineContentType(target.getFileName().toString());
            return new PreviewResource(bytes, contentType, HttpStatus.OK);
        }

        // Target file does not exist. Check if default playable files should be synthesized
        String fileName = target.getFileName() != null ? target.getFileName().toString() : "";
        String resolvedTitle = resolveMissionTitleForWorkspace(workspaceId);

        if (fileName.equalsIgnoreCase("index.html") || normalizedSubpath.isEmpty()) {
            synthesizeDefaultGameFiles(workspaceId, resolvedTitle);
            Path indexPath = workspaceDir.resolve("index.html");
            byte[] htmlBytes = Files.exists(indexPath) 
                    ? Files.readAllBytes(indexPath)
                    : WorkspaceGameTemplate.getHtmlContent(resolvedTitle).getBytes(StandardCharsets.UTF_8);
            return new PreviewResource(htmlBytes, "text/html;charset=UTF-8", HttpStatus.OK);
        }

        if (fileName.equalsIgnoreCase("game.js")) {
            synthesizeDefaultGameFiles(workspaceId, resolvedTitle);
            Path jsPath = workspaceDir.resolve("game.js");
            byte[] jsBytes = Files.exists(jsPath)
                    ? Files.readAllBytes(jsPath)
                    : WorkspaceGameTemplate.getJsContent(resolvedTitle).getBytes(StandardCharsets.UTF_8);
            return new PreviewResource(jsBytes, "application/javascript;charset=UTF-8", HttpStatus.OK);
        }

        if (fileName.equalsIgnoreCase("style.css")) {
            synthesizeDefaultGameFiles(workspaceId, resolvedTitle);
            Path cssPath = workspaceDir.resolve("style.css");
            byte[] cssBytes = Files.exists(cssPath)
                    ? Files.readAllBytes(cssPath)
                    : WorkspaceGameTemplate.getCssContent(resolvedTitle).getBytes(StandardCharsets.UTF_8);
            return new PreviewResource(cssBytes, "text/css;charset=UTF-8", HttpStatus.OK);
        }

        log.warn("Requested file '{}' not found in workspace {}", subpath, workspaceId);
        return new PreviewResource(new byte[0], "text/plain", HttpStatus.NOT_FOUND);
    }

    /**
     * Synthesizes actual functional code files (index.html, game.js, style.css) into the workspace directory.
     *
     * @param workspaceId ID of the workspace
     * @param missionTitle Title or context for the game synthesis
     * @throws IOException on filesystem error
     */
    public void synthesizeDefaultGameFiles(UUID workspaceId, String missionTitle) throws IOException {
        Path workspaceDir = filesystemService.provision(workspaceId);

        Path indexPath = workspaceDir.resolve("index.html");
        Path jsPath = workspaceDir.resolve("game.js");
        Path cssPath = workspaceDir.resolve("style.css");

        Files.writeString(indexPath, WorkspaceGameTemplate.getHtmlContent(missionTitle), StandardCharsets.UTF_8);
        Files.writeString(jsPath, WorkspaceGameTemplate.getJsContent(missionTitle), StandardCharsets.UTF_8);
        Files.writeString(cssPath, WorkspaceGameTemplate.getCssContent(missionTitle), StandardCharsets.UTF_8);

        log.info("Synthesized live playable files for '{}' in workspace: {}", missionTitle, workspaceId);
    }

    /**
     * Overwrites or synthesizes fresh application files tailored to mission prompt.
     */
    public void writeApplicationFiles(UUID workspaceId, String title, String html, String js, String css) throws IOException {
        Path workspaceDir = filesystemService.provision(workspaceId);

        Path indexPath = workspaceDir.resolve("index.html");
        Path jsPath = workspaceDir.resolve("game.js");
        Path cssPath = workspaceDir.resolve("style.css");

        Files.writeString(indexPath, (html != null && !html.isBlank()) ? html : WorkspaceGameTemplate.getHtmlContent(title), StandardCharsets.UTF_8);
        Files.writeString(jsPath, (js != null && !js.isBlank()) ? js : WorkspaceGameTemplate.getJsContent(), StandardCharsets.UTF_8);
        Files.writeString(cssPath, (css != null && !css.isBlank()) ? css : WorkspaceGameTemplate.getCssContent(), StandardCharsets.UTF_8);

        log.info("Wrote application files into workspace: {}", workspaceId);
    }

    public static String determineContentType(String filename) {
        if (filename == null) return "application/octet-stream";
        String lower = filename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html;charset=UTF-8";
        if (lower.endsWith(".js") || lower.endsWith(".mjs")) return "application/javascript;charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css;charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json;charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ico")) return "image/x-icon";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".wasm")) return "application/wasm";
        if (lower.endsWith(".txt") || lower.endsWith(".md")) return "text/plain;charset=UTF-8";
        return "application/octet-stream";
    }

    private String resolveMissionTitleForWorkspace(UUID workspaceId) {
        if (workspaceId == null || missionRepository == null) {
            return "Roguelike Word-Spell Deckbuilder";
        }
        try {
            java.util.List<com.bridgemind.backend.mission.Mission> missions = missionRepository.findByWorkspaceId(workspaceId);
            if (!missions.isEmpty()) {
                return missions.get(missions.size() - 1).getTitle();
            }
        } catch (Exception ignored) {}
        return "Roguelike Word-Spell Deckbuilder";
    }
}
