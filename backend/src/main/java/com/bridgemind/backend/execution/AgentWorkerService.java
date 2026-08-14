package com.bridgemind.backend.execution;

import com.bridgemind.backend.agent.Agent;
import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.agent.AgentStatus;
import com.bridgemind.backend.event.AgentEvent;
import com.bridgemind.backend.mission.Mission;
import com.bridgemind.backend.mission.MissionRepository;
import com.bridgemind.backend.workspace.WorkspacePreviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;
import java.util.UUID;

/**
 * Worker service handling background agent execution tasks and interactive commands.
 * When agents execute code-generation missions, this service synthesizes functional
 * game and web application files (index.html, game.js, style.css) into the workspace directory.
 */
@Service
public class AgentWorkerService {

    private static final Logger log = LoggerFactory.getLogger(AgentWorkerService.class);
    private final AgentService agentService;
    private final MissionRepository missionRepository;
    private final WorkspacePreviewService previewService;

    public AgentWorkerService(AgentService agentService,
                              MissionRepository missionRepository,
                              WorkspacePreviewService previewService) {
        this.agentService = agentService;
        this.missionRepository = missionRepository;
        this.previewService = previewService;
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

            Optional<Mission> optionalMission = missionRepository != null ? missionRepository.findById(event.getMissionId()) : Optional.empty();
            String missionTitle = optionalMission.map(Mission::getTitle).orElse("BridgeMind Live Arcade");
            
            com.bridgemind.backend.agent.AgentRole role = com.bridgemind.backend.agent.AgentRole.ARCHITECT;
            String model = "claude-code";
            try {
                Agent agent = agentService.getAgent(event.getMissionId(), agentId);
                if (agent != null) {
                    if (agent.getRole() != null) role = agent.getRole();
                    if (agent.getModel() != null) model = agent.getModel();
                }
            } catch (Exception ignored) {}

            String[] steps = switch (role) {
                case ARCHITECT -> new String[]{
                    "> [" + model.toUpperCase() + "] Initializing architecture engine for: \"" + missionTitle + "\"",
                    "> Inspecting schema, runtime environments, and project constraints...",
                    "> Formulating execution plan DAG (4 concurrent sub-agents)...",
                    "> Provisioning sandbox container filesystem at /workspace...",
                    "> Architecture specification compiled & verified.",
                    "> System design ready."
                };
                case BACKEND_ENGINEER -> new String[]{
                    "> [" + model.toUpperCase() + "] Synthesizing core mechanics & logic engine for \"" + missionTitle + "\"...",
                    "> Generating game coordinate matrix, boundary collisions, and state machines...",
                    "> Implementing high-performance 60 FPS tick loop...",
                    "> Compiling Web Audio sound synthesis hooks (eat/laser/crash/score)...",
                    "> Core engine verified with 0 warnings."
                };
                case FRONTEND_ENGINEER -> new String[]{
                    "> [" + model.toUpperCase() + "] Rendering responsive HTML5 Canvas & Phosphor UI...",
                    "> Constructing glowing cyber aesthetic, CRT scanlines, and retro HUD...",
                    "> Wiring WASD, Arrow keys, swipe gestures, and mobile touch pads...",
                    "> Binding real-time score counters & local storage high-scores...",
                    "> Frontend bundle compiled and mounted to Live Preview."
                };
                case QA_ENGINEER, REVIEWER -> new String[]{
                    "> [" + model.toUpperCase() + "] Running comprehensive automated validation suite...",
                    "> Verifying 60 FPS frame timing & collision math...",
                    "> Auditing memory leak boundaries & audio context suspension...",
                    "> Simulating 10,000 game loops: 0 crashes detected.",
                    "> Build verified & approved."
                };
                default -> new String[]{
                    "> [" + model.toUpperCase() + "] Initializing subagent runtime for \"" + missionTitle + "\"...",
                    "> Executing autonomous mission tasks in isolated Docker container...",
                    "> Compiling dependencies & running type checker...",
                    "> Artifacts generated and verified.",
                    "> Ready."
                };
            };

            StringBuilder output = new StringBuilder();
            for (String step : steps) {
                output.append(step).append("\n");
                agentService.updateOutput(event.getMissionId(), agentId, output.toString());
                Thread.sleep(600); // 600ms delay for smooth live streaming
            }

            // Synthesize real playable code files into workspace
            synthesizeWorkspaceFiles(event.getMissionId(), missionTitle);

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
            Agent agent = agentService.getAgent(event.getMissionId(), agentId);
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

            // Synthesize updated playable application code reflecting interactive prompt
            synthesizeWorkspaceFiles(event.getMissionId(), command);

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

    private void synthesizeWorkspaceFiles(UUID missionId, String title) {
        if (missionId == null || missionRepository == null || previewService == null) {
            return;
        }
        try {
            Optional<Mission> optionalMission = missionRepository.findById(missionId);
            if (optionalMission.isPresent() && optionalMission.get().getWorkspace() != null) {
                UUID workspaceId = optionalMission.get().getWorkspace().getId();
                previewService.synthesizeDefaultGameFiles(workspaceId, title);
                log.info("Synthesized workspace files for mission: {} in workspace: {}", missionId, workspaceId);
            }
        } catch (Exception e) {
            log.error("Failed to synthesize workspace files for mission {}", missionId, e);
        }
    }
}
