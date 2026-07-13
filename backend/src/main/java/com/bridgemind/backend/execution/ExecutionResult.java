package com.bridgemind.backend.execution;

public record ExecutionResult(int exitCode, String output, String error) {
}
