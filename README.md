# BridgeMind

BridgeMind is an agentic software-development workspace: a human engineering manager creates missions, a planner decomposes them into dependent tasks, and role-based agents execute those tasks inside isolated workspaces.

It is not a chat application or a single-model API wrapper. Its long-term purpose is to orchestrate real external coding-agent runtimes—such as Codex CLI, Claude Code, Antigravity, Gemini CLI, and Aider—while preserving workspace isolation, visibility, review, and human control.

## Architecture

```text
Workspace
  → Mission
  → Planner
  → Mission Task
  → Agent (role)
  → AgentRuntime
  → Isolated Docker workspace
  → External coding-agent CLI
  → Git repository
```

- **Frontend:** React, TypeScript, Vite, React Query, Zustand, Tailwind.
- **Backend:** Java 17, Spring Boot, JPA/PostgreSQL, Spring events, STOMP WebSockets.
- **Infrastructure:** PostgreSQL/pgvector, Redis, and Docker-based execution boundaries.

Read [agent.md](agent.md) for the non-negotiable architecture rules. Current implementation state is recorded in [PROJECT_STATE.md](PROJECT_STATE.md).

## Current capabilities

- Workspace, mission, agent, task, timeline, and WebSocket event flow.
- Persisted dependency-aware task graph: architecture work runs before backend, frontend, and DevOps tasks.
- After-commit event handling, scoped REST routes, and structured API errors.
- Configured workspace provisioning and a hardened Docker execution boundary:
  - image allowlist
  - network disabled
  - dropped capabilities and no-new-privileges
  - read-only root filesystem
  - CPU, memory, PID, and timeout limits
- Extensible `AgentRuntime` foundation with persisted execution state, logs, cancellation, runtime registry, REST endpoints, and runtime WebSocket events.

The included `simulated` runtime is a foundation only. Real coding-agent adapters have not been enabled yet.

## Next milestone

Implement the first real containerized coding-agent adapter—recommended: **Codex CLI**—behind the `AgentRuntime` interface.

Detailed, safety-focused implementation instructions for that work are in [docs/handover.md](docs/handover.md). Do not run coding-agent CLIs directly on the host or tightly couple the planner to a specific runtime.

## Local development

### Prerequisites

- Java 17 and Maven
- Node.js 20+
- Docker Desktop (needed for PostgreSQL/Redis and Testcontainers)

### Infrastructure

```bash
cd infrastructure
docker compose up -d
```

Copy `.env.example` to `.env` and set appropriate local credentials before starting services.

### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm ci
npm run dev
```

The frontend runs on `http://localhost:5173`.

## Verification

```bash
cd frontend
npm test
npm run build

cd ../backend
mvn test
```

Backend integration tests use Testcontainers and require a working Docker daemon. Docker-dependent execution tests are intentionally not expected to run in environments without Docker.

## Key documentation

- [Project state](PROJECT_STATE.md)
- [Handover and implementation instructions](docs/handover.md)
- [Architecture](docs/architecture.md)
- [REST and WebSocket API](docs/api.md)
- [Modules](docs/modules.md)
- [Coding standards](docs/coding-standards.md)

## Contribution rules

- Preserve the Workspace → Mission → Planner → Task → Agent → Runtime hierarchy.
- Keep runtimes provider-agnostic and containerized.
- Never execute untrusted commands on the host.
- Emit timeline/events for significant state changes.
- Keep commits focused; build and test before each milestone commit.
- Before major UI work, propose the visual direction, component hierarchy, interaction model, animation ideas, typography, and color system. The UI should feel like a premium desktop application, not a generic admin dashboard.
