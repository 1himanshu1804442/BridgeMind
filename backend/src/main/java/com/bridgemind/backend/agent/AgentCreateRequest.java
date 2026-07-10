package com.bridgemind.backend.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for spawning a new Agent within a Mission.
 * Validated by Spring's @Valid mechanism in the controller.
 */
public class AgentCreateRequest {

    @NotBlank(message = "Agent display name must not be blank")
    @Size(min = 1, max = 255, message = "Agent display name must be between 1 and 255 characters")
    private String displayName;

    @NotNull(message = "Agent role must not be null")
    private AgentRole role;

    @NotBlank(message = "Model must not be blank")
    @Size(min = 1, max = 100, message = "Model name must be between 1 and 100 characters")
    private String model;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AgentRole getRole() {
        return role;
    }

    public void setRole(AgentRole role) {
        this.role = role;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
