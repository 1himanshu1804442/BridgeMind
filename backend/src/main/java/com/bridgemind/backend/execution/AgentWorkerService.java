package com.bridgemind.backend.execution;

import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.agent.AgentStatus;
import com.bridgemind.backend.event.AgentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AgentWorkerService {

    private static final Logger log = LoggerFactory.getLogger(AgentWorkerService.class);
    private final AgentService agentService;

    public AgentWorkerService(AgentService agentService) {
        this.agentService = agentService;
    }

    @Async
    @EventListener(condition = "#event.eventType == 'AGENT_SPAWNED'")
    public void handleAgentSpawned(AgentEvent event) {
        if (!"AGENT_SPAWNED".equals(event.getEventType())) {
            return;
        }

        UUID agentId = event.getAgentId();
        log.info("Worker started mock execution for agent: {}", agentId);

        try {
            agentService.updateStatus(agentId, AgentStatus.RUNNING);

            StringBuilder output = new StringBuilder();
            String[] steps = {
                "> Initializing workspace...",
                "> Checking dependencies...",
                "> Running npm install...",
                "> Fetching codebase context...",
                "> Analyzing requirements...",
                "> Designing architecture...",
                "> Generating code...",
                "> Running unit tests...",
                "> Fixing lint errors...",
                "> Preparing commit...",
                "> Finalizing diff...",
                "> Done."
            };

            for (String step : steps) {
                output.append(step).append("\n");
                agentService.updateOutput(agentId, output.toString());
                Thread.sleep(1000); // 1-second delay
            }

            agentService.updateStatus(agentId, AgentStatus.COMPLETED);
            log.info("Worker finished mock execution for agent: {}", agentId);
        } catch (InterruptedException e) {
            log.error("Agent mock execution interrupted", e);
            agentService.updateStatus(agentId, AgentStatus.FAILED);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Error during agent mock execution", e);
            agentService.updateStatus(agentId, AgentStatus.FAILED);
        }
    }
}
