package com.bridgemind.backend.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Business logic for Workspace lifecycle management.
 * Per agent.md: "Keep services focused" — this handles CRUD only.
 */
@Service
public class WorkspaceService {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceService.class);

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public Workspace createWorkspace(String name) {
        log.info("Creating workspace: {}", name);
        Workspace workspace = new Workspace(name);
        Workspace saved = workspaceRepository.save(workspace);
        log.info("Workspace created with id: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Workspace> listWorkspaces() {
        List<Workspace> workspaces = workspaceRepository.findAll();
        log.info("Listed {} workspaces", workspaces.size());
        return workspaces;
    }

    @Transactional(readOnly = true)
    public Workspace getWorkspace(UUID id) {
        log.info("Fetching workspace with id: {}", id);
        return workspaceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Workspace not found with id: {}", id);
                    return new WorkspaceNotFoundException("Workspace not found with id: " + id);
                });
    }

    @Transactional
    public void deleteWorkspace(UUID id) {
        if (!workspaceRepository.existsById(id)) {
            log.error("Cannot delete — workspace not found with id: {}", id);
            throw new WorkspaceNotFoundException("Workspace not found with id: " + id);
        }
        workspaceRepository.deleteById(id);
        log.info("Workspace deleted with id: {}", id);
    }
}
