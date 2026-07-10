package com.bridgemind.backend.workspace;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Workspace CRUD operations.
 * Per architecture.md: Controllers contain NO business logic;
 * all work is delegated to WorkspaceService.
 */
@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceController.class);

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Workspace createWorkspace(@Valid @RequestBody WorkspaceCreateRequest request) {
        log.info("Creating workspace with name: {}", request.getName());
        return workspaceService.createWorkspace(request.getName());
    }

    @GetMapping
    public List<Workspace> listWorkspaces() {
        log.info("Listing all workspaces");
        return workspaceService.listWorkspaces();
    }

    @GetMapping("/{id}")
    public Workspace getWorkspace(@PathVariable UUID id) {
        log.info("Fetching workspace with id: {}", id);
        return workspaceService.getWorkspace(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@PathVariable UUID id) {
        log.info("Deleting workspace with id: {}", id);
        workspaceService.deleteWorkspace(id);
    }
}
