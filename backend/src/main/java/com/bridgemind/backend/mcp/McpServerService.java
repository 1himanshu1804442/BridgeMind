package com.bridgemind.backend.mcp;

import com.bridgemind.backend.review.GitReviewService;
import com.bridgemind.backend.workspace.WorkspaceFileNode;
import com.bridgemind.backend.workspace.WorkspaceFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class McpServerService {
    private static final Logger log = LoggerFactory.getLogger(McpServerService.class);

    private final WorkspaceFileService workspaceFileService;
    private final GitReviewService gitReviewService;

    public McpServerService(WorkspaceFileService workspaceFileService, GitReviewService gitReviewService) {
        this.workspaceFileService = workspaceFileService;
        this.gitReviewService = gitReviewService;
    }

    public McpResponse handleRequest(McpRequest request) {
        if (request == null || request.method() == null) {
            return McpResponse.error(null, -32600, "Invalid Request: method is missing");
        }

        log.info("MCP Request received: method={}, id={}", request.method(), request.id());

        return switch (request.method()) {
            case "initialize" -> handleInitialize(request);
            case "tools/list" -> handleListTools(request);
            case "tools/call" -> handleCallTool(request);
            case "ping" -> McpResponse.success(request.id(), Map.of("status", "pong"));
            default -> McpResponse.error(request.id(), -32601, "Method not found: " + request.method());
        };
    }

    private McpResponse handleInitialize(McpRequest request) {
        Map<String, Object> serverInfo = Map.of(
                "name", "BridgeMind ADE MCP Server",
                "version", "1.0.0",
                "protocolVersion", "2024-11-05"
        );
        Map<String, Object> capabilities = Map.of(
                "tools", Map.of("listChanged", false),
                "resources", Map.of("subscribe", false),
                "prompts", Map.of("listChanged", false)
        );
        return McpResponse.success(request.id(), Map.of(
                "serverInfo", serverInfo,
                "capabilities", capabilities
        ));
    }

    private McpResponse handleListTools(McpRequest request) {
        List<McpTool> tools = List.of(
                new McpTool(
                        "read_workspace_file",
                        "Reads the UTF-8 text content of a file located within a BridgeMind workspace.",
                        Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "workspaceId", Map.of("type", "string", "description", "UUID of the workspace"),
                                        "path", Map.of("type", "string", "description", "Relative path of the file to read")
                                ),
                                "required", List.of("workspaceId", "path")
                        )
                ),
                new McpTool(
                        "list_workspace_files",
                        "Recursively lists all files and folders in a BridgeMind workspace.",
                        Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "workspaceId", Map.of("type", "string", "description", "UUID of the workspace")
                                ),
                                "required", List.of("workspaceId")
                        )
                ),
                new McpTool(
                        "save_workspace_file",
                        "Writes or modifies a file within a BridgeMind workspace.",
                        Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "workspaceId", Map.of("type", "string", "description", "UUID of the workspace"),
                                        "path", Map.of("type", "string", "description", "Relative path of the file"),
                                        "content", Map.of("type", "string", "description", "Text content to save")
                                ),
                                "required", List.of("workspaceId", "path", "content")
                        )
                ),
                new McpTool(
                        "get_workspace_git_diff",
                        "Retrieves the uncommitted Git diff changes made by the AI agent swarm in the workspace.",
                        Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "workspaceId", Map.of("type", "string", "description", "UUID of the workspace")
                                ),
                                "required", List.of("workspaceId")
                        )
                ),
                new McpTool(
                        "approve_workspace_git_diff",
                        "Approves and commits uncommitted workspace modifications to the Git repository.",
                        Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "workspaceId", Map.of("type", "string", "description", "UUID of the workspace"),
                                        "message", Map.of("type", "string", "description", "Git commit message")
                                ),
                                "required", List.of("workspaceId", "message")
                        )
                )
        );

        return McpResponse.success(request.id(), Map.of("tools", tools));
    }

    private McpResponse handleCallTool(McpRequest request) {
        Map<String, Object> params = request.params() != null ? request.params() : Collections.emptyMap();
        String toolName = (String) params.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) params.getOrDefault("arguments", Collections.emptyMap());

        if (toolName == null || toolName.isBlank()) {
            return McpResponse.error(request.id(), -32602, "Tool name is required");
        }

        try {
            return switch (toolName) {
                case "read_workspace_file" -> {
                    UUID workspaceId = UUID.fromString((String) arguments.get("workspaceId"));
                    String path = (String) arguments.get("path");
                    String content = workspaceFileService.getFileContent(workspaceId, path);
                    yield McpResponse.success(request.id(), Map.of(
                            "content", List.of(Map.of("type", "text", "text", content))
                    ));
                }
                case "list_workspace_files" -> {
                    UUID workspaceId = UUID.fromString((String) arguments.get("workspaceId"));
                    List<WorkspaceFileNode> files = workspaceFileService.listFiles(workspaceId);
                    yield McpResponse.success(request.id(), Map.of(
                            "files", files
                    ));
                }
                case "save_workspace_file" -> {
                    UUID workspaceId = UUID.fromString((String) arguments.get("workspaceId"));
                    String path = (String) arguments.get("path");
                    String content = (String) arguments.get("content");
                    workspaceFileService.saveFileContent(workspaceId, path, content);
                    yield McpResponse.success(request.id(), Map.of(
                            "status", "SAVED",
                            "path", path
                    ));
                }
                case "get_workspace_git_diff" -> {
                    UUID workspaceId = UUID.fromString((String) arguments.get("workspaceId"));
                    String diff = gitReviewService.getDiff(workspaceId);
                    yield McpResponse.success(request.id(), Map.of(
                            "diff", diff
                    ));
                }
                case "approve_workspace_git_diff" -> {
                    UUID workspaceId = UUID.fromString((String) arguments.get("workspaceId"));
                    String message = (String) arguments.getOrDefault("message", "feat: approve MCP agent changes");
                    gitReviewService.approve(workspaceId, UUID.randomUUID(), message);
                    yield McpResponse.success(request.id(), Map.of(
                            "status", "COMMITTED",
                            "message", message
                    ));
                }
                default -> McpResponse.error(request.id(), -32601, "Unknown tool: " + toolName);
            };
        } catch (IllegalArgumentException e) {
            log.warn("Invalid arguments for tool {}: {}", toolName, e.getMessage());
            return McpResponse.error(request.id(), -32602, "Invalid arguments: " + e.getMessage());
        } catch (Exception e) {
            log.error("Execution error calling tool {}", toolName, e);
            return McpResponse.error(request.id(), -32603, "Tool execution failed: " + e.getMessage());
        }
    }
}
