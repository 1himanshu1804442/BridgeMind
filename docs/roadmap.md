# Roadmap

## Phase 1: Infrastructure & Scaffolding (Completed)
- Set up PostgreSQL and Redis via Docker Compose.
- Bootstrap Vite React frontend and Spring Boot backend.
- Implement CRUD operations for Workspaces.
- Build basic Multiplexer UI (AgentGrid, Sidebar).

## Phase 2: Agent Orchestration Core (Completed)
- Create Event-driven DAG logic (`PlannerService`).
- Define Agent and Mission entities.
- Implement STOMP WebSockets for bidirectional event streams.
- Simulate agent execution delays and output streams via `AgentWorkerService`.

## Phase 3: Live Audit & Polish (Completed)
- Build Timeline component for chronological UI logging.
- Implement memory scrolling and terminal animations (pulsing cursors).
- Auto-invalidating React Query cache based on WebSocket `AgentEvent` broadcasts.

## Phase 4: Sandboxing (In Progress)
- Spin up isolated Docker containers programmatically from Java using `ProcessBuilder`.
- Mount dynamic workspaces into containers.
- Enforce network and file isolation.

## Phase 5: LLM Integration (Planned)
- Remove simulated delays.
- Integrate LangChain4j for prompt execution and tool binding.
- Hook into Anthropic/OpenAI APIs.

## Phase 6: Multi-Tenant & SaaS (Planned)
- Integrate Stripe API for metered agent billing.
- Implement Clerk/Auth0 for JWT authentication.
- Add rate limiting and abuse prevention.
