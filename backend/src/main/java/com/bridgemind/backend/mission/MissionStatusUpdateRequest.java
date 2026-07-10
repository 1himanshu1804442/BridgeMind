package com.bridgemind.backend.mission;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating a Mission's status via PATCH endpoint.
 */
public class MissionStatusUpdateRequest {

    @NotNull(message = "New status must not be null")
    private MissionStatus status;

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }
}
