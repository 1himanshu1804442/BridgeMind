package com.bridgemind.backend.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listens for all HivePilot application events and logs them.
 * Future enhancement: forward events to WebSocket topics for real-time UI updates.
 */
@Component
public class EventBusListener {

    private static final Logger log = LoggerFactory.getLogger(EventBusListener.class);

    @EventListener
    public void handleMissionEvent(MissionEvent event) {
        log.info("[EventBus] MissionEvent received — type={}, missionId={}, workspaceId={}, " +
                        "oldStatus={}, newStatus={}, timestamp={}",
                event.getEventType(),
                event.getMissionId(),
                event.getWorkspaceId(),
                event.getOldStatus(),
                event.getNewStatus(),
                event.getOccurredAt());
    }

    @EventListener
    public void handleAgentEvent(AgentEvent event) {
        log.info("[EventBus] AgentEvent received — type={}, agentId={}, missionId={}, " +
                        "details={}, timestamp={}",
                event.getEventType(),
                event.getAgentId(),
                event.getMissionId(),
                event.getDetails(),
                event.getOccurredAt());
    }
}
