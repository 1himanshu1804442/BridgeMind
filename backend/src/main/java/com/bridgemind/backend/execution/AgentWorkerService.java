package com.bridgemind.backend.execution;

import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.agent.AgentStatus;
import com.bridgemind.backend.event.AgentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Service
public class AgentWorkerService {

    private static final Logger log = LoggerFactory.getLogger(AgentWorkerService.class);
    private final AgentService agentService;

    public AgentWorkerService(AgentService agentService) {
        this.agentService = agentService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true,
            condition = "#event.eventType == 'AGENT_SPAWNED'")
    public void handleAgentSpawned(AgentEvent event) {
        if (!"AGENT_SPAWNED".equals(event.getEventType())) {
            return;
        }

        UUID agentId = event.getAgentId();
        log.info("Worker started mock execution for agent: {}", agentId);

        try {
            agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.RUNNING);

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
                agentService.updateOutput(event.getMissionId(), agentId, output.toString());
                Thread.sleep(1000); // 1-second delay
            }

            agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
            log.info("Worker finished mock execution for agent: {}", agentId);
        } catch (InterruptedException e) {
            log.error("Agent mock execution interrupted", e);
            agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.FAILED);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Error during agent mock execution", e);
            agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.FAILED);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true,
            condition = "#event.eventType == 'AGENT_COMMAND_RECEIVED'")
    public void handleAgentCommand(AgentEvent event) {
        if (!"AGENT_COMMAND_RECEIVED".equals(event.getEventType())) {
            return;
        }

        UUID agentId = event.getAgentId();
        String command = event.getDetails();
        log.info("Worker handling direct interactive command for agent {}: {}", agentId, command);

        try {
            com.bridgemind.backend.agent.Agent agent = agentService.getAgent(event.getMissionId(), agentId);
            String model = agent.getModel() != null ? agent.getModel() : "claude-code";

            String[] dynamicSteps = {
                "> [" + model.toUpperCase() + "] Received prompt: \"" + command + "\"",
                "> Parsing context & inspecting sandbox environment...",
                "> Synthesizing code modifications...",
                "> Running static type checker & AST validator...",
                "> Generated artifacts & updated workspace.",
                "> Ready for next command."
            };

            StringBuilder sb = new StringBuilder(agent.getLastOutput() != null ? agent.getLastOutput() : "");
            for (String step : dynamicSteps) {
                sb.append(step).append("\n");
                agentService.updateOutput(event.getMissionId(), agentId, sb.toString());
                Thread.sleep(600);
            }

            agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
            log.info("Worker completed command for agent: {}", agentId);
        } catch (InterruptedException e) {
            log.error("Command execution interrupted", e);
            agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.FAILED);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Error executing agent command", e);
            agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.FAILED);
        }
    }
}
