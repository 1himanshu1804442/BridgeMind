package com.bridgemind.backend.agent;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for hot-swapping the AI engine/model of an active agent pane.
 */
public class AgentModelUpdateRequest {

    @NotBlank(message = "Model cannot be blank")
    private String model;

    public AgentModelUpdateRequest() {}

    public AgentModelUpdateRequest(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
