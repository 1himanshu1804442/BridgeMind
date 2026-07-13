package com.bridgemind.backend.runtime;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RuntimeRegistry {
    private final Map<String, AgentRuntime> runtimes;
    public RuntimeRegistry(List<AgentRuntime> runtimes) {
        this.runtimes = runtimes.stream().collect(Collectors.toUnmodifiableMap(AgentRuntime::id, Function.identity()));
    }
    public AgentRuntime require(String id) {
        AgentRuntime runtime = runtimes.get(id);
        if (runtime == null) throw new IllegalArgumentException("Unknown agent runtime: " + id);
        return runtime;
    }
    public List<String> ids() { return runtimes.keySet().stream().sorted().toList(); }
}
