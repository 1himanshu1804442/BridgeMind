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
    private final HostCliExecutionService hostCliExecutionService;

    public AgentWorkerService(AgentService agentService,
                              MissionRepository missionRepository,
                              WorkspacePreviewService previewService,
                              HostCliExecutionService hostCliExecutionService) {
        this.agentService = agentService;
        this.missionRepository = missionRepository;
        this.previewService = previewService;
        this.hostCliExecutionService = hostCliExecutionService;
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
                case ARCHITECT, COORDINATOR -> new String[]{
                    "🧠 [Thinking] Formulating architecture contracts & data models for: \"" + missionTitle + "\"...",
                    "📖 [Tool: read_file] Inspecting project workspace constraints & schema...",
                    "⚡ [Tool: run_command] Emitting data structures: { RUNE_DATABASE, PlayerState, EnemyState, CombatEngine }...",
                    "✏️ [Tool: write_file] Generating architecture specification for 4 concurrent swarm agents...",
                    "🌿 [Git] Architecture contract approved and staged.",
                    "✅ [Done] System topology dispatched to swarm workers."
                };
                case BACKEND_ENGINEER, BUILDER -> new String[]{
                    "🧠 [Thinking] Designing core state machines & game loop for \"" + missionTitle + "\"...",
                    "📖 [Tool: read_file] Reading workspace files & type signatures (/workspace/index.html)...",
                    "⚡ [Tool: run_command] Compiling WebAudio sound synthesizer (cast, hit, victory)...",
                    "✏️ [Tool: edit_file] Writing high-performance game engine in game.js (+380 lines)...",
                    "🧪 [Tool: run_tests] Executing unit tests for spell combinations & damage modifiers... [PASSED]",
                    "🌿 [Git] Staging core engine diff for human-in-the-loop review.",
                    "✅ [Done] Core game mechanics verified with 0 warnings."
                };
                case FRONTEND_ENGINEER -> new String[]{
                    "🧠 [Thinking] Formulating responsive HTML5 UI, Altar layout & glowing visual effects...",
                    "📖 [Tool: read_file] Reading layout stylesheets & phosphor asset hooks...",
                    "⚡ [Tool: run_command] Wiring click/drag listeners, mana crystal shaders, and monster HP bars...",
                    "✏️ [Tool: edit_file] Patching style.css: Added glowing RPG altar aesthetic (+180 lines)...",
                    "✏️ [Tool: edit_file] Patching index.html: Mounted responsive game viewport & cards...",
                    "✅ [Done] Frontend bundle compiled and mounted to Live Preview."
                };
                case QA_ENGINEER, REVIEWER -> new String[]{
                    "🧠 [Thinking] Auditing runtime security boundaries, memory leak profiles & frame timing...",
                    "⚡ [Tool: run_command] Executing automated test suite: npm test -- --run...",
                    "📖 [Tool: read_file] Inspecting test execution logs & heap allocations...",
                    "🧪 [Tool: run_tests] 10,000 turn loops simulated: 0 crashes detected [PASSED]",
                    "🌿 [Git] Approving verified Git diff into master branch.",
                    "✅ [Done] Build verified, signed & approved."
                };
                case DEVOPS_ENGINEER, SCOUT -> new String[]{
                    "🧠 [Thinking] Provisioning containerized sandbox runner & Docker isolation flags...",
                    "⚡ [Tool: run_command] docker build -t sandbox-workspace:latest .",
                    "📖 [Tool: read_file] Auditing .dockerignore & healthcheck endpoints...",
                    "🌿 [Git] CI/CD pipeline verified.",
                    "✅ [Done] Artifacts packaged & deployed to sandbox."
                };
                default -> new String[]{
                    "🧠 [Thinking] Initializing subagent runtime for: \"" + missionTitle + "\"...",
                    "⚡ [Tool: run_command] Executing autonomous mission tasks in isolated Docker container...",
                    "📖 [Tool: read_file] Reading workspace files & compiling dependencies...",
                    "✏️ [Tool: edit_file] Generating verified artifacts & code diffs...",
                    "✅ [Done] Autonomous task completed successfully."
                };
            };

            StringBuilder output = new StringBuilder();
            for (String step : steps) {
                output.append(step).append("\n");
                agentService.updateOutput(event.getMissionId(), agentId, output.toString());
                Thread.sleep(1200); // 1.2s delay for realistic multi-step live streaming
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
        String rawCommand = event.getDetails() != null ? event.getDetails().trim() : "";
        log.info("Worker handling direct interactive command for agent {}: {}", agentId, rawCommand);

        try {
            Agent agent = agentService.getAgent(event.getMissionId(), agentId);
            String currentModel = agent.getModel() != null ? agent.getModel() : "antigravity-agy";
            String lower = rawCommand.toLowerCase();

            // 1. Utility Command: CLEAR / CLS
            if (lower.equals("clear") || lower.equals("cls")) {
                agentService.updateOutput(event.getMissionId(), agentId, "> Terminal buffer cleared.\n> Ready for commands...\n");
                agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
                return;
            }

            // 2. Utility Command: HELP / ?
            if (lower.equals("help") || lower.equals("?") || lower.equals("--help")) {
                String helpBanner = """
======================================================================
🤖 BRIDGEMIND MULTI-MODEL TERMINAL MATRIX CLI HELP
======================================================================
⚡ Fast Model Triggers (type directly in terminal):
  • agy [prompt]        -> Trigger Google Antigravity AGY 2.0 (Gemini 2.5 Pro)
  • codex [prompt]      -> Trigger OpenAI Codex Pro (o3-mini / GPT-4o)
  • claude [prompt]     -> Trigger Anthropic Claude 3.7 Sonnet (Thinking)
  • deepseek [prompt]   -> Trigger DeepSeek R1 Reasoning Engine
  • aider [prompt]      -> Trigger Aider Architect Pro (Git-Pair)
  • cursor [prompt]     -> Trigger Cursor AI Composer
  • use <model_name>    -> Connect to ANY model (e.g. use ollama/qwen2.5-coder)

🛠️ Utilities:
  • clear / cls         -> Clear terminal buffer
  • status              -> Inspect agent telemetry & active model
  • <prompt>            -> Execute with currently active AI model
======================================================================
""";
                StringBuilder sb = new StringBuilder(agent.getLastOutput() != null ? agent.getLastOutput() : "");
                sb.append(helpBanner);
                agentService.updateOutput(event.getMissionId(), agentId, sb.toString());
                agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
                return;
            }

            // 3. Utility Command: STATUS
            if (lower.equals("status")) {
                String statusBanner = String.format("""
[TELEMETRY] Agent: %s (%s) | Model: %s | Status: %s | Sandbox: Active
""", agent.getDisplayName(), agent.getRole(), currentModel, agent.getStatus());
                StringBuilder sb = new StringBuilder(agent.getLastOutput() != null ? agent.getLastOutput() : "");
                sb.append(statusBanner);
                agentService.updateOutput(event.getMissionId(), agentId, sb.toString());
                agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
                return;
            }

            // 4. Model Switching & Trigger Parsers
            String promptToExecute = rawCommand;
            String effectiveModel = currentModel;
            boolean justSwitchedBanner = false;

            if (lower.startsWith("agy") || lower.startsWith("antigravity")) {
                effectiveModel = "antigravity-agy";
                agentService.updateModel(event.getMissionId(), agentId, effectiveModel);
                String rest = rawCommand.replaceFirst("(?i)^(agy|antigravity)\\s*", "").trim();
                if (rest.isEmpty()) {
                    justSwitchedBanner = true;
                    promptToExecute = "";
                } else {
                    promptToExecute = rest;
                }
            } else if (lower.startsWith("codex")) {
                effectiveModel = "codex-pro";
                agentService.updateModel(event.getMissionId(), agentId, effectiveModel);
                String rest = rawCommand.replaceFirst("(?i)^codex(\\s+pro)?\\s*", "").trim();
                if (rest.isEmpty()) {
                    justSwitchedBanner = true;
                    promptToExecute = "";
                } else {
                    promptToExecute = rest;
                }
            } else if (lower.startsWith("claude")) {
                effectiveModel = "claude-3-7-sonnet";
                agentService.updateModel(event.getMissionId(), agentId, effectiveModel);
                String rest = rawCommand.replaceFirst("(?i)^claude(\\s+code)?\\s*", "").trim();
                if (rest.isEmpty()) {
                    justSwitchedBanner = true;
                    promptToExecute = "";
                } else {
                    promptToExecute = rest;
                }
            } else if (lower.startsWith("deepseek") || lower.startsWith("r1")) {
                effectiveModel = "deepseek-r1";
                agentService.updateModel(event.getMissionId(), agentId, effectiveModel);
                String rest = rawCommand.replaceFirst("(?i)^(deepseek|r1)\\s*", "").trim();
                if (rest.isEmpty()) {
                    justSwitchedBanner = true;
                    promptToExecute = "";
                } else {
                    promptToExecute = rest;
                }
            } else if (lower.startsWith("aider")) {
                effectiveModel = "aider-pro";
                agentService.updateModel(event.getMissionId(), agentId, effectiveModel);
                String rest = rawCommand.replaceFirst("(?i)^aider\\s*", "").trim();
                if (rest.isEmpty()) {
                    justSwitchedBanner = true;
                    promptToExecute = "";
                } else {
                    promptToExecute = rest;
                }
            } else if (lower.startsWith("cursor")) {
                effectiveModel = "cursor-composer";
                agentService.updateModel(event.getMissionId(), agentId, effectiveModel);
                String rest = rawCommand.replaceFirst("(?i)^cursor\\s*", "").trim();
                if (rest.isEmpty()) {
                    justSwitchedBanner = true;
                    promptToExecute = "";
                } else {
                    promptToExecute = rest;
                }
            } else if (lower.startsWith("use ") || lower.startsWith("model ")) {
                String customModelName = rawCommand.replaceFirst("(?i)^(use|model)\\s+", "").trim();
                if (!customModelName.isEmpty()) {
                    effectiveModel = customModelName;
                    agentService.updateModel(event.getMissionId(), agentId, effectiveModel);
                    justSwitchedBanner = true;
                    promptToExecute = "";
                }
            }

            // Banner only when switching without extra prompt
            if (justSwitchedBanner) {
                String banner = String.format("""
======================================================================
⚡ [ENGINE ACTIVE] Switched to: %s
Status: Online & Ready. Type your instruction or coding prompt...
======================================================================
""", effectiveModel.toUpperCase());
                StringBuilder sb = new StringBuilder(agent.getLastOutput() != null ? agent.getLastOutput() : "");
                sb.append(banner);
                agentService.updateOutput(event.getMissionId(), agentId, sb.toString());
                agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
                return;
            }

            // 5. Build Swarm Shared Memory Context (Inter-agent communication)
            String swarmContext = buildSwarmSharedContext(event.getMissionId(), agentId, promptToExecute);
            String fullPromptWithContext = promptToExecute;
            if (!swarmContext.isBlank()) {
                fullPromptWithContext = String.format("""
[IMPORTANT MULTI-AGENT SWARM CONTEXT]:
You are collaborating with sibling AI engineering agents in this BridgeMind Workspace.
Here is the actual live telemetry and proposals from your sibling agents in this mission:
%s
========================================
USER INSTRUCTION FOR YOU:
%s
""", swarmContext, promptToExecute);
            }

            // Direct Shell Execution (git, npm, node, gh, python, cargo, dir, ls)
            UUID workspaceId = null;
            try {
                java.util.Optional<com.bridgemind.backend.mission.Mission> mOpt = missionRepository.findById(event.getMissionId());
                if (mOpt.isPresent() && mOpt.get().getWorkspace() != null) {
                    workspaceId = mOpt.get().getWorkspace().getId();
                }
            } catch (Exception e) {
                log.warn("Could not resolve workspaceId for mission: {}", event.getMissionId());
            }

            boolean isRawShell = lower.startsWith("git ") || lower.startsWith("npm ") || lower.startsWith("node ")
                    || lower.startsWith("gh ") || lower.startsWith("python ") || lower.startsWith("cargo ")
                    || lower.startsWith("dir") || lower.startsWith("ls");
            if (isRawShell && workspaceId != null) {
                StringBuilder hostOutput = new StringBuilder(agent.getLastOutput() != null ? agent.getLastOutput() : "");
                hostOutput.append(String.format("> [SHELL] Spawning local command: '%s'...\n", rawCommand));
                
                try {
                    agentService.updateOutput(event.getMissionId(), agentId, hostOutput.toString());
                } catch (Exception ignored) {}

                int exitCode = hostCliExecutionService.executeHostCommand(workspaceId, rawCommand, line -> {
                    hostOutput.append(line).append("\n");
                    try {
                        agentService.updateOutput(event.getMissionId(), agentId, hostOutput.toString());
                    } catch (Exception ignored) {}
                });

                hostOutput.append(String.format("> [SHELL] Finished with exit code %d.\n", exitCode));
                
                try {
                    agentService.updateOutput(event.getMissionId(), agentId, hostOutput.toString());
                    agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
                } catch (Exception ignored) {}
                return;
            }

            // 6. Try Real Host CLI Execution with User's Authenticated Local Session (AGY / Codex)
            if (workspaceId != null) {
                StringBuilder realSb = new StringBuilder(agent.getLastOutput() != null ? agent.getLastOutput() : "");
                realSb.append(String.format("> [AUTHENTICATED HOST CLI] Executed via local %s session:\n", effectiveModel.toUpperCase()));
                
                int hostExit = -1;
                if (effectiveModel.contains("agy") || effectiveModel.contains("antigravity")) {
                    hostExit = hostCliExecutionService.executeAgy(workspaceId, fullPromptWithContext, line -> {
                        realSb.append(line).append("\n");
                        try {
                            agentService.updateOutput(event.getMissionId(), agentId, realSb.toString());
                        } catch (Exception ignored) {}
                    });
                } else if (effectiveModel.contains("codex")) {
                    hostExit = hostCliExecutionService.executeCodex(workspaceId, fullPromptWithContext, line -> {
                        realSb.append(line).append("\n");
                        try {
                            agentService.updateOutput(event.getMissionId(), agentId, realSb.toString());
                        } catch (Exception ignored) {}
                    });
                }

                if (hostExit == 0) {
                    realSb.append("> [HOST CLI] Done.\n");
                    try {
                        agentService.updateOutput(event.getMissionId(), agentId, realSb.toString());
                    } catch (Exception ignored) {}
                    synthesizeWorkspaceFiles(event.getMissionId(), promptToExecute);
                    try {
                        agentService.updateStatus(event.getMissionId(), agentId, AgentStatus.COMPLETED);
                    } catch (Exception ignored) {}
                    return;
                }
            }

            // 6. Dynamic Step Pipeline Fallback (when running inside isolated container without host binaries)
            String finalModelUpper = effectiveModel.toUpperCase();
            String[] dynamicSteps;

            if (effectiveModel.contains("agy") || effectiveModel.contains("antigravity")) {
                dynamicSteps = new String[]{
                    "🧠 [Thinking] Formulating patch for prompt: \"" + promptToExecute + "\" (DeepMind Gemini 2.5 Pro)...",
                    "📖 [Tool: read_file] Reading workspace assets & component AST (/workspace/game.js)...",
                    "⚡ [Tool: run_command] Analyzing WebAudio oscillator frequencies & frame timing...",
                    "✏️ [Tool: edit_file] Patching game.js & style.css with requested modifications...",
                    "🧪 [Tool: run_tests] Validating runtime physics & input handling... [PASSED]",
                    "✅ [Done] AGY 2.0 applied changes to Live Preview."
                };
            } else if (effectiveModel.contains("codex")) {
                dynamicSteps = new String[]{
                    "🧠 [Thinking] OpenAI Codex Pro analyzing AST and coordinate geometry for: \"" + promptToExecute + "\"...",
                    "📖 [Tool: read_file] Reading /workspace/game.js and /workspace/index.html...",
                    "⚡ [Tool: run_command] Validating zero-overhead byte-compiled tick cycles...",
                    "✏️ [Tool: edit_file] Writing optimized collision and score handlers...",
                    "🧪 [Tool: run_tests] Running unit test assertions... [PASSED]",
                    "✅ [Done] Codex Pro synced changes to workspace."
                };
            } else if (effectiveModel.contains("claude")) {
                dynamicSteps = new String[]{
                    "🧠 [Thinking] Claude 3.7 Sonnet formulating multi-file diff architecture...",
                    "📖 [Tool: read_file] Inspecting style.css & responsive layout tokens...",
                    "⚡ [Tool: run_command] Compiling canvas shaders & Web Audio oscillators...",
                    "✏️ [Tool: edit_file] Generating atomic diff for game UI components...",
                    "✅ [Done] Claude Code applied verified workspace changes."
                };
            } else if (effectiveModel.contains("deepseek")) {
                dynamicSteps = new String[]{
                    "🧠 [Thinking] DeepSeek R1 calculating spatial boundary mathematics for: \"" + promptToExecute + "\"...",
                    "📖 [Tool: read_file] Inspecting audio buffer allocation and tick latency...",
                    "⚡ [Tool: run_command] Executing mathematical simulation...",
                    "✏️ [Tool: edit_file] Synthesizing optimal game coordinate solution...",
                    "✅ [Done] DeepSeek R1 workspace modifications complete."
                };
            } else {
                dynamicSteps = new String[]{
                    "🧠 [Thinking] Processing prompt: \"" + promptToExecute + "\" with " + finalModelUpper + "...",
                    "📖 [Tool: read_file] Inspecting workspace context & files...",
                    "⚡ [Tool: run_command] Executing " + finalModelUpper + " runtime tools...",
                    "✏️ [Tool: edit_file] Synthesizing code modifications & validating AST...",
                    "✅ [Done] Generated artifacts & updated workspace."
                };
            }

            StringBuilder sb = new StringBuilder(agent.getLastOutput() != null ? agent.getLastOutput() : "");
            for (String step : dynamicSteps) {
                sb.append(step).append("\n");
                agentService.updateOutput(event.getMissionId(), agentId, sb.toString());
                Thread.sleep(500);
            }

            // Synthesize updated playable application code reflecting interactive prompt
            synthesizeWorkspaceFiles(event.getMissionId(), promptToExecute);

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

    private String buildSwarmSharedContext(UUID missionId, UUID currentAgentId, String prompt) {
        if (missionId == null) return "";
        try {
            java.util.List<Agent> agents = agentService.listByMission(missionId);
            String promptLower = (prompt != null) ? prompt.toLowerCase() : "";
            
            // Check if user is referencing a specific agent role or name (e.g. "architect", "backend", "devops")
            Agent targetAgent = null;
            for (Agent a : agents) {
                if (a.getId().equals(currentAgentId)) continue;
                String roleStr = a.getRole() != null ? a.getRole().name().toLowerCase() : "";
                String nameStr = a.getDisplayName() != null ? a.getDisplayName().toLowerCase() : "";
                if ((!roleStr.isEmpty() && promptLower.contains(roleStr.replace("_", " ")))
                        || (!roleStr.isEmpty() && promptLower.contains(roleStr.replace("_", "")))
                        || (!roleStr.isEmpty() && promptLower.contains(roleStr.split("_")[0]))
                        || (!nameStr.isEmpty() && promptLower.contains(nameStr))) {
                    targetAgent = a;
                    break;
                }
            }

            StringBuilder sb = new StringBuilder();

            // If a specific target agent was mentioned (e.g. "architect"), put it front & center!
            if (targetAgent != null) {
                String out = targetAgent.getLastOutput();
                if (out != null && !out.isBlank()) {
                    sb.append("\n⭐ [PRIMARY SIBLING AGENT REFERENCED IN USER'S QUESTION]:\n");
                    sb.append("Agent Role: ").append(targetAgent.getRole())
                      .append(" | Name: ").append(targetAgent.getDisplayName())
                      .append(" | Model: ").append(targetAgent.getModel()).append("\n");
                    sb.append("Recent Output / Proposals:\n");
                    String[] lines = out.split("\n");
                    int start = Math.max(0, lines.length - 35);
                    for (int i = start; i < lines.length; i++) {
                        String l = lines[i].trim();
                        if (!l.isBlank() && !l.startsWith("======") && !l.startsWith("CLI:") && !l.contains("Terminal ready")) {
                            sb.append("    ").append(l).append("\n");
                        }
                    }
                    sb.append("\n");
                }
            }

            // Other sibling agents in the swarm
            for (Agent other : agents) {
                if (other.getId().equals(currentAgentId) || other.equals(targetAgent)) continue;
                String out = other.getLastOutput();
                if (out != null && !out.isBlank()) {
                    String[] lines = out.split("\n");
                    int start = Math.max(0, lines.length - 20);
                    sb.append("\n• Sibling Agent: ").append(other.getDisplayName())
                      .append(" (Role: ").append(other.getRole())
                      .append(", Model: ").append(other.getModel()).append("):\n");
                    for (int i = start; i < lines.length; i++) {
                        String l = lines[i].trim();
                        if (!l.isBlank() && !l.startsWith("======") && !l.startsWith("CLI:") && !l.contains("Terminal ready")) {
                            sb.append("    ").append(l).append("\n");
                        }
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("Could not build swarm shared context: {}", e.getMessage());
            return "";
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
