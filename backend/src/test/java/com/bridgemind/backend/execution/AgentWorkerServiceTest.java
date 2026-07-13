package com.bridgemind.backend.execution;

import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.agent.AgentStatus;
import com.bridgemind.backend.event.AgentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentWorkerServiceTest {

    @Mock
    private AgentService agentService;

    @InjectMocks
    private AgentWorkerService agentWorkerService;

    @Test
    void testHandleAgentSpawned() throws InterruptedException {
        UUID agentId = UUID.randomUUID();
        UUID missionId = UUID.randomUUID();
        AgentEvent event = new AgentEvent(this, agentId, missionId, "AGENT_SPAWNED", "Mock Agent Spawned");

        agentWorkerService.handleAgentSpawned(event);

        verify(agentService).updateStatus(missionId, agentId, AgentStatus.RUNNING);
        // It should do 10-15 iterations
        verify(agentService, atLeast(10)).updateOutput(eq(missionId), eq(agentId), anyString());
        verify(agentService).updateStatus(missionId, agentId, AgentStatus.COMPLETED);
    }

    @Test
    void testHandleOtherEventsAreIgnored() throws InterruptedException {
        UUID agentId = UUID.randomUUID();
        UUID missionId = UUID.randomUUID();
        AgentEvent event = new AgentEvent(this, agentId, missionId, "AGENT_DELETED", "Test");

        agentWorkerService.handleAgentSpawned(event);

        verifyNoInteractions(agentService);
    }
}
