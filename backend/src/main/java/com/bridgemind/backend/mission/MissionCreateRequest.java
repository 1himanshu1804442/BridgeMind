package com.bridgemind.backend.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a Mission within a Workspace.
 * Validated by Spring's @Valid mechanism in the controller.
 */
public class MissionCreateRequest {

    @NotBlank(message = "Mission title must not be blank")
    @Size(min = 1, max = 500, message = "Mission title must be between 1 and 500 characters")
    private String title;

    private CollaborationMode collaborationMode = CollaborationMode.COLLABORATIVE;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CollaborationMode getCollaborationMode() {
        return collaborationMode;
    }

    public void setCollaborationMode(CollaborationMode collaborationMode) {
        this.collaborationMode = collaborationMode;
    }

}
