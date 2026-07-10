package com.bridgemind.backend.agent;

/**
 * Request DTO for updating an Agent's last output.
 */
public class AgentOutputUpdateRequest {

    private String output;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
