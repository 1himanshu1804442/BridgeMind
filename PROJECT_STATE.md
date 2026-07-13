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

## Remaining work

1. Add multi-tenant security, deployment, observability, and release readiness.

## Current branch

`master`

## Next recommended task

Have Gemini CLI follow the implementation of workspace memory, diffs, and Git review workflows. Alternatively, build additional external runtime adapters (Claude Code, Aider, etc.) mirroring the `CodexRuntime` pattern.
