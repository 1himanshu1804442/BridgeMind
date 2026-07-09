# HivePilot - Repository Instructions

## Product Vision

HivePilot is an Agent Development Environment (ADE).

This is NOT:
- a chatbot
- a ChatGPT clone
- another AI IDE
- another Cursor clone

HivePilot is the Operating System for AI Software Development.

The user acts as an Engineering Manager.
AI Agents act as the Engineering Team.
Every feature should reinforce this mental model.

---

# Core Principles
Every architectural decision must satisfy these principles.
1. Provider Agnostic
2. Agent Oriented
3. Mission Driven
4. Workspace First
5. Event Driven
6. Git Native
7. Human Always In Control

---

# Core Architecture
Everything revolves around a Workspace.

Workspace
    ↓
Mission
    ↓
Planner
    ↓
Agent
    ↓
Provider
    ↓
Tools

Never bypass this hierarchy.

---

# Workspace
Workspace is the primary object.
Every entity belongs to exactly one Workspace.

Examples:
- Agents
- Missions
- Memory
- Git Repository
- Timeline
- Provider Keys
- Settings
- Files
- Logs

Do not create global state outside Workspace unless absolutely necessary.

---

# Missions
A Mission represents user intent.

Examples:
- Implement OAuth
- Refactor Authentication
- Build Landing Page
- Fix Memory Leak

Agents never execute arbitrary work.
Every task belongs to a Mission.

---

# Planner
Planner coordinates work.
Planner never edits code.
Planner only:
- decomposes work
- assigns work
- waits for dependencies
- merges completion state

Business logic should never bypass the Planner.

---

# Agents
Agents represent engineering roles.
Agents are NOT models.

**Correct:**
- Backend Engineer
- Frontend Engineer
- Security Auditor
- Reviewer
- QA Engineer
- Planner

**Incorrect:**
- Claude Agent
- Gemini Agent
- Codex Agent

The model is only the implementation.

---

# Providers
Providers are interchangeable.
Never write provider-specific business logic.
Every provider must implement the common Provider interface.
Adding a new provider should require minimal changes.

Examples:
- Gemini
- Claude
- OpenAI
- Groq
- OpenRouter
- Ollama
- DeepSeek

Future providers should plug into the existing abstraction.

---

# Tools
Agents never access the operating system directly.
Everything goes through Tools.

Examples:
- Read File
- Write File
- Git
- Terminal
- Docker
- Browser
- Search
- Database

Future tools should register automatically through the Tool Registry.

---

# Memory
There are three memory layers.

**Workspace Memory**
Shared by every Agent.

**Agent Working Memory**
Temporary context for a single task.

**Long Term Memory**
Persistent project knowledge.

Agents communicate through Workspace Memory.
Never communicate directly Agent → Agent.

---

# Event Driven Design
Long running work must communicate through Events.

Examples:
- MissionCreated
- MissionStarted
- MissionCompleted
- AgentSpawned
- AgentCompleted
- ToolExecuted
- DiffGenerated
- CommitCreated
- TimelineUpdated

Avoid tightly coupled service-to-service orchestration.

---

# Git Philosophy
Git is a first-class feature.
Agents never silently edit files.

Workflow:
Edit → Generate Diff → Review → Accept → Commit

The user always approves changes before merge.

---

# Timeline
Every important action creates a Timeline Event.
The Timeline is the audit log for the Workspace.

Users should always be able to answer:
- What happened?
- Who did it?
- When?
- Why?

---

# UI Philosophy
HivePilot is Mission Control.
The interface should communicate:
- Current Mission
- Current Agent
- Current Status
- Current Diff
- Current Cost
- Current Timeline

Avoid chat-centric layouts.
Prioritize visibility over minimalism.

---

# Backend Standards
Use Java Spring Boot.
Follow:
Controller → Service → Repository

Never place business logic inside Controllers.
Use constructor injection.
Keep services focused.

---

# Frontend Standards
React + TypeScript.
Prefer reusable components.
Keep components small.
Extract business logic into hooks.
API communication belongs inside services.

---

# State Management
UI state remains local whenever possible.
Workspace state belongs in global state.
Avoid prop drilling.
Prefer predictable state flow.

---

# Error Philosophy
Never silently fail.
Errors should:
- be logged
- appear in Timeline
- provide actionable context

---

# Security
Never expose provider API keys.
Never log secrets.
Never execute shell commands outside isolated workspaces.
Never trust model output.
Always validate Tool calls before execution.

---

# Scalability
Every architectural decision should assume:
- multiple users
- multiple workspaces
- multiple providers
- multiple agents
- long-running missions

Avoid shortcuts that block future scaling.

---

# Future Compatibility
The architecture should naturally support:
- Docker execution
- MCP
- Remote agents
- Local agents
- Voice agents
- Multi-user collaboration
- Cloud workspaces
- Plugin marketplace

Avoid designs that make these difficult later.

---

# Code Quality
Before adding new code:
1. Search for existing abstractions.
2. Reuse existing services.
3. Extend instead of duplicate.

Avoid creating parallel implementations.

---

# Definition of Done
A feature is complete only if:
- Architecture remains clean.
- Timeline events exist.
- Errors are handled.
- Logging is meaningful.
- UI states are complete.
- Code follows repository conventions.
- Documentation is updated if architecture changes.

---

# Non-Negotiable Rules
Never hardcode provider-specific logic.
Never bypass Mission → Planner → Agent.
Never bypass Tool abstractions.
Never edit files without producing a Diff.
Never break Workspace isolation.
Always prefer extensibility over quick hacks.
Every major feature should feel like it belongs inside an AI Operating System, not a traditional CRUD application.

---

# AI Self-Review
Before completing any task, verify:
- Does this duplicate existing code?
- Does this violate the Workspace → Mission → Planner → Agent architecture?
- Is this provider agnostic?
- Can another provider replace the current one without code changes?
- Is the implementation modular?
- If this project doubles in size, will this still be maintainable?

If any answer is "No", refactor before considering the task complete.
