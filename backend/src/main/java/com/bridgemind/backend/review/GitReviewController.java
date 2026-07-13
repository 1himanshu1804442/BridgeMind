package com.bridgemind.backend.review;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/review")
public class GitReviewController {

    private final GitReviewService reviewService;

    public GitReviewController(GitReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/diff")
    public ResponseEntity<?> getDiff(@PathVariable UUID workspaceId) {
        String diff = reviewService.getDiff(workspaceId);
        return ResponseEntity.ok(Map.of("diff", diff));
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approve(
            @PathVariable UUID workspaceId,
            @RequestParam UUID executionId,
            @RequestParam String message) {
        reviewService.approve(workspaceId, executionId, message);
        return ResponseEntity.ok().build();
    }
}
