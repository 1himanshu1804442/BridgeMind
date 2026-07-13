package com.bridgemind.backend.runtime;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "runtime_executions")
public class RuntimeExecution {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false) private UUID agentId;
    @Column(nullable = false) private UUID missionId;
    @Column(nullable = false) private UUID workspaceId;
    private UUID taskId;
    @Column(nullable = false) private String runtimeId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RuntimeStatus status = RuntimeStatus.QUEUED;
    @Lob @Column(columnDefinition = "TEXT") private String logs = "";
    @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(nullable = false) private Instant updatedAt = Instant.now();

    protected RuntimeExecution() { }
    public RuntimeExecution(String runtimeId, RuntimeLaunchRequest request) {
        this.runtimeId = runtimeId; this.agentId = request.agentId(); this.missionId = request.missionId();
        this.workspaceId = request.workspaceId(); this.taskId = request.taskId();
    }
    @PreUpdate void touch() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public UUID getWorkspaceId() { return workspaceId; }
    public String getRuntimeId() { return runtimeId; }
    public RuntimeStatus getStatus() { return status; }
    public String getLogs() { return logs; }
    public void setStatus(RuntimeStatus status) { this.status = status; }
    public void appendLog(String line) { logs += line + "\n"; }
}
