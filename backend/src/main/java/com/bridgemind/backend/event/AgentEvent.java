package com.bridgemind.backend.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Application event published when an Agent is spawned, changes status, or is deleted.
 * Consumed by EventBusListener for logging and future WebSocket broadcasting.
 */
public class AgentEvent extends ApplicationEvent {

    private final UUID agentId;
    private final UUID missionId;
    private final String eventType;
    private final String details;
    private final Instant occurredAt;

    public AgentEvent(Object source, UUID agentId, UUID missionId,
                      String eventType, String details) {
        super(source);
        this.agentId = agentId;
        this.missionId = missionId;
        this.eventType = eventType;
        this.details = details;
        this.occurredAt = Instant.now();
    }

    public UUID getAgentId() {
        return agentId;
    }

    public UUID getMissionId() {
        return missionId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDetails() {
        return details;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return "AgentEvent{" +
                "agentId=" + agentId +
                ", missionId=" + missionId +
                ", eventType='" + eventType + '\'' +
                ", details='" + details + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
