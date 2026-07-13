package com.bridgemind.backend.memory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceMemoryRepository extends JpaRepository<WorkspaceMemory, UUID> {
    Optional<WorkspaceMemory> findByWorkspaceIdAndMemoryKey(UUID workspaceId, String memoryKey);
}
