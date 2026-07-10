package com.bridgemind.backend.websocket;

import java.time.Instant;
import java.util.Map;

/**
 * Generic WebSocket message envelope sent over STOMP to React clients.
 * Serialised to JSON by Spring's Jackson converter.
 *
 * <p>Example JSON:
 * <pre>
 * {
 *   "type": "MISSION_STATUS_CHANGED",
 *   "payload": { "missionId": "...", "newStatus": "RUNNING" },
 *   "timestamp": "2026-07-10T17:30:00Z"
 * }
 * </pre>
 */
public class WebSocketMessage {

    private String type;
    private Map<String, Object> payload;
    private Instant timestamp;

    public WebSocketMessage() {
    }

    public WebSocketMessage(String type, Map<String, Object> payload, Instant timestamp) {
        this.type = type;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WebSocketMessage{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                ", timestamp=" + timestamp +
                '}';
    }
}
