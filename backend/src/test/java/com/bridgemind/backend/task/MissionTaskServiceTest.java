package com.bridgemind.backend.task;

import com.bridgemind.backend.agent.AgentRole;
import com.bridgemind.backend.mission.Mission;
import com.bridgemind.backend.mission.MissionRepository;
import com.bridgemind.backend.mission.MissionService;
import com.bridgemind.backend.mission.MissionStatus;
import com.bridgemind.backend.workspace.Workspace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionTaskServiceTest {

    @Mock private MissionTaskRepository taskRepository;
    @Mock private MissionRepository missionRepository;
    @Mock private MissionService missionService;

    @Test
    void claimsOnlyTasksWhoseDependenciesAreComplete() {
        Mission mission = planningMission();
        MissionTask architecture = new MissionTask(mission, "Architecture", AgentRole.ARCHITECT);
        MissionTask backend = new MissionTask(mission, "Backend", AgentRole.BACKEND_ENGINEER);
        backend.addDependency(architecture);
        UUID missionId = UUID.randomUUID();
        when(taskRepository.findByMissionIdForUpdate(missionId)).thenReturn(List.of(architecture, backend));
        when(taskRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<MissionTask> claimed = service().claimRunnableTasks(missionId);

        assertThat(claimed).containsExactly(architecture);
        assertThat(architecture.getStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(backend.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void failureCancelsPendingWorkAndFailsTheMission() {
        Mission mission = planningMission();
        MissionTask failed = new MissionTask(mission, "Architecture", AgentRole.ARCHITECT);
        MissionTask pending = new MissionTask(mission, "Backend", AgentRole.BACKEND_ENGINEER);
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(failed));
        when(taskRepository.findByMissionIdForUpdate(null)).thenReturn(List.of(failed, pending));
        when(taskRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        service().markFailed(taskId);

        assertThat(failed.getStatus()).isEqualTo(TaskStatus.FAILED);
        assertThat(pending.getStatus()).isEqualTo(TaskStatus.CANCELLED);
        verify(missionService).updateStatus(isNull(), isNull(), org.mockito.ArgumentMatchers.eq(MissionStatus.FAILED));
    }

    private MissionTaskService service() {
        return new MissionTaskService(taskRepository, missionRepository, missionService);
    }

    private Mission planningMission() {
        Workspace workspace = new Workspace("Workspace");
        Mission mission = new Mission("Mission", workspace);
        mission.setStatus(MissionStatus.PLANNING);
        return mission;
    }
}
