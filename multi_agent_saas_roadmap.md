# The Agentic Workspace OS Roadmap (V3: Production-Grade)

**Vision:** *Your AI Engineering Team in One Workspace.*
**Core Concept:** The Operating System for AI software development. An event-driven, sandboxed environment where specialized AI agents collaborate autonomously.

---

## 🏗 Core Architecture (Event-Driven & Modular)
The backend is logically separated into decoupled modules communicating via an internal Event Bus and Task Queues.

`API Gateway` → `Workspace Service` → `Execution Engine` → `Provider Layer` → `Tool Layer`

### The Expanded Provider Interface (Java)
```java
public interface Provider {
    Stream<Response> stream(Prompt prompt);
    Completion complete(Prompt prompt);
    ToolResult executeTool(ToolRequest request);
    List<Model> models();
    Usage usage();
    boolean supportsTools();
    boolean supportsVision();
    boolean supportsThinking();
}
```

### The Dual-Memory System
*   **Working Memory (Short-term context):** Redis (Fast, transient state, pub/sub).
*   **Long Memory (RAG/Codebase understanding):** PostgreSQL + `pgvector` (Cheaper and simpler than Pinecone).

---

## 🛠 The Technology Stack (Optimized for AI Maintainability)
**Backend:**
*   Java 21 + Spring Boot 3 (Spring Security, Spring WebSockets)
*   Spring Events (Internal Event Bus) & Simple Task Queues (for async orchestration)
*   PostgreSQL (Data + `pgvector`) & Redis (Caching/PubSub)
*   Docker (For strictly isolated Workspace Sandboxing)

**Frontend:**
*   React + TypeScript (Strict typing prevents agent hallucinations)
*   Vite (Build tool)
*   Tailwind CSS + shadcn/ui (Rapid, consistent, modern UI)
*   React Query (Server state) & Zustand (Client state)

---

## 🚀 Phase 1: Workspace Infrastructure & Event Bus
**Goal:** Build the modular skeleton optimized for asynchronous workflows.
*   **Database (PostgreSQL):** Setup tables (`workspaces`, `agents`, `missions`, `timeline`).
*   **Workspace State Machine:** Implement ENUM states (`CREATED`, `RUNNING`, `PAUSED`, `FAILED`, `COMPLETED`).
*   **The Event Bus:** Implement Spring ApplicationEvents. *(e.g., When `AgentFinishedEvent` fires, the Timeline updates, the Frontend receives a WebSocket push, and the Planner wakes up).*
*   **Frontend (TS/Tailwind):** Scaffold the VS Code-style sidebar (Explorer, Git, Memory, Tasks, Timeline).

---

## 🚀 Phase 2: Agent Engine & Task Queues
**Goal:** Abstract the providers and manage long-running tasks asynchronously.
*   **Provider Layer:** Implement the expanded `Provider` interface for Gemini and Claude.
*   **Task Queue:** Never run agents on HTTP threads. Implement an internal Queue: `Mission -> Queue -> Worker -> Provider`.
*   **Agent UI Panels:** Build the React panels displaying: `Provider Icon` → `Task` → `Streaming Output` → `Tool Calls`.

---

## 🚀 Phase 3: Docker Sandboxing & Tool Execution
**Goal:** Safe, isolated environment for agents to run code.
*   **Workspace Containers:** Instead of running raw `ProcessBuilder` on the host machine, the backend spins up an isolated Docker container for *each* Workspace.
*   **Tool Execution:** The Java backend sends commands (like `npm run test`) to the specific Docker container and streams the `stdout` back through the WebSocket.
*   **Git Integration:** First-class Git tools inside the container. Agents produce diffs; the Engineering Manager (User) reviews and hits "Accept" to commit.

---

## 🚀 Phase 4: Missions & Parallel Orchestration (The Killer Feature)
**Goal:** The ultimate "vibe coding" experience.
*   **The Planner Agent:** Break down large tasks (e.g., "Implement OAuth") into a Directed Acyclic Graph (DAG) of sub-tasks.
*   **Parallel Missions:** Launch 8 agents simultaneously. Backend, Frontend, and Security agents work in parallel.
*   **The Reviewer:** A specialized agent that listens for `AgentFinishedEvents`, reviews the generated diffs, and merges them into the final Mission output.
*   **The Timeline:** A beautiful, real-time audit log tracking every event on the Event Bus.

---

## 🚀 Phase 5: Polish & Launch
*   **UI/UX:** Finalize the dark-mode shadcn/ui aesthetic. Ensure all spacing and micro-animations feel premium.
*   **Billing:** Integrate Stripe for a BYOK (Bring Your Own Key) subscription tier.
*   **Deployment:** Docker Compose scripts, GitHub Actions, and deployment to a reliable VPS (or AWS/GCP).
