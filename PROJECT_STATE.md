# Project State

## Completed work

- **Milestone 1 — Reliability baseline** (`0581f6e`)
  - Single after-commit WebSocket bridge, scoped nested API routes, consistent error responses, cascading Workspace/Mission deletion, repaired frontend type checks/tests, and frontend CI test coverage.
- **Milestone 2 — Persisted mission task graph and deterministic scheduling**
  - Added mission tasks and dependency edges.
  - Replaced fixed planner fan-out with a graph: Architecture first, then Backend, Frontend, and DevOps in parallel.
  - Agents are associated with their assigned task; completion unlocks dependent work.
  - Added concurrency protection for task claims, mission/task failure propagation, after-commit worker execution, and task lifecycle cascading when a mission is deleted.
  - Added targeted task-graph tests.
- **Milestone 3 — Hardened workspace execution boundary**
  - Added configured workspace provisioning instead of machine-specific filesystem paths.
  - Restricted Docker execution to an image allowlist with disabled networking, dropped capabilities, no-new-privileges, read-only root filesystem, PID/memory/CPU limits, and timeouts.
  - Added execution-started/completed WebSocket lifecycle events and policy unit tests.
- **Milestone 4 — AgentRuntime foundation**
  - Added an extensible runtime contract, registry, persisted runtime execution state/logs, cancellation semantics, REST entry points, and runtime WebSocket events.
  - Added a simulated runtime only; real external coding-agent adapters are intentionally deferred.
- **Milestone 5 — External coding-agent adapters (Codex CLI)**
  - Created a pinned Docker container for `codex-cli`.
  - Added `CodexRuntime` adapter implementing `AgentRuntime`.
  - Used hardened Docker boundary with resource constraints and credential injection via mounted read-only volume.
  - Implemented asynchronous execution with bounded WebSocket log streaming and lifecycle state mapping.
  - Added Docker-free state-transition unit tests for `CodexRuntime`.
- **Milestone 6 — Human-in-the-loop and Premium UX**
  - Added workspace memory entity and repository.
  - Implemented Git-native diff generation and review/approval workflow.
  - Built the premium Mission Control UX (Neon hacker-terminal, 4-pane agent grid).
- **Milestone 7 — Multi-runtime adapters**
  - Added `ClaudeCodeRuntime` adapter (id: `claude-code`, image: `claude-code:latest`).
  - Added `AiderRuntime` adapter (id: `aider`, image: `aider:latest`).
  - Both follow the CodexRuntime pattern: Docker isolation, credential mounting, bounded logs, `REVIEW_PENDING` on success.
  - Added Docker-free unit tests for both adapters.
- **Milestone 8 — JWT authentication and multi-tenant security**
  - Added `User` entity with email, password hash, and role (ADMIN/USER).
  - Implemented `JwtService` for HMAC-SHA256 token generation/validation.
  - Added `JwtAuthenticationFilter` for stateless request authentication.
  - Created `AuthController` with register and login endpoints at `/api/auth/**`.
  - Configured `SecurityConfig` with BCrypt, CORS, stateless sessions, and protected routes.
  - Added 13 security tests (7 JWT unit + 6 auth controller integration).
- **Milestone 9 — Production deployment configuration**
  - Created multi-stage Dockerfiles for backend (JDK 21) and frontend (Node 20 + nginx).
  - Added nginx reverse proxy with SPA fallback, API proxying, gzip, and cache headers.
  - Updated docker-compose with backend/frontend services, healthchecks, and service dependency ordering.
  - Added `.dockerignore` files for both backend and frontend.
  - Added `docker-build` CI job that validates Docker images after tests pass.

## Remaining work

1. Add observability (structured logging, metrics, tracing).
2. Database migrations (Flyway) to replace `ddl-auto: update`.
3. Cloud deployment (Docker Compose to Kubernetes or Cloud Run).
4. Additional agent adapters (Gemini CLI, Cursor, Windsurf).

## Current branch

`master`

## Next recommended task

Add Flyway database migrations to replace Hibernate's `ddl-auto: update` for production safety. Alternatively, add structured logging and observability with Micrometer/Prometheus.
