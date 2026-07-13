# REST APIs

All REST APIs are served under the base URL `/api`. Currently, the API is internal and lacks authentication, though Auth headers are planned for Phase 6.

## Workspaces
### Create Workspace
- **URL**: `POST /api/workspaces`
- **Request Body**: `WorkspaceCreateRequest` (`{ name: string }`)
- **Response**: `Workspace` (201 Created)

### List Workspaces
- **URL**: `GET /api/workspaces`
- **Response**: `List<Workspace>` (200 OK)

## Missions
### Create Mission
- **URL**: `POST /api/workspaces/{workspaceId}/missions`
- **Request Body**: `MissionCreateRequest` (`{ title: string, workspaceId: string }`)
- **Response**: `Mission` (201 Created)
- **Side Effect**: Emits `MISSION_CREATED` event on EventBus, triggering orchestration.

### List Missions
- **URL**: `GET /api/workspaces/{workspaceId}/missions`
- **Response**: `List<Mission>` (200 OK)

### Update Mission Status
- **URL**: `PATCH /api/workspaces/missions/{missionId}/status`
- **Request Body**: `{ status: "string" }`
- **Response**: `Mission` (200 OK)

## Agents
### List Agents for Mission
- **URL**: `GET /api/missions/{missionId}/agents`
- **Response**: `List<Agent>` (200 OK)
- **Note**: The frontend uses this to initialize the AgentGrid. Subsequent updates arrive via WebSockets.

## Timeline
### Get Audit Logs
- **URL**: `GET /api/workspaces/{workspaceId}/timeline`
- **Response**: `List<TimelineEntry>` (200 OK)

---

# WebSocket STOMP Endpoints

- **Broker Connection**: `ws://localhost:8080/ws`
- **Workspace Missions Topic**: `/topic/workspace/{workspaceId}/missions`
  - Receives `MissionEvent` payloads.
- **Mission Agents Topic**: `/topic/mission/{missionId}/agents`
  - Receives `AgentEvent` payloads.
