package com.bridgemind.backend.runtime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class SimulatedAgentRuntime implements AgentRuntime {
    private final RuntimeExecutionRepository repository;
    public SimulatedAgentRuntime(RuntimeExecutionRepository repository) { this.repository = repository; }
    public String id() { return "simulated"; }
    @Transactional public RuntimeExecution start(RuntimeLaunchRequest request) {
        RuntimeExecution execution = new RuntimeExecution(id(), request);
        execution.setStatus(RuntimeStatus.RUNNING);
        execution.appendLog("Runtime accepted execution request");
        return repository.save(execution);
    }
    @Transactional(readOnly = true) public RuntimeExecution status(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Runtime execution not found: " + id));
    }
    @Transactional public RuntimeExecution cancel(UUID id) {
        RuntimeExecution execution = status(id);
        execution.setStatus(RuntimeStatus.CANCELLED);
        execution.appendLog("Runtime execution cancelled");
        return repository.save(execution);
    }
}
