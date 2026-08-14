package com.bridgemind.backend.agent;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for sending direct interactive terminal commands to a specific agent.
 */
public class AgentCommandRequest {

    @NotBlank(message = "Command cannot be blank")
    private String command;

    public AgentCommandRequest() {}

    public AgentCommandRequest(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
