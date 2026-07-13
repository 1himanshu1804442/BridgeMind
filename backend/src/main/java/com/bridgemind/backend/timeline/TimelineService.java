package com.bridgemind.backend.timeline;

import com.bridgemind.backend.event.AgentEvent;
import com.bridgemind.backend.event.MissionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.bridgemind.backend.mission.MissionRepository;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for recording and querying workspace timeline entries.
 *
 * <p>In addition to the programmatic {@link #recordEvent} API, this service
 * automatically creates timeline entries in response to Spring Application
 * Events ({@link MissionEvent}, {@link AgentEvent}).</p>
 */
@Service
public class TimelineService {

    private static final Logger log = LoggerFactory.getLogger(TimelineService.class);

    private final TimelineRepository timelineRepository;
    private final MissionRepository missionRepository;

    public TimelineService(TimelineRepository timelineRepository, MissionRepository missionRepository) {
        this.timelineRepository = timelineRepository;
        this.missionRepository = missionRepository;
    }

    /**
     * Persist a new timeline entry for the given workspace.
     *
     * @param workspaceId the workspace this event belongs to
     * @param eventType   a short classifier, e.g. "MISSION_CREATED"
     * @param actorName   human-readable name of who/what triggered the event
     * @param actorType   one of "AGENT", "USER", or "SYSTEM"
     * @param summary     one-line human-readable description
     * @param details     optional longer description (may be {@code null})
     * @return the persisted {@link TimelineEntry}
     */
    @Transactional
    public TimelineEntry recordEvent(UUID workspaceId,
                                     String eventType,
                                     String actorName,
                                     String actorType,
                                     String summary,
                                     String details) {
        TimelineEntry entry = new TimelineEntry(
                workspaceId, eventType, actorName, actorType, summary, details
        );
        TimelineEntry saved = timelineRepository.save(entry);
        log.info("Recorded timeline entry [{}] for workspace {} – {}",
                eventType, workspaceId, summary);
        return saved;
    }

    /**
     * List all timeline entries for a workspace, newest first.
     */
    @Transactional(readOnly = true)
    public List<TimelineEntry> listByWorkspace(UUID workspaceId) {
        List<TimelineEntry> entries = timelineRepository
                .findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
        log.info("Retrieved {} timeline entries for workspace {}", entries.size(), workspaceId);
        return entries;
    }

    /**
     * Automatically creates a timeline entry whenever a {@link MissionEvent} is published.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void onMissionEvent(MissionEvent event) {
        String summary = buildMissionSummary(event);
        String details = "Old status: " + event.getOldStatus()
                + ", New status: " + event.getNewStatus();

        recordEvent(
                event.getWorkspaceId(),
                event.getEventType(),
                "SYSTEM",
                "SYSTEM",
                summary,
                details
        );
        log.info("Timeline entry created from MissionEvent [{}] for workspace {}",
                event.getEventType(), event.getWorkspaceId());
    }

    /**
     * Automatically creates a timeline entry whenever an {@link AgentEvent} is published.
     * <p>AgentEvent does not carry a workspaceId directly; we store the missionId
     * in the details and use the missionId as a proxy identifier. In a production
     * system, you would resolve the workspaceId from the mission.</p>
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void onAgentEvent(AgentEvent event) {
        String summary = "Agent " + event.getAgentId() + " – " + event.getEventType();

        UUID workspaceId = missionRepository.findById(event.getMissionId())
                .map(mission -> mission.getWorkspace().getId())
                .orElse(event.getMissionId());

        recordEvent(
                workspaceId,
                event.getEventType(),
                "Agent-" + event.getAgentId(),
                "AGENT",
                summary,
                event.getDetails()
        );
        log.info("Timeline entry created from AgentEvent [{}] for agent {} in mission {}",
                event.getEventType(), event.getAgentId(), event.getMissionId());
    }

    private String buildMissionSummary(MissionEvent event) {
        if (event.getOldStatus() == null || event.getOldStatus().isBlank()) {
            return "Mission " + event.getMissionId() + " created with status "
                    + event.getNewStatus();
        }
        return "Mission " + event.getMissionId() + " status changed from "
                + event.getOldStatus() + " to " + event.getNewStatus();
    }
}
