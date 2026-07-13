package com.bridgemind.backend.planner;

import com.bridgemind.backend.agent.AgentCreateRequest;
import com.bridgemind.backend.agent.AgentRole;
import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.event.MissionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlannerService {

    private static final Logger log = LoggerFactory.getLogger(PlannerService.class);
    private static final String DEFAULT_MODEL = "claude-3-5-sonnet-latest";

    private final AgentService agentService;

    public PlannerService(AgentService agentService) {
        this.agentService = agentService;
    }

    @Async
    @EventListener
    public void onMissionEvent(MissionEvent event) {
        if ("MISSION_CREATED".equals(event.getEventType())) {
            log.info("Mission {} created. Spawning agents for parallel orchestration.", event.getMissionId());
            spawnAgentsForMission(event);
        }
    }

    private void spawnAgentsForMission(MissionEvent event) {
        // Mock DAG of 4 tasks -> 4 agents
        List<AgentCreateRequest> agentsToSpawn = List.of(
                createAgentRequest(AgentRole.ARCHITECT, "System Architect"),
                createAgentRequest(AgentRole.BACKEND_ENGINEER, "Backend Engineer"),
                createAgentRequest(AgentRole.FRONTEND_ENGINEER, "Frontend Engineer"),
                createAgentRequest(AgentRole.DEVOPS_ENGINEER, "DevOps Engineer")
        );

        for (AgentCreateRequest request : agentsToSpawn) {
            try {
                agentService.spawnAgent(event.getMissionId(), request);
            } catch (Exception e) {
                log.error("Failed to spawn agent {} for mission {}", request.getDisplayName(), event.getMissionId(), e);
            }
        }
    }

    private AgentCreateRequest createAgentRequest(AgentRole role, String displayName) {
        AgentCreateRequest request = new AgentCreateRequest();
        request.setRole(role);
        request.setDisplayName(displayName);
        request.setModel(DEFAULT_MODEL);
        return request;
    }
}
