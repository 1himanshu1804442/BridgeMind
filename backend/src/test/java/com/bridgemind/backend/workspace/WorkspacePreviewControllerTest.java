package com.bridgemind.backend.workspace;

import com.bridgemind.backend.security.JwtAuthenticationFilter;
import com.bridgemind.backend.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkspacePreviewController.class)
@Import(SecurityConfig.class)
@DisplayName("WorkspacePreviewController Integration Tests")
public class WorkspacePreviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkspacePreviewService previewService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("GET /api/workspaces/{id}/preview — returns default HTML when empty")
    void testGetDefaultPreviewHtml() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        byte[] mockHtml = "<html><body><h1>Space Runner</h1></body></html>".getBytes(StandardCharsets.UTF_8);

        when(previewService.getPreviewFile(eq(workspaceId), eq("index.html")))
                .thenReturn(new WorkspacePreviewService.PreviewResource(mockHtml, "text/html;charset=UTF-8", HttpStatus.OK));

        mockMvc.perform(get("/api/workspaces/" + workspaceId + "/preview"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/html;charset=UTF-8"))
                .andExpect(content().string("<html><body><h1>Space Runner</h1></body></html>"));
    }

    @Test
    @DisplayName("GET /api/workspaces/{id}/preview/game.js — returns javascript content")
    void testGetPreviewGameJs() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        byte[] mockJs = "console.log('game running');".getBytes(StandardCharsets.UTF_8);

        when(previewService.getPreviewFile(eq(workspaceId), eq("game.js")))
                .thenReturn(new WorkspacePreviewService.PreviewResource(mockJs, "application/javascript;charset=UTF-8", HttpStatus.OK));

        mockMvc.perform(get("/api/workspaces/" + workspaceId + "/preview/game.js"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/javascript;charset=UTF-8"))
                .andExpect(content().string("console.log('game running');"));
    }

    @Test
    @DisplayName("GET /api/workspaces/{id}/preview/style.css — returns css stylesheet")
    void testGetPreviewStyleCss() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        byte[] mockCss = "body { margin: 0; background: #000; }".getBytes(StandardCharsets.UTF_8);

        when(previewService.getPreviewFile(eq(workspaceId), eq("style.css")))
                .thenReturn(new WorkspacePreviewService.PreviewResource(mockCss, "text/css;charset=UTF-8", HttpStatus.OK));

        mockMvc.perform(get("/api/workspaces/" + workspaceId + "/preview/style.css"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/css;charset=UTF-8"))
                .andExpect(content().string("body { margin: 0; background: #000; }"));
    }

    @Test
    @DisplayName("GET /api/workspaces/{id}/preview/missing.png — returns 404 when not found")
    void testGetMissingFile() throws Exception {
        UUID workspaceId = UUID.randomUUID();

        when(previewService.getPreviewFile(eq(workspaceId), eq("missing.png")))
                .thenReturn(new WorkspacePreviewService.PreviewResource(new byte[0], "text/plain", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/workspaces/" + workspaceId + "/preview/missing.png"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/workspaces/{id}/preview/../../secret — rejects directory traversal")
    void testDirectoryTraversalRejection() throws Exception {
        UUID workspaceId = UUID.randomUUID();

        when(previewService.getPreviewFile(eq(workspaceId), eq("../../secret")))
                .thenThrow(new IllegalArgumentException("Invalid subpath — directory traversal forbidden"));

        mockMvc.perform(get("/api/workspaces/" + workspaceId + "/preview/../../secret"))
                .andExpect(status().isBadRequest());
    }
}
