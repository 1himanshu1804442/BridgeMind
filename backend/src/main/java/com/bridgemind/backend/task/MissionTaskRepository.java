package com.bridgemind.backend.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MissionTaskRepository extends JpaRepository<MissionTask, UUID> {
    List<MissionTask> findByMissionIdOrderByCreatedAtAsc(UUID missionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select distinct task from MissionTask task left join fetch task.dependencies where task.mission.id = :missionId order by task.createdAt asc")
    List<MissionTask> findByMissionIdForUpdate(@Param("missionId") UUID missionId);
}
