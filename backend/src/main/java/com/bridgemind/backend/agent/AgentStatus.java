package com.bridgemind.backend.agent;

/**
 * Lifecycle states for an AI Agent within a Mission.
 * CREATED → WAITING → RUNNING → THINKING → CALLING_TOOL → WAITING_FOR_TOOL
 * → GENERATING_DIFF → REVIEW_PENDING → COMPLETED (or FAILED at any point)
 */
public enum AgentStatus {
    CREATED,
    WAITING,
    RUNNING,
    THINKING,
    CALLING_TOOL,
    WAITING_FOR_TOOL,
    GENERATING_DIFF,
    REVIEW_PENDING,
    COMPLETED,
    FAILED
}
