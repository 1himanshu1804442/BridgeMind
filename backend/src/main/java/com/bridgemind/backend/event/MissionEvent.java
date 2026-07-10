package com.bridgemind.backend.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Application event published when a Mission is created or its status changes.
 * Consumed by EventBusListener for logging and future WebSocket broadcasting.
 */
public class MissionEvent extends ApplicationEvent {

    private final UUID missionId;
    private final UUID workspaceId;
    private final String eventType;
    private final String oldStatus;
    private final String newStatus;
    private final Instant occurredAt;

    public MissionEvent(Object source, UUID missionId, UUID workspaceId,
                        String eventType, String oldStatus, String newStatus) {
        super(source);
        this.missionId = missionId;
        this.workspaceId = workspaceId;
        this.eventType = eventType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.occurredAt = Instant.now();
    }

    public UUID getMissionId() {
        return missionId;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return "MissionEvent{" +
                "missionId=" + missionId +
                ", workspaceId=" + workspaceId +
                ", eventType='" + eventType + '\'' +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
