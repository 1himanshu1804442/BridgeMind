package com.bridgemind.backend.workspace;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public Workspace createWorkspace(String name) {
        Workspace workspace = new Workspace(name);
        return workspaceRepository.save(workspace);
    }

    @Transactional(readOnly = true)
    public List<Workspace> listWorkspaces() {
        return workspaceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Workspace getWorkspace(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found with id: " + id));
    }

    @Transactional
    public void deleteWorkspace(UUID id) {
        if (!workspaceRepository.existsById(id)) {
            throw new WorkspaceNotFoundException("Workspace not found with id: " + id);
        }
        workspaceRepository.deleteById(id);
    }
}
