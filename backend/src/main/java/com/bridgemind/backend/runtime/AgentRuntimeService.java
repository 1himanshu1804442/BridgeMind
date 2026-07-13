package com.bridgemind.backend.runtime;

import com.bridgemind.backend.event.RuntimeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class AgentRuntimeService {
    private final RuntimeRegistry registry;
    private final ApplicationEventPublisher events;
    public AgentRuntimeService(RuntimeRegistry registry, ApplicationEventPublisher events) { this.registry = registry; this.events = events; }
    @Transactional public RuntimeExecution launch(String runtimeId, RuntimeLaunchRequest request) {
        RuntimeExecution execution = registry.require(runtimeId).start(request);
        events.publishEvent(new RuntimeEvent(this, request.workspaceId(), execution.getId(), "RUNTIME_STARTED", runtimeId));
        return execution;
    }
    @Transactional public RuntimeExecution cancel(String runtimeId, UUID executionId) {
        RuntimeExecution execution = registry.require(runtimeId).cancel(executionId);
        events.publishEvent(new RuntimeEvent(this, execution.getWorkspaceId(), executionId, "RUNTIME_CANCELLED", runtimeId));
        return execution;
    }
}
