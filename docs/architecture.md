# HivePilot Architecture

## Overall Architecture
HivePilot is an Event-Driven Multi-Agent Orchestration SaaS platform. It consists of a React/Vite frontend and a Java Spring Boot backend, backed by PostgreSQL. The system orchestrates multiple autonomous agents (e.g., Architect, Backend Engineer, Frontend Engineer) in parallel to fulfill software development "Missions" for users.

## Module Relationships
- **Frontend (React)**: Communicates with the backend via REST for standard CRUD operations and subscribes to STOMP WebSockets for real-time agent output streaming.
- **Backend (Spring Boot)**: Exposes REST APIs, manages PostgreSQL transactions via JPA/Hibernate, and handles internal event routing via Spring's `ApplicationEventPublisher`.
- **Database (PostgreSQL)**: Stores all persistent state (Workspaces, Missions, Agents, Timeline).

## Request Flow
1. **User Action**: User creates a Mission in the UI via POST `/api/workspaces/{workspaceId}/missions`.
2. **Persistence**: `MissionService` saves the Mission to PostgreSQL and publishes a `MissionEvent` (`MISSION_CREATED`).
3. **Orchestration**: `PlannerService` asynchronously listens to `MISSION_CREATED`, generates a DAG of tasks, and triggers `AgentService.spawnAgent()`.
4. **Agent Execution**: `AgentService` saves Agents to the database and publishes `AgentEvent` (`AGENT_SPAWNED`). The async `AgentWorkerService` picks up these agents and simulates execution (to be replaced with actual LLMs).
5. **Real-time Streaming**: During execution, `AgentWorkerService` continuously updates the agent's `lastOutput` and publishes `AgentEvent` (`AGENT_OUTPUT_UPDATED`).
6. **WebSocket Forwarding**: `EventBusListener` intercepts all `MissionEvent` and `AgentEvent` broadcasts and forwards them to the Spring `SimpMessagingTemplate`.
7. **UI Update**: The frontend receives the WebSocket message and automatically invalidates the React Query cache, instantly refreshing the Agent terminal grids without manual polling.

## Data Flow
The system strictly relies on the Event Bus for decoupling. 
Controllers never directly call the Orchestrator. The Orchestrator never directly calls the WebSockets. Data flows linearly: Service -> Event Publisher -> Listeners (Planners/WebSockets).

## Important Design Decisions
- **Event-Driven**: Allows easy addition of new agent types and analytics without modifying core CRUD services.
- **WebSocket over SSE**: WebSockets were chosen because the UI requires bidirectional capabilities (in the future, users will type into the simulated terminals to interrupt agents).
- **Relational Integrity**: PostgreSQL is used to strictly enforce constraints between Workspaces, Missions, and their spawned Agents.
