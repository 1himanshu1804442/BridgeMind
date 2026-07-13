package com.bridgemind.backend.mission;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Mission CRUD operations.
 * Nested under /api/workspaces/{workspaceId}/missions.
 * All business logic is delegated to MissionService.
 */
@RestController
@RequestMapping("/api/workspaces/{workspaceId}/missions")
public class MissionController {

    private static final Logger log = LoggerFactory.getLogger(MissionController.class);

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mission createMission(@PathVariable UUID workspaceId,
                                 @Valid @RequestBody MissionCreateRequest request) {
        log.info("POST /api/workspaces/{}/missions — title: {}", workspaceId, request.getTitle());
        return missionService.createMission(workspaceId, request.getTitle());
    }

    @GetMapping
    public List<Mission> listMissions(@PathVariable UUID workspaceId) {
        log.info("GET /api/workspaces/{}/missions", workspaceId);
        return missionService.listByWorkspace(workspaceId);
    }

    @GetMapping("/{missionId}")
    public Mission getMission(@PathVariable UUID workspaceId,
                              @PathVariable UUID missionId) {
        log.info("GET /api/workspaces/{}/missions/{}", workspaceId, missionId);
        return missionService.getMission(workspaceId, missionId);
    }

    @PatchMapping("/{missionId}/status")
    public Mission updateMissionStatus(@PathVariable UUID workspaceId,
                                       @PathVariable UUID missionId,
                                       @Valid @RequestBody MissionStatusUpdateRequest request) {
        log.info("PATCH /api/workspaces/{}/missions/{}/status — newStatus: {}",
                workspaceId, missionId, request.getStatus());
        return missionService.updateStatus(workspaceId, missionId, request.getStatus());
    }

    @DeleteMapping("/{missionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMission(@PathVariable UUID workspaceId,
                              @PathVariable UUID missionId) {
        log.info("DELETE /api/workspaces/{}/missions/{}", workspaceId, missionId);
        missionService.deleteMission(workspaceId, missionId);
    }
}
