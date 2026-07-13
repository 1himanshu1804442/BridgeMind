# Coding Standards

## Global Paradigms
- **"No-Mistakes Pipeline"**: Test-Driven Development (TDD) is enforced. Backend Integration Tests (via Testcontainers) and Frontend React Testing Library tests must be written *before* implementation logic.
- **No Placeholders**: `// TODO` or `...rest of code` comments are strictly forbidden during code generation. All committed code must be fully implemented and functional.
- **Explain the "Why"**: Code logic that is non-obvious must be documented with brief comments explaining *why* an approach was taken.

## Backend (Java Spring Boot)
- **Strict Layered Architecture**:
  - `Controller` handles HTTP routing and DTO validation. Absolutely zero business logic.
  - `Service` handles core business logic and transactional boundaries.
  - `Repository` handles database IO.
- **Dependency Injection**: Always use Constructor Injection. Never use `@Autowired` on fields to ensure immutability and testability.
- **Error Handling**: Use `@ControllerAdvice` for global exception handling. Do not swallow exceptions in empty `catch` blocks. Uncaught errors must print standard Spring ProblemDetails/JSON formats.
- **Logging**: Use SLF4J (via `LoggerFactory` or `@Slf4j`). Emit `log.info()` on significant state changes (e.g. "Agent spawned") and `log.error()` heavily during exceptions.

## Frontend (React + Vite)
- **Functional React**: Class components are strictly prohibited. Use Functional Components and React Hooks exclusively.
- **Component Folder Structure**: `src/components`, `src/pages`, `src/services`, `src/hooks`.
- **API Separation**: UI components must never call `fetch` or `axios` directly. All network requests must be abstracted away into `src/services/api.ts` and consumed via React Query hooks.
- **Styling (Tailwind CSS)**: 
  - Standardize around Tailwind utility classes and `shadcn/ui` structural patterns.
  - **Seamless React Bounce Rule**: A strict CSS reset is enforced. `html, body, #root` must explicitly have `margin: 0; padding: 0; min-height: 100vh;` and a **hardcoded hex background color** matching the dark theme to prevent white flashes during overscroll bouncing on macOS/iOS. Dynamic `@apply bg-background` is forbidden here.
