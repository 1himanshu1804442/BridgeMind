# Architectural Decisions

## 1. Java Spring Boot for Backend
**Why**: Agentic workflows require massive, complex state management and strict data integrity. Spring Boot's JPA/Hibernate layer enforces rigid schema rules. Furthermore, Spring's native `ApplicationEventPublisher` makes it extremely easy to decouple event broadcasting (Mission Created, Agent Spawned) from HTTP controllers.
**Alternatives Considered**: Node.js/Express (rejected due to loose typing and unstructured event handling in larger monoliths), Python/FastAPI (good for AI, but lacks the robust enterprise ORM of Java).

## 2. Event-Driven UI with WebSockets
**Why**: When 4+ AI agents are generating code and logs simultaneously, polling the backend every 500ms via HTTP REST is unscalable and creates latency jitter. We opted for STOMP WebSockets hooked directly into the Spring EventBus to push log streams down to the React Query cache natively.
**Alternatives Considered**: Server-Sent Events (SSE). Rejected because SSE is strictly one-way. While fine for logs, Phase 7 requires users to *interact* and type into the AI's terminal. WebSockets natively support this bidirectional need.

## 3. "No-Mistakes Pipeline" (TDD First)
**Why**: AI agents suffer from context drift when refactoring undocumented or untested code. By forcing the AI (or developer) to write Integration Tests using Testcontainers *first*, we guarantee the behavior is locked into the PostgreSQL schema constraints before the LLM hallucination risk begins.

## 4. Hardcoded Hex Backgrounds for React Bouncing
**Why**: Apple devices enforce a rubber-band overscroll effect. If the UI relies entirely on class-based Tailwind (`<html class="dark">`) to apply dark backgrounds, the browser engine sometimes fails to paint the bounce region correctly, resulting in a blinding white flash. We bypassed Tailwind variable resolution for the absolute base layer (`html, body, #root`) and hardcoded the hex color (`#09090b`).
