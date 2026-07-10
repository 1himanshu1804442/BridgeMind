package com.bridgemind.backend.workspace;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a Workspace.
 * Validated by Spring's @Valid mechanism in the controller.
 */
public class WorkspaceCreateRequest {

    @NotBlank(message = "Workspace name must not be blank")
    @Size(min = 1, max = 255, message = "Workspace name must be between 1 and 255 characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
