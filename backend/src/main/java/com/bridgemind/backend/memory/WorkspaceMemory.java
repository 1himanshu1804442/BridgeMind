package com.bridgemind.backend.memory;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace_memory")
public class WorkspaceMemory {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false) 
    private UUID workspaceId;
    
    @Column(nullable = false) 
    private String memoryKey;
    
    @Lob 
    @Column(columnDefinition = "TEXT") 
    private String memoryValue;
    
    @Column(nullable = false, updatable = false) 
    private Instant createdAt = Instant.now();

    public WorkspaceMemory() {}

    public WorkspaceMemory(UUID workspaceId, String memoryKey, String memoryValue) {
        this.workspaceId = workspaceId;
        this.memoryKey = memoryKey;
        this.memoryValue = memoryValue;
    }

    public UUID getId() { return id; }
    public UUID getWorkspaceId() { return workspaceId; }
    public String getMemoryKey() { return memoryKey; }
    public String getMemoryValue() { return memoryValue; }
    public void setMemoryValue(String memoryValue) { this.memoryValue = memoryValue; }
}
