# ⚡ BridgeMind Backend Service

The high-performance Java 21 Spring Boot backend orchestrator for **BridgeMind — The Autonomous Development Environment (ADE)**.

---

## 🏛️ Architecture & Modules

* **`com.bridgemind.backend.agent`**: Agent JPA entity, lifecycle status state machine (`CREATED`, `WAITING`, `RUNNING`, `THINKING`, `CALLING_TOOL`, `GENERATING_DIFF`, `REVIEW_PENDING`, `COMPLETED`, `FAILED`), and role assignment (`COORDINATOR`, `BUILDER`, `SCOUT`, `REVIEWER`, `BACKEND_ENGINEER`, `FRONTEND_ENGINEER`, etc.).
* **`com.bridgemind.backend.mcp`**: Anthropic Model Context Protocol (MCP) JSON-RPC 2.0 and Server-Sent Events (SSE) server for external AI tools (Cursor, Claude Code, OpenAI Codex CLI, Cline).
* **`com.bridgemind.backend.test`**: Automated sandbox test suite runner with framework auto-detection (`Node.js/Vitest`, `Java/JUnit 5`, `Python/PyTest`) and live STOMP streaming.
* **`com.bridgemind.backend.task`**: Directed Acyclic Graph (DAG) task scheduler with database row locks (`PESSIMISTIC_WRITE`) preventing race conditions.
* **`com.bridgemind.backend.security`**: Spring Security 6, JWT stateless authentication, HMAC-SHA256 token verification, and BCrypt password encryption.
* **`com.bridgemind.backend.review`**: Git diff generator and human-in-the-loop commit review service.
* **`com.bridgemind.backend.execution`**: Hardened Docker container boundary with resource limits (CPU, memory, PIDs, read-only root, no-new-privileges, network isolation).
* **`com.bridgemind.backend.runtime`**: Containerized external runtime adapters (`CodexRuntime`, `ClaudeCodeRuntime`, `AiderRuntime`).
* **`com.bridgemind.backend.workspace`**: Workspace filesystem provisioning, path-traversal prevention, and live playable HTML5 preview server.

---

## 🛠️ Tech Stack

* **Language**: Java 21 / Java 17
* **Framework**: Spring Boot 3.2.5 (Spring Web, Spring Data JPA, Spring Security, Spring WebSocket)
* **Database**: PostgreSQL 16
* **Cache**: Redis 7
* **Testing**: JUnit 5, Mockito, AssertJ, Spring MockMvc

---

## ⚡ Development & Scripts

### 1. Run Tests
```bash
mvn clean test
```

### 2. Start Application Locally
```bash
mvn spring-boot:run
```
Service listens on `http://localhost:8080`.
