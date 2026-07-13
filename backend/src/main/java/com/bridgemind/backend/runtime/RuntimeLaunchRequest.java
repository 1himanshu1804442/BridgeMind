package com.bridgemind.backend.runtime;

import java.util.UUID;

public record RuntimeLaunchRequest(UUID agentId, UUID missionId, UUID workspaceId, UUID taskId, String instruction) {
}
