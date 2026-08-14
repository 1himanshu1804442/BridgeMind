package com.bridgemind.backend.review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GitReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GitReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitReviewService gitReviewService;

    @MockBean
    private com.bridgemind.backend.security.JwtService jwtService;

    @MockBean
    private com.bridgemind.backend.security.UserService userService;

    @Test
    void testGetDiff() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        when(gitReviewService.getDiff(workspaceId)).thenReturn("mock diff");

        mockMvc.perform(get("/api/workspaces/" + workspaceId + "/review/diff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diff").value("mock diff"));
    }

    @Test
    void testApproveCommit() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();

        mockMvc.perform(post("/api/workspaces/" + workspaceId + "/review/approve")
                        .param("executionId", executionId.toString())
                        .param("message", "Test commit"))
                .andExpect(status().isOk());

        verify(gitReviewService).approve(workspaceId, executionId, "Test commit");
    }
}
