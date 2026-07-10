package com.bridgemind.backend.timeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing the workspace timeline to the React frontend.
 */
@RestController
@RequestMapping("/api/workspaces/{workspaceId}/timeline")
public class TimelineController {

    private static final Logger log = LoggerFactory.getLogger(TimelineController.class);

    private final TimelineService timelineService;

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    /**
     * Returns all timeline entries for the given workspace, ordered newest first.
     *
     * @param workspaceId the workspace UUID (path variable)
     * @return 200 OK with the list of timeline entries
     */
    @GetMapping
    public ResponseEntity<List<TimelineEntry>> getTimeline(
            @PathVariable UUID workspaceId) {
        log.info("GET /api/workspaces/{}/timeline", workspaceId);
        List<TimelineEntry> entries = timelineService.listByWorkspace(workspaceId);
        return ResponseEntity.ok(entries);
    }
}
