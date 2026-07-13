package com.bridgemind.backend.planner;

import com.bridgemind.backend.agent.AgentCreateRequest;
import com.bridgemind.backend.agent.AgentRole;
import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.config.AsyncConfig;
import com.bridgemind.backend.event.MissionEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = PlannerServiceIntegrationTest.TestConfig.class)
@ActiveProfiles("test")
public class PlannerServiceIntegrationTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @Import({PlannerService.class, AsyncConfig.class})
    static class TestConfig {
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private AgentService agentService;

    @Test
    void shouldSpawnAgentsWhenMissionCreatedEventFired() {
        // Arrange
        UUID missionId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();

        // Act
        MissionEvent event = new MissionEvent(this, missionId, workspaceId, "MISSION_CREATED", null, "CREATED");
        eventPublisher.publishEvent(event);

        // Assert - wait up to 2 seconds for the async method to execute
        verify(agentService, timeout(2000)).spawnAgent(eq(missionId), org.mockito.ArgumentMatchers.argThat(req -> req.getRole() == AgentRole.ARCHITECT));
        verify(agentService, timeout(2000)).spawnAgent(eq(missionId), org.mockito.ArgumentMatchers.argThat(req -> req.getRole() == AgentRole.BACKEND_ENGINEER));
        verify(agentService, timeout(2000)).spawnAgent(eq(missionId), org.mockito.ArgumentMatchers.argThat(req -> req.getRole() == AgentRole.FRONTEND_ENGINEER));
        verify(agentService, timeout(2000)).spawnAgent(eq(missionId), org.mockito.ArgumentMatchers.argThat(req -> req.getRole() == AgentRole.DEVOPS_ENGINEER));
    }
}
