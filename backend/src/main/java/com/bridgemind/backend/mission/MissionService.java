package com.bridgemind.backend.mission;

import com.bridgemind.backend.event.MissionEvent;
import com.bridgemind.backend.workspace.Workspace;
import com.bridgemind.backend.workspace.WorkspaceNotFoundException;
import com.bridgemind.backend.workspace.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Business logic for Mission lifecycle management.
 * Validates workspace existence, publishes MissionEvents on create and status changes.
 */
@Service
public class MissionService {

    private static final Logger log = LoggerFactory.getLogger(MissionService.class);

    private final MissionRepository missionRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final com.bridgemind.backend.workspace.WorkspacePreviewService previewService;

    public MissionService(MissionRepository missionRepository,
                          WorkspaceRepository workspaceRepository,
                          ApplicationEventPublisher eventPublisher,
                          com.bridgemind.backend.workspace.WorkspacePreviewService previewService) {
        this.missionRepository = missionRepository;
        this.workspaceRepository = workspaceRepository;
        this.eventPublisher = eventPublisher;
        this.previewService = previewService;
    }

    @Transactional
    public Mission createMission(UUID workspaceId, String title) {
        return createMission(workspaceId, title, CollaborationMode.COLLABORATIVE);
    }

    @Transactional
    public Mission createMission(UUID workspaceId, String title, CollaborationMode collaborationMode) {
        log.info("Creating mission '{}' (mode: {}) in workspace {}", title, collaborationMode, workspaceId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> {
                    log.error("Cannot create mission — workspace not found: {}", workspaceId);
                    return new WorkspaceNotFoundException("Workspace not found with id: " + workspaceId);
                });

        Mission mission = new Mission(title, workspace);
        if (collaborationMode != null) {
            mission.setCollaborationMode(collaborationMode);
        }
        Mission saved = missionRepository.save(mission);
        log.info("Mission created with id: {} in workspace: {}", saved.getId(), workspaceId);

        // Pre-synthesize live game/application files immediately so the preview frame updates instantly
        try {
            if (previewService != null) {
                previewService.synthesizeDefaultGameFiles(workspaceId, title);
            }
        } catch (Exception e) {
            log.error("Error synthesizing preview files for mission {}", saved.getId(), e);
        }

        eventPublisher.publishEvent(new MissionEvent(
                this,
                saved.getId(),
                workspaceId,
                "MISSION_CREATED",
                null,
                saved.getStatus().name()
        ));

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Mission> listByWorkspace(UUID workspaceId) {
        log.info("Listing missions for workspace: {}", workspaceId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> {
                    log.error("Cannot list missions — workspace not found: {}", workspaceId);
                    return new WorkspaceNotFoundException("Workspace not found with id: " + workspaceId);
                });

        List<Mission> missions = missionRepository.findByWorkspace(workspace);
        log.info("Found {} missions in workspace: {}", missions.size(), workspaceId);
        return missions;
    }

    @Transactional(readOnly = true)
    public Mission getMission(UUID workspaceId, UUID id) {
        log.info("Fetching mission {} in workspace {}", id, workspaceId);
        return missionRepository.findById(id)
                .filter(mission -> mission.getWorkspace().getId().equals(workspaceId))
                .orElseThrow(() -> {
                    log.error("Mission {} was not found in workspace {}", id, workspaceId);
                    return new MissionNotFoundException("Mission not found with id: " + id);
                });
    }

    @Transactional
    public Mission updateStatus(UUID workspaceId, UUID id, MissionStatus newStatus) {
        log.info("Updating mission {} status to {}", id, newStatus);

        Mission mission = getMission(workspaceId, id);

        String oldStatusName = mission.getStatus().name();
        mission.setStatus(newStatus);
        Mission saved = missionRepository.save(mission);
        log.info("Mission {} status changed from {} to {}", id, oldStatusName, newStatus);

        eventPublisher.publishEvent(new MissionEvent(
                this,
                saved.getId(),
                saved.getWorkspace().getId(),
                "MISSION_STATUS_CHANGED",
                oldStatusName,
                newStatus.name()
        ));

        return saved;
    }

    @Transactional
    public void deleteMission(UUID workspaceId, UUID id) {
        log.info("Deleting mission with id: {}", id);

        getMission(workspaceId, id);

        missionRepository.deleteById(id);
        log.info("Mission deleted with id: {}", id);
    }
}
