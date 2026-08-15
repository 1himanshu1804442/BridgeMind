package com.bridgemind.backend.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/tests")
public class TestRunnerController {
    private static final Logger log = LoggerFactory.getLogger(TestRunnerController.class);
    private final TestRunnerService testRunnerService;

    public TestRunnerController(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }

    @PostMapping("/run")
    public ResponseEntity<TestRunResult> runTests(@PathVariable UUID workspaceId) {
        log.info("REST request to run tests for workspace {}", workspaceId);
        TestRunResult result = testRunnerService.runTests(workspaceId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestResult(@PathVariable UUID workspaceId) {
        TestRunResult result = testRunnerService.getLatestResult(workspaceId);
        if (result == null) {
            return ResponseEntity.ok(Map.of("status", "NO_RUNS_YET", "summary", "No test suite has been executed yet."));
        }
        return ResponseEntity.ok(result);
    }
}
