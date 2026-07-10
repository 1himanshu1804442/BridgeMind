package com.bridgemind.backend.websocket;

import com.bridgemind.backend.event.AgentEvent;
import com.bridgemind.backend.event.MissionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bridges the Spring Application Event Bus to WebSocket STOMP topics.
 *
 * <p>Listens for {@link MissionEvent} and {@link AgentEvent}, converts each
 * into a {@link WebSocketMessage}, and publishes to the appropriate topic so
 * that connected React clients receive real-time updates.</p>
 *
 * <p>Topic layout:
 * <ul>
 *   <li>{@code /topic/workspace/{workspaceId}/missions} – mission lifecycle</li>
 *   <li>{@code /topic/mission/{missionId}/agents} – agent status within a mission</li>
 * </ul>
 */
@Component
public class WebSocketEventForwarder {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventForwarder.class);

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventForwarder(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Forwards mission-related events to the workspace-scoped missions topic.
     */
    @EventListener
    public void handleMissionEvent(MissionEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("missionId", event.getMissionId().toString());
        payload.put("workspaceId", event.getWorkspaceId().toString());
        payload.put("eventType", event.getEventType());
        payload.put("oldStatus", event.getOldStatus());
        payload.put("newStatus", event.getNewStatus());

        WebSocketMessage message = new WebSocketMessage(
                event.getEventType(),
                payload,
                Instant.ofEpochMilli(event.getTimestamp())
        );

        String destination = "/topic/workspace/" + event.getWorkspaceId() + "/missions";
        messagingTemplate.convertAndSend(destination, message);
        log.info("Forwarded MissionEvent [{}] to {}", event.getEventType(), destination);
    }

    /**
     * Forwards agent-related events to the mission-scoped agents topic.
     */
    @EventListener
    public void handleAgentEvent(AgentEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("agentId", event.getAgentId().toString());
        payload.put("missionId", event.getMissionId().toString());
        payload.put("eventType", event.getEventType());
        payload.put("details", event.getDetails());

        WebSocketMessage message = new WebSocketMessage(
                event.getEventType(),
                payload,
                Instant.ofEpochMilli(event.getTimestamp())
        );

        String destination = "/topic/mission/" + event.getMissionId() + "/agents";
        messagingTemplate.convertAndSend(destination, message);
        log.info("Forwarded AgentEvent [{}] to {}", event.getEventType(), destination);
    }
}
