package com.bridgemind.backend.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MissionTaskRepository extends JpaRepository<MissionTask, UUID> {
    List<MissionTask> findByMissionIdOrderByCreatedAtAsc(UUID missionId);
}
