package com.bridgemind.backend.workspace;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Workspace createWorkspace(@RequestBody WorkspaceCreateRequest request) {
        return workspaceService.createWorkspace(request.getName());
    }

    @GetMapping
    public List<Workspace> listWorkspaces() {
        return workspaceService.listWorkspaces();
    }

    @GetMapping("/{id}")
    public Workspace getWorkspace(@PathVariable UUID id) {
        return workspaceService.getWorkspace(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@PathVariable UUID id) {
        workspaceService.deleteWorkspace(id);
    }
}
