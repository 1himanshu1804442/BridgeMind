package com.bridgemind.backend.task;

import com.bridgemind.backend.agent.AgentRole;
import com.bridgemind.backend.mission.Mission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "mission_tasks", indexes = {
        @Index(name = "idx_mission_tasks_mission_created", columnList = "mission_id, createdAt ASC"),
        @Index(name = "idx_mission_tasks_status", columnList = "status")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MissionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    @JsonIgnore
    private Mission mission;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentRole assignedRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "mission_task_dependencies",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "depends_on_task_id"))
    private Set<MissionTask> dependencies = new LinkedHashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Version
    private long version;

    protected MissionTask() {
    }

    public MissionTask(Mission mission, String title, AgentRole assignedRole) {
        this.mission = mission;
        this.title = title;
        this.assignedRole = assignedRole;
    }

    public UUID getId() { return id; }
    public Mission getMission() { return mission; }
    public String getTitle() { return title; }
    public AgentRole getAssignedRole() { return assignedRole; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public Set<MissionTask> getDependencies() { return dependencies; }
    public void addDependency(MissionTask dependency) { dependencies.add(dependency); }
    public Instant getCreatedAt() { return createdAt; }
}
