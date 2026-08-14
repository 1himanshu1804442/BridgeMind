package com.bridgemind.backend.workspace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
public class WorkspaceIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @BeforeEach
    void setUp() {
        workspaceRepository.deleteAll();
    }

    @Test
    void shouldCreateWorkspace() throws Exception {
        String json = """
                {
                    "name": "Project Alpha"
                }
                """;

        mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Project Alpha"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldListWorkspaces() throws Exception {
        workspaceRepository.save(new Workspace("Workspace 1"));
        workspaceRepository.save(new Workspace("Workspace 2"));

        mockMvc.perform(get("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Workspace 1", "Workspace 2")));
    }

    @Test
    void shouldGetWorkspaceById() throws Exception {
        Workspace workspace = workspaceRepository.save(new Workspace("My Special Workspace"));

        mockMvc.perform(get("/api/workspaces/" + workspace.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workspace.getId().toString()))
                .andExpect(jsonPath("$.name").value("My Special Workspace"));
    }

    @Test
    void shouldReturn404WhenWorkspaceNotFound() throws Exception {
        mockMvc.perform(get("/api/workspaces/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldDeleteWorkspace() throws Exception {
        Workspace workspace = workspaceRepository.save(new Workspace("Workspace to delete"));

        mockMvc.perform(delete("/api/workspaces/" + workspace.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify it was deleted by checking it returns 404
        mockMvc.perform(get("/api/workspaces/" + workspace.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldServeLivePreviewWithoutAuthentication() throws Exception {
        Workspace workspace = workspaceRepository.save(new Workspace("Space Runner Mission"));

        // GET preview endpoint should succeed with 200 and return default playable HTML5 space runner
        mockMvc.perform(get("/api/workspaces/" + workspace.getId() + "/preview"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("SPACE RUNNER")));
    }

    @Test
    void shouldServePreviewGameJsWithoutAuthentication() throws Exception {
        Workspace workspace = workspaceRepository.save(new Workspace("Space Runner Mission 2"));

        mockMvc.perform(get("/api/workspaces/" + workspace.getId() + "/preview/game.js"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/javascript;charset=UTF-8"))
                .andExpect(content().string(containsString("SoundEngine")));
    }
}
