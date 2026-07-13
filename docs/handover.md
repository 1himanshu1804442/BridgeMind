# Antigravity Handover

**Repository:** BridgeMind / HivePilot  
**Branch:** `master`  
**Latest completed milestone commit:** `0581f6e` — `fix: stabilize event flow and API contracts`  
**Current state:** Milestone 2 is partially implemented and intentionally handed over before commit-level completion.

## Product intent

HivePilot is intended to be an agent development environment: an engineering manager creates a Workspace and Mission; a Planner decomposes work; role-based agents execute it; all state changes appear in a timeline and WebSocket stream. The architecture rules are in `agent.md`.

The desired hierarchy is:

```text
Workspace -> Mission -> Planner -> Task -> Agent -> Provider -> Tools
```

The codebase is currently an early prototype. It does **not** yet execute real LLMs or safe tools.

## Repository layout

- `backend/`: Java 17 Spring Boot 3.2.5, JPA/PostgreSQL, Spring events, STOMP WebSockets.
- `frontend/`: React 19, TypeScript, Vite, React Query, Zustand, Tailwind.
- `infrastructure/docker-compose.yml`: PostgreSQL/pgvector, Redis, pgAdmin only; it does not run the app services.
- `docs/`: product documentation and current technical notes.

## Completed work: Milestone 1

Commit `0581f6e` is committed and pushed to `origin/master`.

It made the existing simulated vertical slice reliable enough to build and test:

1. Removed `backend/.../event/EventBusListener.java`, which duplicated all WebSocket broadcasts. `WebSocketEventForwarder` is now the single outbound bridge.
2. Changed planner, timeline, and WebSocket listeners to `@TransactionalEventListener(AFTER_COMMIT, fallbackExecution = true)` so they do not act on rolled-back/uncommitted writes.
3. Scoped nested backend API operations to their parent path:
   - Mission get/update/delete validate `workspaceId`.
   - Agent get/update/delete validate `missionId`.
4. Corrected API contract drift:
   - `MissionCreateRequest` no longer redundantly accepts `workspaceId` in the body.
   - Frontend status route is now `/workspaces/{workspaceId}/missions/{missionId}/status`.
   - Frontend WebSocket envelope is typed and consumes `payload.details`.
5. Added cascading JPA parent relationships for Workspace -> Mission and Mission -> Agent deletion.
6. Standardized REST errors as `{ status, error, message }`, matching existing tests.
7. Tightened CORS to local frontend origins and added PATCH support.
8. Repaired the frontend build and test setup:
   - removed an unused import and invalid WebSocket property access;
   - added `npm test` (`vitest run`);
   - made React Query component tests provide `QueryClientProvider`;
   - guarded `scrollIntoView` for jsdom.
9. CI now uses `npm ci` and runs frontend tests.

### Verified Milestone 1

- `frontend`: `npm test` passed (3 tests), then `npm run build` passed.
- `backend`: `mvn -DskipTests compile` passed.
- `backend`: `mvn -Dtest=AgentWorkerServiceTest,PlannerServiceIntegrationTest test` passed.
- Full backend test suite still requires a usable Docker daemon for Testcontainers and was not runnable in this environment.

## Current uncommitted work: Milestone 2

The goal is to replace fixed four-agent fan-out with persisted mission tasks and dependency-aware scheduling.

### Files added

- `backend/src/main/java/com/bridgemind/backend/task/TaskStatus.java`
- `backend/src/main/java/com/bridgemind/backend/task/MissionTask.java`
- `backend/src/main/java/com/bridgemind/backend/task/MissionTaskRepository.java`
- `backend/src/main/java/com/bridgemind/backend/task/MissionTaskService.java`
- `backend/src/main/java/com/bridgemind/backend/task/MissionTaskController.java`

### Files modified

- `backend/src/main/java/com/bridgemind/backend/agent/Agent.java`
  - adds optional `MissionTask task` relation.
- `backend/src/main/java/com/bridgemind/backend/agent/AgentService.java`
  - adds `spawnAgentForTask(UUID taskId, String model)`.
- `backend/src/main/java/com/bridgemind/backend/planner/PlannerService.java`
  - rewrites planner to create the default graph and dispatch runnable tasks.
- `backend/src/test/java/com/bridgemind/backend/planner/PlannerServiceIntegrationTest.java`
  - updates older fan-out assertions to task-service assertions.

### Intended initial graph

```text
Architecture task
  ├─ Backend task
  ├─ Frontend task
  └─ DevOps task
```

The planner should initially claim and spawn only the Architecture task. When its agent completes, it should claim/spawn the three dependent tasks in parallel. When all tasks complete, it should mark the mission `DONE`.

### M2 implementation status

`MissionTaskService` currently:

- creates the graph exactly once per mission;
- lists tasks at `GET /api/missions/{missionId}/tasks`;
- claims PENDING tasks when every dependency is COMPLETE;
- marks tasks COMPLETE/FAILED;
- moves the Mission to PLANNING, RUNNING, and DONE.

`PlannerService` currently:

- reacts after mission creation;
- creates the graph;
- claims runnable tasks;
- spawns an agent for each claimed task;
- reacts to agent status events to complete/failed a task and dispatch the next level.

## Critical blockers to resolve before treating M2 as complete

These are from an independent review and are important. Do not merely commit M2 as feature-complete without resolving them.

1. **Worker may run before the agent row commits**
   - `AgentService.spawnAgentForTask` publishes `AGENT_SPAWNED` inside its transaction.
   - `AgentWorkerService` still uses async plain `@EventListener`.
   - It can try to update an uncommitted/nonexistent Agent and leave the task claimed as RUNNING.
   - Fix: make the worker an `@Async @TransactionalEventListener(phase = AFTER_COMMIT, fallbackExecution = true)` or adopt an outbox before introducing real distributed workers.

2. **Mission deletion will fail after task creation**
   - `MissionTask` has a non-null mission foreign key.
   - `Mission` currently cascades only agents, not tasks.
   - Fix: add a `@OneToMany(mappedBy = "mission", cascade = ALL, orphanRemoval = true)` task collection on `Mission`, hidden from JSON, or explicitly delete tasks in the correct order.

3. **Task claims are race-prone**
   - `claimRunnableTasks` reads, filters, and saves without a lock/version.
   - Concurrent completion events can create duplicate agents for the same task.
   - Fix: use pessimistic locking or optimistic `@Version` plus retry; have a unique agent-per-task policy. Treat agent spawn failure as a task failure/retry state, never leave an unexplained RUNNING task.

4. **Failure has no complete terminal policy**
   - `MissionStatus` now lacks a failure transition suitable for task graph execution (the M2 code currently cannot resolve dependents after failure correctly).
   - Fix: add a Mission failure state or cancellation state, define behavior for dependencies of failed work, emit timeline/WebSocket events, and ensure no task remains permanently PENDING.

5. **Lazy entity access inside async planner is fragile**
   - Planner obtains Agent via repository then reads lazy `task` and `mission` associations from an async listener.
   - Fix: move the transition lookup and state update into a dedicated `@Transactional` orchestration service, or query a projection containing `missionId`, `taskId`, and status.

6. **Current planner test masks scheduling errors**
   - `claimRunnableTasks` is a mock returning `null` by default, so the `for` loop can NPE after the already-verified call and the test may still pass.
   - Fix: stub it to return explicit tasks and assert `spawnAgentForTask` gets called exactly once per claimed task.

## Tests required for M2

Add and run these before finalizing the milestone:

1. Default plan persistence: four tasks and correct Architecture -> dependent edges.
2. Initial claim: only Architecture is runnable.
3. Architecture completion unlocks the three dependent tasks.
4. Final task completion marks the mission DONE.
5. Agent failure propagates to a defined mission/task terminal state with no deadlocked tasks.
6. Concurrent claim results in exactly one agent per task.
7. Mission deletion after task/agent creation succeeds.
8. Plan creation is idempotent if a mission-created event is replayed.
9. Agent worker handling occurs only after agent transaction commit.

## Important configuration / health notes

- Backend POM is Java **17**, while Dockerfile/CI/docs mention Java 21. Align this deliberately before production.
- `spring.jpa.hibernate.ddl-auto=update` is only suitable for local development; introduce migrations (e.g. Flyway) before deployment.
- `DockerExecutionService` remains a prototype and hardcodes `C:/Users/hy180/BridgeMind/workspaces`. Do not expose it to LLMs before Milestone 3 hardening.
- Full backend tests use Testcontainers and require Docker. `DockerExecutionServiceIntegrationTest` is also path-dependent and will not be portable to Linux CI as written.
- `backend/target/**` is tracked in Git. Build commands can modify it. It should be untracked in a focused cleanup milestone, but this handover commit intentionally stages all current changes at the user’s request.
- The frontend is intentionally not yet redesigned. Before major UI work, propose and receive approval for visual direction, component hierarchy, interaction model, animation, typography, and color system. Do not build a generic admin dashboard.

## Recommended continuation order

### AgentRuntime foundation (completed after this handover was first written)

The backend now has `runtime/AgentRuntime`, `RuntimeLaunchRequest`, persisted `RuntimeExecution`, `RuntimeStatus`, `RuntimeRegistry`, `AgentRuntimeService`, and a `simulated` runtime. Runtime lifecycle events are sent to `/topic/workspace/{workspaceId}/runtimes`.

### Instructions for Gemini CLI: implement external runtime adapters

Do **not** add direct OpenAI/Gemini/Anthropic API clients. BridgeMind orchestrates installed coding-agent CLIs through `AgentRuntime` implementations.

Implement one adapter at a time, starting with `CodexRuntime`, then repeat the same pattern for Claude Code, Antigravity, Gemini CLI, and Aider:

1. Build a dedicated container image that installs only that CLI at a pinned version. Never mount host credentials or run the CLI on the host.
2. Add a runtime bean whose `id()` is stable (for example, `codex-cli`) and whose `start` builds a strictly allowlisted Docker command using the Milestone 3 execution boundary.
3. Pass task instructions through a mounted, generated request file or a fixed stdin protocol—never concatenate untrusted text into a host shell command.
4. Stream stdout/stderr as bounded runtime-log events; persist a bounded tail in `RuntimeExecution` and redact secrets.
5. Map process exit, timeout, cancellation, and malformed output to terminal `RuntimeStatus` values. Cancellation must stop only the matching container/execution.
6. Add integration tests only where Docker is available; keep policy and state-transition tests Docker-free.
7. Keep the Planner dependent only on `AgentRuntime` and runtime IDs. Agent roles, tasks, and runtime selection must remain separate concerns.

Before enabling a real adapter, add an explicit runtime-image allowlist, a credential-injection design, command/resource limits, and an operator approval flow for commands that can modify the workspace.

1. Complete the six M2 blockers and the graph tests above.
2. Build/verify M2; update `PROJECT_STATE.md`; commit M2 separately if starting from a clean point after this handover.
3. Milestone 3: safe workspace lifecycle and Docker execution (no host paths, allowlisted images/commands, timeouts, resource and network limits, streamed tool events).
4. Milestone 4: provider interface and first LLM provider. This needs a product choice for provider and credential storage model.
5. Milestones 5–8: memory, Git review workflow, premium Mission Control UI, and multi-tenant/security/deployment.

## Git protocol

- Remote is configured as `origin`: `https://github.com/1himanshu1804442/BridgeMind.git`.
- Milestone 1 was pushed successfully.
- The user explicitly requested `git add .` followed by `git commit -m "handover to antigravity"` for this checkpoint. Perform/preserve that exact handover commit before taking over.
- After each future *completed* milestone: build, update `PROJECT_STATE.md`, make a focused commit, then push. Do not squash unrelated changes.
