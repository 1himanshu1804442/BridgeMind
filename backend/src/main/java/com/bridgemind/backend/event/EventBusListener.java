package com.bridgemind.backend.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Listens for all HivePilot application events and forwards them to WebSocket topics.
 */
@Component
public class EventBusListener {

    private static final Logger log = LoggerFactory.getLogger(EventBusListener.class);
    
    private final SimpMessagingTemplate messagingTemplate;

    public EventBusListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleMissionEvent(MissionEvent event) {
        log.info("[EventBus] Forwarding MissionEvent to WebSocket: type={}, missionId={}",
                event.getEventType(), event.getMissionId());
        
        // Broadcast to workspace topic
        messagingTemplate.convertAndSend(
                "/topic/workspace/" + event.getWorkspaceId() + "/missions",
                event
        );
    }

    @EventListener
    public void handleAgentEvent(AgentEvent event) {
        // We log at debug level to avoid spamming the console with fast-streaming text
        log.debug("[EventBus] Forwarding AgentEvent to WebSocket: type={}, agentId={}",
                event.getEventType(), event.getAgentId());
                
        // Broadcast to mission topic
        messagingTemplate.convertAndSend(
                "/topic/mission/" + event.getMissionId() + "/agents",
                event
        );
    }
}
