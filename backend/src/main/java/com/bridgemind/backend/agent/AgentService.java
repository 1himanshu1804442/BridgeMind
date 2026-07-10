package com.bridgemind.backend.agent;

import com.bridgemind.backend.event.AgentEvent;
import com.bridgemind.backend.mission.Mission;
import com.bridgemind.backend.mission.MissionNotFoundException;
import com.bridgemind.backend.mission.MissionRepository;
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

    public AgentService(AgentRepository agentRepository,
                        MissionRepository missionRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.agentRepository = agentRepository;
        this.missionRepository = missionRepository;
        this.eventPublisher = eventPublisher;
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
    public Agent getAgent(UUID id) {
        log.info("Fetching agent with id: {}", id);
        return agentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Agent not found with id: {}", id);
                    return new AgentNotFoundException("Agent not found with id: " + id);
                });
    }

    @Transactional
    public Agent updateStatus(UUID id, AgentStatus newStatus) {
        log.info("Updating agent {} status to {}", id, newStatus);

        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update status — agent not found: {}", id);
                    return new AgentNotFoundException("Agent not found with id: " + id);
                });

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
    public Agent updateOutput(UUID id, String output) {
        log.info("Updating output for agent: {}", id);

        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update output — agent not found: {}", id);
                    return new AgentNotFoundException("Agent not found with id: " + id);
                });

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
    public void deleteAgent(UUID id) {
        log.info("Deleting agent with id: {}", id);

        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot delete — agent not found with id: {}", id);
                    return new AgentNotFoundException("Agent not found with id: " + id);
                });

        UUID missionId = agent.getMission().getId();
        String displayName = agent.getDisplayName();

        agentRepository.deleteById(id);
        log.info("Agent deleted with id: {}", id);

        eventPublisher.publishEvent(new AgentEvent(
                this,
                id,
                missionId,
                "AGENT_DELETED",
                "Agent '" + displayName + "' deleted"
        ));
    }
}
