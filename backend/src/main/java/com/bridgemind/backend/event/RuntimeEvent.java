package com.bridgemind.backend.event;
import org.springframework.context.ApplicationEvent;
import java.time.Instant;
import java.util.UUID;
public class RuntimeEvent extends ApplicationEvent {
    private final UUID workspaceId; private final UUID executionId; private final String eventType; private final String details; private final Instant occurredAt = Instant.now();
    public RuntimeEvent(Object source, UUID workspaceId, UUID executionId, String eventType, String details) { super(source); this.workspaceId=workspaceId; this.executionId=executionId; this.eventType=eventType; this.details=details; }
    public UUID getWorkspaceId(){return workspaceId;} public UUID getExecutionId(){return executionId;} public String getEventType(){return eventType;} public String getDetails(){return details;} public Instant getOccurredAt(){return occurredAt;}
}
