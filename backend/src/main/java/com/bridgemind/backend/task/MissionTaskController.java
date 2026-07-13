package com.bridgemind.backend.task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/missions/{missionId}/tasks")
public class MissionTaskController {
    private final MissionTaskService taskService;

    public MissionTaskController(MissionTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<MissionTask> listTasks(@PathVariable UUID missionId) {
        return taskService.listByMission(missionId);
    }
}
