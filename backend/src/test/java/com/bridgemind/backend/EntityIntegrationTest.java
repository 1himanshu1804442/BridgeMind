package com.bridgemind.backend;

import com.bridgemind.backend.mission.Mission;
import com.bridgemind.backend.mission.MissionRepository;
import com.bridgemind.backend.mission.MissionStatus;
import com.bridgemind.backend.workspace.Workspace;
import com.bridgemind.backend.workspace.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class EntityIntegrationTest {
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Test
    public void testWorkspaceAndMissionLifecycle() {
        // 1. Create Workspace
        Workspace workspace = new Workspace("Alpha Project");
        Workspace savedWorkspace = workspaceRepository.save(workspace);

        assertThat(savedWorkspace.getId()).isNotNull();
        assertThat(savedWorkspace.getName()).isEqualTo("Alpha Project");
        assertThat(savedWorkspace.getCreatedAt()).isNotNull();

        // 2. Create Mission
        Mission mission = new Mission("Implement OAuth", savedWorkspace);
        Mission savedMission = missionRepository.save(mission);

        assertThat(savedMission.getId()).isNotNull();
        assertThat(savedMission.getTitle()).isEqualTo("Implement OAuth");
        assertThat(savedMission.getStatus()).isEqualTo(MissionStatus.CREATED);
        assertThat(savedMission.getWorkspace().getId()).isEqualTo(savedWorkspace.getId());

        // 3. Update Mission Status
        savedMission.setStatus(MissionStatus.PLANNING);
        Mission updatedMission = missionRepository.save(savedMission);
        assertThat(updatedMission.getStatus()).isEqualTo(MissionStatus.PLANNING);

        // 4. Fetch Missions for Workspace
        List<Mission> missions = missionRepository.findByWorkspace(savedWorkspace);
        assertThat(missions).hasSize(1);
        assertThat(missions.get(0).getTitle()).isEqualTo("Implement OAuth");
    }
}
