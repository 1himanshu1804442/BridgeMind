package com.bridgemind.backend.agent;

import com.bridgemind.backend.mission.Mission;
import com.bridgemind.backend.task.MissionTask;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an AI Agent assigned to a Mission.
 * Each agent has a role, an LLM model, tracks cost/time, and holds its latest output.
 */
@Entity
@Table(name = "agents", indexes = {
        @Index(name = "idx_agents_mission_created", columnList = "mission_id, createdAt ASC"),
        @Index(name = "idx_agents_status", columnList = "status")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentRole role;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status = AgentStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MissionTask task;

    @Column(nullable = false)
    private long costCents = 0L;

    @Column(nullable = false)
    private long elapsedMs = 0L;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lastOutput;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Agent() {
    }

    public Agent(AgentRole role, String displayName, String model, Mission mission) {
        this.role = role;
        this.displayName = displayName;
        this.model = model;
        this.mission = mission;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public AgentRole getRole() {
        return role;
    }

    public void setRole(AgentRole role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public MissionTask getTask() { return task; }
    public void setTask(MissionTask task) { this.task = task; }

    public long getCostCents() {
        return costCents;
    }

    public void setCostCents(long costCents) {
        this.costCents = costCents;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public void setElapsedMs(long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    public String getLastOutput() {
        return lastOutput;
    }

    public void setLastOutput(String lastOutput) {
        this.lastOutput = lastOutput;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
