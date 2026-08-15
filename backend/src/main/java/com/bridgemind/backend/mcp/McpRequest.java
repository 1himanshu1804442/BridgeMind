package com.bridgemind.backend.mcp;

import java.util.Map;

public record McpRequest(
        String jsonrpc,
        Object id,
        String method,
        Map<String, Object> params
) {
    public McpRequest {
        if (jsonrpc == null) jsonrpc = "2.0";
    }
}
