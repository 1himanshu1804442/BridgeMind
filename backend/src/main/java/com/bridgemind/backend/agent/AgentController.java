package com.bridgemind.backend.agent;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Agent CRUD operations.
 * Nested under /api/missions/{missionId}/agents.
 * All business logic is delegated to AgentService.
 */
@RestController
@RequestMapping("/api/missions/{missionId}/agents")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agent spawnAgent(@PathVariable UUID missionId,
                            @Valid @RequestBody AgentCreateRequest request) {
        log.info("POST /api/missions/{}/agents — name: {}, role: {}",
                missionId, request.getDisplayName(), request.getRole());
        return agentService.spawnAgent(missionId, request);
    }

    @GetMapping
    public List<Agent> listAgents(@PathVariable UUID missionId) {
        log.info("GET /api/missions/{}/agents", missionId);
        return agentService.listByMission(missionId);
    }

    @GetMapping("/{agentId}")
    public Agent getAgent(@PathVariable UUID missionId,
                          @PathVariable UUID agentId) {
        log.info("GET /api/missions/{}/agents/{}", missionId, agentId);
        return agentService.getAgent(missionId, agentId);
    }

    @PatchMapping("/{agentId}/status")
    public Agent updateAgentStatus(@PathVariable UUID missionId,
                                   @PathVariable UUID agentId,
                                   @Valid @RequestBody AgentStatusUpdateRequest request) {
        log.info("PATCH /api/missions/{}/agents/{}/status — newStatus: {}",
                missionId, agentId, request.getStatus());
        return agentService.updateStatus(missionId, agentId, request.getStatus());
    }

    @PatchMapping("/{agentId}/output")
    public Agent updateAgentOutput(@PathVariable UUID missionId,
                                   @PathVariable UUID agentId,
                                   @RequestBody AgentOutputUpdateRequest request) {
        log.info("PATCH /api/missions/{}/agents/{}/output", missionId, agentId);
        return agentService.updateOutput(missionId, agentId, request.getOutput());
    }

    @PostMapping("/{agentId}/command")
    public Agent sendCommand(@PathVariable UUID missionId,
                             @PathVariable UUID agentId,
                             @Valid @RequestBody AgentCommandRequest request) {
        log.info("POST /api/missions/{}/agents/{}/command — command: {}",
                missionId, agentId, request.getCommand());
        return agentService.sendCommand(missionId, agentId, request.getCommand());
    }

    @PatchMapping("/{agentId}/model")
    public Agent updateAgentModel(@PathVariable UUID missionId,
                                  @PathVariable UUID agentId,
                                  @Valid @RequestBody AgentModelUpdateRequest request) {
        log.info("PATCH /api/missions/{}/agents/{}/model — newModel: {}",
                missionId, agentId, request.getModel());
        return agentService.updateModel(missionId, agentId, request.getModel());
    }

    @PostMapping("/{agentId}/reset")
    public Agent resetAgent(@PathVariable UUID missionId,
                            @PathVariable UUID agentId) {
        log.info("POST /api/missions/{}/agents/{}/reset", missionId, agentId);
        return agentService.resetAgent(missionId, agentId);
    }

    @DeleteMapping("/{agentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAgent(@PathVariable UUID missionId,
                            @PathVariable UUID agentId) {
        log.info("DELETE /api/missions/{}/agents/{}", missionId, agentId);
        agentService.deleteAgent(missionId, agentId);
    }
}
