package com.bridgemind.backend.test;

import java.time.Instant;

public record TestRunResult(
        String suiteType,
        String status, // PASSED, FAILED, ERROR
        int exitCode,
        long durationMs,
        String output,
        String summary,
        Instant executedAt
) {}
