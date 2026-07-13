package com.bridgemind.backend.planner;

import com.bridgemind.backend.agent.AgentRepository;
import com.bridgemind.backend.agent.AgentService;
import com.bridgemind.backend.config.AsyncConfig;
import com.bridgemind.backend.event.MissionEvent;
import com.bridgemind.backend.task.MissionTaskService;
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

    @MockBean
    private MissionTaskService taskService;

    @MockBean
    private AgentRepository agentRepository;

    @Test
    void shouldSpawnAgentsWhenMissionCreatedEventFired() {
        // Arrange
        UUID missionId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();

        // Act
        MissionEvent event = new MissionEvent(this, missionId, workspaceId, "MISSION_CREATED", null, "CREATED");
        eventPublisher.publishEvent(event);

        // Assert - the planner creates and schedules a persisted task graph after commit.
        verify(taskService, timeout(2000)).createDefaultPlan(eq(missionId));
        verify(taskService, timeout(2000)).claimRunnableTasks(eq(missionId));
    }
}
