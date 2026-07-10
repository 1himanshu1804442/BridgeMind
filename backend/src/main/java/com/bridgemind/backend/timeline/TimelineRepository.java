package com.bridgemind.backend.timeline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Data-access layer for {@link TimelineEntry} entities.
 */
@Repository
public interface TimelineRepository extends JpaRepository<TimelineEntry, UUID> {

    /**
     * Returns all timeline entries for the given workspace,
     * ordered newest-first (most recent event at index 0).
     */
    List<TimelineEntry> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
