package com.bridgemind.backend.mcp;

import com.bridgemind.backend.review.GitReviewService;
import com.bridgemind.backend.workspace.WorkspaceFileNode;
import com.bridgemind.backend.workspace.WorkspaceFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class McpServerServiceTest {

    private WorkspaceFileService fileService;
    private GitReviewService gitReviewService;
    private McpServerService mcpServerService;
    private UUID workspaceId;

    @BeforeEach
    void setUp() {
        fileService = Mockito.mock(WorkspaceFileService.class);
        gitReviewService = Mockito.mock(GitReviewService.class);
        mcpServerService = new McpServerService(fileService, gitReviewService);
        workspaceId = UUID.randomUUID();
    }

    @Test
    void testInitialize() {
        McpRequest req = new McpRequest("2.0", 1, "initialize", Map.of());
        McpResponse resp = mcpServerService.handleRequest(req);

        assertThat(resp.error()).isNull();
        assertThat(resp.result()).isNotNull();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) resp.result();
        assertThat(map).containsKey("serverInfo");
    }

    @Test
    void testListTools() {
        McpRequest req = new McpRequest("2.0", "tools-req", "tools/list", Map.of());
        McpResponse resp = mcpServerService.handleRequest(req);

        assertThat(resp.error()).isNull();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) resp.result();
        @SuppressWarnings("unchecked")
        List<McpTool> tools = (List<McpTool>) map.get("tools");
        assertThat(tools).isNotEmpty();
        assertThat(tools.stream().anyMatch(t -> t.name().equals("read_workspace_file"))).isTrue();
    }

    @Test
    void testCallTool_ReadWorkspaceFile() {
        when(fileService.getFileContent(workspaceId, "src/index.ts")).thenReturn("console.log('Hello');");

        McpRequest req = new McpRequest("2.0", 42, "tools/call", Map.of(
                "name", "read_workspace_file",
                "arguments", Map.of(
                        "workspaceId", workspaceId.toString(),
                        "path", "src/index.ts"
                )
        ));

        McpResponse resp = mcpServerService.handleRequest(req);

        assertThat(resp.error()).isNull();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) resp.result();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) map.get("content");
        assertThat(content.get(0).get("text")).isEqualTo("console.log('Hello');");
    }
}
