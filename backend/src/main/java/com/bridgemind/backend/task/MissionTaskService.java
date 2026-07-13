package com.bridgemind.backend.task;

import com.bridgemind.backend.agent.AgentRole;
import com.bridgemind.backend.mission.Mission;
import com.bridgemind.backend.mission.MissionNotFoundException;
import com.bridgemind.backend.mission.MissionRepository;
import com.bridgemind.backend.mission.MissionService;
import com.bridgemind.backend.mission.MissionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MissionTaskService {
    private final MissionTaskRepository taskRepository;
    private final MissionRepository missionRepository;
    private final MissionService missionService;

    public MissionTaskService(MissionTaskRepository taskRepository, MissionRepository missionRepository,
                              MissionService missionService) {
        this.taskRepository = taskRepository;
        this.missionRepository = missionRepository;
        this.missionService = missionService;
    }

    @Transactional
    public List<MissionTask> createDefaultPlan(UUID missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException("Mission not found with id: " + missionId));
        if (!taskRepository.findByMissionIdOrderByCreatedAtAsc(missionId).isEmpty()) {
            return taskRepository.findByMissionIdOrderByCreatedAtAsc(missionId);
        }

        MissionTask architecture = taskRepository.save(new MissionTask(mission, "Design the implementation approach", AgentRole.ARCHITECT));
        MissionTask backend = new MissionTask(mission, "Implement backend changes", AgentRole.BACKEND_ENGINEER);
        MissionTask frontend = new MissionTask(mission, "Implement frontend changes", AgentRole.FRONTEND_ENGINEER);
        MissionTask devops = new MissionTask(mission, "Validate delivery and operations", AgentRole.DEVOPS_ENGINEER);
        backend.addDependency(architecture);
        frontend.addDependency(architecture);
        devops.addDependency(architecture);
        taskRepository.saveAll(List.of(backend, frontend, devops));
        missionService.updateStatus(mission.getWorkspace().getId(), missionId, MissionStatus.PLANNING);
        return taskRepository.findByMissionIdOrderByCreatedAtAsc(missionId);
    }

    @Transactional(readOnly = true)
    public List<MissionTask> listByMission(UUID missionId) {
        return taskRepository.findByMissionIdOrderByCreatedAtAsc(missionId);
    }

    @Transactional
    public List<MissionTask> claimRunnableTasks(UUID missionId) {
        List<MissionTask> tasks = taskRepository.findByMissionIdOrderByCreatedAtAsc(missionId);
        List<MissionTask> runnable = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.PENDING)
                .filter(task -> task.getDependencies().stream().allMatch(dependency -> dependency.getStatus() == TaskStatus.COMPLETED))
                .toList();
        runnable.forEach(task -> task.setStatus(TaskStatus.RUNNING));
        List<MissionTask> claimed = taskRepository.saveAll(runnable);
        if (!claimed.isEmpty() && tasks.get(0).getMission().getStatus() == MissionStatus.PLANNING) {
            Mission mission = tasks.get(0).getMission();
            missionService.updateStatus(mission.getWorkspace().getId(), mission.getId(), MissionStatus.RUNNING);
        }
        return claimed;
    }

    @Transactional
    public void markCompleted(UUID taskId) {
        MissionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
        List<MissionTask> tasks = taskRepository.findByMissionIdOrderByCreatedAtAsc(task.getMission().getId());
        if (tasks.stream().allMatch(candidate -> candidate.getStatus() == TaskStatus.COMPLETED)) {
            Mission mission = task.getMission();
            missionService.updateStatus(mission.getWorkspace().getId(), mission.getId(), MissionStatus.DONE);
        }
    }

    @Transactional
    public void markFailed(UUID taskId) {
        MissionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        task.setStatus(TaskStatus.FAILED);
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public MissionTask getTask(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
    }
}
