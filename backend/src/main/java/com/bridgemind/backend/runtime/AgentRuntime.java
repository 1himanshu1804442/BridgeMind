package com.bridgemind.backend.runtime;

import java.util.UUID;

public interface AgentRuntime {
    String id();
    RuntimeExecution start(RuntimeLaunchRequest request);
    RuntimeExecution status(UUID executionId);
    RuntimeExecution cancel(UUID executionId);
}
