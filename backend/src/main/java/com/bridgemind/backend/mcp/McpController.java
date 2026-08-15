package com.bridgemind.backend.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/mcp")
public class McpController {
    private static final Logger log = LoggerFactory.getLogger(McpController.class);
    private final McpServerService mcpServerService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public McpController(McpServerService mcpServerService) {
        this.mcpServerService = mcpServerService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<McpResponse> handleMcpJsonRpc(@RequestBody McpRequest request) {
        McpResponse response = mcpServerService.handleRequest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "BridgeMind ADE Model Context Protocol Server",
                "status", "ACTIVE",
                "version", "1.0.0",
                "endpoint", "/api/mcp",
                "sseEndpoint", "/api/mcp/sse"
        ));
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSse(@RequestParam(value = "clientId", required = false) String clientId) {
        String id = clientId != null ? clientId : java.util.UUID.randomUUID().toString();
        log.info("MCP Client connected to SSE stream: {}", id);

        SseEmitter emitter = new SseEmitter(300_000L); // 5 min timeout
        emitters.put(id, emitter);

        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError((e) -> emitters.remove(id));

        try {
            emitter.send(SseEmitter.event()
                    .name("endpoint")
                    .data("/api/mcp?clientId=" + id));
        } catch (IOException e) {
            log.error("Failed to send initial MCP SSE event to {}", id, e);
        }

        return emitter;
    }
}
