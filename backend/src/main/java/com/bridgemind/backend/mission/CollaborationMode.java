package com.bridgemind.backend.mission;

/**
 * Operating mode for multi-agent workflows.
 * ISOLATED: Agents run in parallel sandboxes without synchronization locks for maximum velocity.
 * COLLABORATIVE: Agents share unified workspace memory, event bus, and coordinate on a single Git branch.
 */
public enum CollaborationMode {
    ISOLATED,
    COLLABORATIVE
}
