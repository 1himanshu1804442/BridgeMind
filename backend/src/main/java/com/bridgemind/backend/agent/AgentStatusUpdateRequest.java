package com.bridgemind.backend.agent;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating an Agent's status via PATCH endpoint.
 */
public class AgentStatusUpdateRequest {

    @NotNull(message = "New status must not be null")
    private AgentStatus status;

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }
}
