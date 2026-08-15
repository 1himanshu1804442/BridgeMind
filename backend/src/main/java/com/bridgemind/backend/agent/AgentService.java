package com.bridgemind.backend.agent;

import com.bridgemind.backend.event.AgentEvent;
import com.bridgemind.backend.mission.Mission;
import com.bridgemind.backend.mission.MissionNotFoundException;
import com.bridgemind.backend.mission.MissionRepository;
import com.bridgemind.backend.task.MissionTask;
import com.bridgemind.backend.task.MissionTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Business logic for Agent lifecycle management.
 * Validates mission existence, publishes AgentEvents on spawn, status change, and output update.
 */
@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final AgentRepository agentRepository;
    private final MissionRepository missionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MissionTaskService taskService;

    public AgentService(AgentRepository agentRepository,
                        MissionRepository missionRepository,
                        ApplicationEventPublisher eventPublisher,
                        MissionTaskService taskService) {
        this.agentRepository = agentRepository;
        this.missionRepository = missionRepository;
        this.eventPublisher = eventPublisher;
        this.taskService = taskService;
    }

    @Transactional
    public Agent spawnAgent(UUID missionId, AgentCreateRequest request) {
        log.info("Spawning agent '{}' with role {} for mission {}",
                request.getDisplayName(), request.getRole(), missionId);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> {
                    log.error("Cannot spawn agent — mission not found: {}", missionId);
                    return new MissionNotFoundException("Mission not found with id: " + missionId);
                });

        Agent agent = new Agent(request.getRole(), request.getDisplayName(), request.getModel(), mission);
        Agent saved = agentRepository.save(agent);
        log.info("Agent spawned with id: {} for mission: {}", saved.getId(), missionId);

        eventPublisher.publishEvent(new AgentEvent(
                this,
                saved.getId(),
                missionId,
                "AGENT_SPAWNED",
                "Agent '" + saved.getDisplayName() + "' spawned with role " + saved.getRole()
        ));

        return saved;
    }

    @Transactional
    public Agent spawnAgentForTask(UUID taskId, String model) {
        MissionTask task = taskService.getTask(taskId);
        Agent agent = new Agent(task.getAssignedRole(), task.getAssignedRole().name(), model, task.getMission());
        agent.setTask(task);
        Agent saved = agentRepository.save(agent);
        eventPublisher.publishEvent(new AgentEvent(this, saved.getId(), task.getMission().getId(),
                "AGENT_SPAWNED", "Agent assigned to task " + task.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Agent> listByMission(UUID missionId) {
        log.info("Listing agents for mission: {}", missionId);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> {
                    log.error("Cannot list agents — mission not found: {}", missionId);
                    return new MissionNotFoundException("Mission not found with id: " + missionId);
                });

        List<Agent> agents = agentRepository.findByMission(mission);
        log.info("Found {} agents in mission: {}", agents.size(), missionId);
        return agents;
    }

    @Transactional(readOnly = true)
    public Agent getAgent(UUID missionId, UUID id) {
        log.info("Fetching agent {} in mission {}", id, missionId);
        return agentRepository.findById(id)
                .filter(agent -> agent.getMission().getId().equals(missionId))
                .orElseThrow(() -> {
                    log.error("Agent {} was not found in mission {}", id, missionId);
                    return new AgentNotFoundException("Agent not found with id: " + id);
                });
    }

    @Transactional
    public Agent updateStatus(UUID missionId, UUID id, AgentStatus newStatus) {
        log.info("Updating agent {} status to {}", id, newStatus);

        Agent agent = getAgent(missionId, id);

        AgentStatus oldStatus = agent.getStatus();
        agent.setStatus(newStatus);
        Agent saved = agentRepository.save(agent);
        log.info("Agent {} status changed from {} to {}", id, oldStatus, newStatus);

        eventPublisher.publishEvent(new AgentEvent(
                this,
                saved.getId(),
                saved.getMission().getId(),
                "AGENT_STATUS_CHANGED",
                "Status changed from " + oldStatus + " to " + newStatus
        ));

        return saved;
    }

    @Transactional
    public Agent updateOutput(UUID missionId, UUID id, String output) {
        log.info("Updating output for agent: {}", id);

        Agent agent = getAgent(missionId, id);

        agent.setLastOutput(output);
        Agent saved = agentRepository.save(agent);
        log.info("Agent {} output updated (length: {} chars)", id,
                output != null ? output.length() : 0);

        eventPublisher.publishEvent(new AgentEvent(
                this,
                saved.getId(),
                saved.getMission().getId(),
                "AGENT_OUTPUT_UPDATED",
                "Output updated (" + (output != null ? output.length() : 0) + " chars)"
        ));

        return saved;
    }

    @Transactional
    public Agent sendCommand(UUID missionId, UUID id, String command) {
        log.info("Sending interactive command to agent {}: {}", id, command);
        Agent agent = getAgent(missionId, id);

        String currentOutput = agent.getLastOutput() != null ? agent.getLastOutput() : "";
        String updatedOutput = currentOutput + "\n$ " + command + "\n";
        agent.setLastOutput(updatedOutput);
        agent.setStatus(AgentStatus.RUNNING);
        Agent saved = agentRepository.save(agent);

        eventPublisher.publishEvent(new AgentEvent(
                this,
                saved.getId(),
                saved.getMission().getId(),
                "AGENT_COMMAND_RECEIVED",
                command
        ));

        return saved;
    }

    @Transactional
    public Agent updateModel(UUID missionId, UUID id, String model) {
        log.info("Updating agent {} model to {}", id, model);
        Agent agent = getAgent(missionId, id);

        agent.setModel(model);
        String currentOutput = agent.getLastOutput() != null ? agent.getLastOutput() : "";
        agent.setLastOutput(currentOutput + "\n[System] Switched runtime engine to: " + model + "\n");
        Agent saved = agentRepository.save(agent);

        eventPublisher.publishEvent(new AgentEvent(
                this,
                saved.getId(),
                saved.getMission().getId(),
                "AGENT_MODEL_UPDATED",
                "Model changed to " + model
        ));

        return saved;
    }

    @jakarta.annotation.PostConstruct
    @Transactional
    public void cleanupStaleRunningAgents() {
        try {
            List<Agent> agents = agentRepository.findAll();
            for (Agent a : agents) {
                if (a.getStatus() == AgentStatus.RUNNING || a.getStatus() == AgentStatus.THINKING) {
                    a.setStatus(AgentStatus.COMPLETED);
                    agentRepository.save(a);
                    log.info("Reset stale agent {} to COMPLETED state on startup", a.getId());
                }
            }
        } catch (Exception e) {
            log.warn("Could not reset stale agents on startup: {}", e.getMessage());
        }
    }

    @Transactional
    public Agent resetAgent(UUID missionId, UUID id) {
        log.info("Manually resetting agent {} to COMPLETED state", id);
        return updateStatus(missionId, id, AgentStatus.COMPLETED);
    }

    @Transactional
    public void deleteAgent(UUID missionId, UUID id) {
        log.info("Deleting agent with id: {}", id);

        Agent agent = getAgent(missionId, id);

        UUID persistedMissionId = agent.getMission().getId();
        String displayName = agent.getDisplayName();

        agentRepository.deleteById(id);
        log.info("Agent deleted with id: {}", id);

        eventPublisher.publishEvent(new AgentEvent(
                this,
                id,
                persistedMissionId,
                "AGENT_DELETED",
                "Agent '" + displayName + "' deleted"
        ));
    }
}
