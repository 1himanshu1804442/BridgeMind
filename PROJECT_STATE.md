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

## Remaining work

1. Milestone 4: provider-agnostic LLM layer and first provider integration.
2. Milestone 5: Redis working memory and Postgres/pgvector long-term memory.
3. Milestone 6: Git diff, review, approval, and commit workflow.
4. Milestone 7: premium Mission Control UX. Present the visual direction, hierarchy, interaction model, motion, typography, and color system before major UI implementation.
5. Milestone 8: authentication, tenancy, observability, deployment, and release readiness.

## Current branch

`master`

## Next recommended task

Start Milestone 4 by defining the provider interface and selecting the first provider/credential-storage approach. Keep all provider-specific code behind the common abstraction.
