# HivePilot Modules

## Backend Modules (Java Spring Boot)
Located in `src/main/java/com/bridgemind/backend/`

- **`workspace`**: Handles Workspace creation and retrieval.
- **`mission`**: Manages Mission lifecycle. Entry point for orchestrating tasks.
- **`agent`**: Agent lifecycle management. Responsible for creating agents and tracking their state (cost, elapsed time, status).
- **`planner`**: The Orchestrator. `PlannerService` listens to new missions and translates them into a DAG of specific Agent deployments.
- **`task`**: Persists mission task graphs, dependency edges, task lifecycle state, and atomic runnable-task claims.
- **`execution`**: Houses the simulated `AgentWorkerService`, workspace filesystem provisioning, and the hardened Docker execution boundary. Docker executions emit lifecycle events to the workspace WebSocket topic.
- **`timeline`**: Provides an audit log of all system events.
- **`event`**: Contains domain events (`MissionEvent`, `AgentEvent`) and the `EventBusListener` which forwards these events to WebSockets.
- **`config`**: Configuration for CORS, WebSockets (`WebSocketConfig`), and Global Exception Handling (`GlobalExceptionHandler`).

## Frontend Modules (React + Vite)
Located in `src/`

- **`features/workspace`**: 
  - `WorkspaceLayout.tsx`: Main layout, manages WebSocket connections and global state.
  - `AgentGrid.tsx`: Multiplexer terminal UI for viewing agents.
  - `Timeline.tsx`: Live audit log of events.
  - `Sidebar.tsx`: Navigation.
- **`hooks`**: React Query wrappers (`useAgents`, `useMissions`, `useTimeline`) and WebSocket state management (`useWebSocket`).
- **`services`**: `api.ts` contains raw fetch/Axios wrappers. Never called directly by UI components.
- **`store`**: Zustand for active workspace/mission state tracking.
- **`types`**: Centralized TypeScript definitions representing backend DTOs and entities.
