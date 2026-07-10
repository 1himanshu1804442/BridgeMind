package com.bridgemind.backend.timeline;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Persistent audit-trail record capturing every significant event
 * that occurs within a workspace (mission changes, agent actions, user actions).
 *
 * <p>Entries are immutable once created – they serve as a chronological log.</p>
 */
@Entity
@Table(name = "timeline_entries", indexes = {
        @Index(name = "idx_timeline_workspace_created", columnList = "workspace_id, created_at DESC")
})
public class TimelineEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "actor_name", nullable = false)
    private String actorName;

    /**
     * The type of actor that triggered this event.
     * Expected values: {@code AGENT}, {@code USER}, {@code SYSTEM}.
     */
    @Column(name = "actor_type", nullable = false)
    private String actorType;

    @Column(nullable = false)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TimelineEntry() {
    }

    public TimelineEntry(UUID workspaceId,
                         String eventType,
                         String actorName,
                         String actorType,
                         String summary,
                         String details) {
        this.workspaceId = workspaceId;
        this.eventType = eventType;
        this.actorName = actorName;
        this.actorType = actorType;
        this.summary = summary;
        this.details = details;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActorName() {
        return actorName;
    }

    public String getActorType() {
        return actorType;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetails() {
        return details;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "TimelineEntry{" +
                "id=" + id +
                ", workspaceId=" + workspaceId +
                ", eventType='" + eventType + '\'' +
                ", actorName='" + actorName + '\'' +
                ", actorType='" + actorType + '\'' +
                ", summary='" + summary + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
