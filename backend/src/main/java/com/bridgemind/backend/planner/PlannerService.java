package com.bridgemind.backend.planner;

import com.bridgemind.backend.agent.Agent;
import com.bridgemind.backend.agent.AgentRepository;
import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.agent.AgentStatus;
import com.bridgemind.backend.event.AgentEvent;
import com.bridgemind.backend.event.MissionEvent;
import com.bridgemind.backend.task.MissionTask;
import com.bridgemind.backend.task.MissionTaskService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlannerService {
    private static final String DEFAULT_MODEL = "claude-3-5-sonnet-latest";

    private final MissionTaskService taskService;
    private final AgentService agentService;
    private final AgentRepository agentRepository;

    public PlannerService(MissionTaskService taskService, AgentService agentService, AgentRepository agentRepository) {
        this.taskService = taskService;
        this.agentService = agentService;
        this.agentRepository = agentRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMissionEvent(MissionEvent event) {
        if ("MISSION_CREATED".equals(event.getEventType())) {
            taskService.createDefaultPlan(event.getMissionId());
            dispatchRunnableTasks(event.getMissionId());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void onAgentEvent(AgentEvent event) {
        if (!"AGENT_STATUS_CHANGED".equals(event.getEventType())) {
            return;
        }
        agentRepository.findById(event.getAgentId()).ifPresent(agent -> updateTaskAndDispatch(agent));
    }

    private void updateTaskAndDispatch(Agent agent) {
        if (agent.getTask() == null) {
            return;
        }
        if (agent.getStatus() == AgentStatus.COMPLETED) {
            taskService.markCompleted(agent.getTask().getId());
            dispatchRunnableTasks(agent.getMission().getId());
        } else if (agent.getStatus() == AgentStatus.FAILED) {
            taskService.markFailed(agent.getTask().getId());
        }
    }

    private String getRoleModel(com.bridgemind.backend.agent.AgentRole role) {
        if (role == null) return "antigravity-agy";
        return switch (role) {
            case ARCHITECT -> "antigravity-agy";
            case BACKEND_ENGINEER -> "codex-pro";
            case FRONTEND_ENGINEER -> "claude-3-7-sonnet";
            case QA_ENGINEER, DEVOPS_ENGINEER, REVIEWER, SECURITY_AUDITOR -> "deepseek-r1";
            default -> "antigravity-agy";
        };
    }

    private void dispatchRunnableTasks(java.util.UUID missionId) {
        for (MissionTask task : taskService.claimRunnableTasks(missionId)) {
            try {
                String model = getRoleModel(task.getAssignedRole());
                agentService.spawnAgentForTask(task.getId(), model);
            } catch (RuntimeException exception) {
                taskService.markFailed(task.getId());
            }
        }
    }
}
