package com.bridgemind.backend.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public class ExecutionEvent extends ApplicationEvent {
    private final UUID workspaceId;
    private final String eventType;
    private final String details;
    private final Instant occurredAt = Instant.now();

    public ExecutionEvent(Object source, UUID workspaceId, String eventType, String details) {
        super(source);
        this.workspaceId = workspaceId;
        this.eventType = eventType;
        this.details = details;
    }

    public UUID getWorkspaceId() { return workspaceId; }
    public String getEventType() { return eventType; }
    public String getDetails() { return details; }
    public Instant getOccurredAt() { return occurredAt; }
}
