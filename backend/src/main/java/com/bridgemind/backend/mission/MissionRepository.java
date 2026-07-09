package com.bridgemind.backend.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;
import com.bridgemind.backend.workspace.Workspace;

@Repository
public interface MissionRepository extends JpaRepository<Mission, UUID> {
    List<Mission> findByWorkspace(Workspace workspace);
}
