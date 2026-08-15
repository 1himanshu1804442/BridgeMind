package com.bridgemind.backend.mission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.bridgemind.backend.workspace.Workspace;
import com.bridgemind.backend.agent.Agent;
import com.bridgemind.backend.task.MissionTask;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "missions", indexes = {
        @Index(name = "idx_missions_workspace_created", columnList = "workspace_id, createdAt DESC"),
        @Index(name = "idx_missions_status", columnList = "status")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status = MissionStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaborationMode collaborationMode = CollaborationMode.COLLABORATIVE;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Agent> agents = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MissionTask> tasks = new ArrayList<>();

    public Mission() {}

    public Mission(String title, Workspace workspace) {
        this.title = title;
        this.workspace = workspace;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public CollaborationMode getCollaborationMode() {
        return collaborationMode;
    }

    public void setCollaborationMode(CollaborationMode collaborationMode) {
        this.collaborationMode = collaborationMode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
