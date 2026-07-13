# Current Tasks & Tech Debt

## Next Recommended Tasks (For Codex)
1. **Remove Mock Execution**: The `AgentWorkerService` currently simulates agent actions by looping through an array of strings with a 1-second `Thread.sleep()`. This needs to be replaced with a real LangChain4j integration or direct LLM API calls.
2. **Implement Agent Sub-graph**: While `PlannerService` spawns 4 agents, they currently run completely independently without awaiting each other. Implement a true DAG execution flow (e.g. Backend Engineer waits for Architect to finish).
3. **Docker Execution Service**: Wire the `DockerExecutionService` (Phase 3 prototype) into the `AgentWorkerService` so agents can actually execute bash scripts and compile code in an isolated volume.

## Known Bugs
- **Integration Tests in Sandbox**: The Backend Integration Tests (which use Testcontainers) frequently fail when run inside cloud-based CI/CD or sandboxes because the environment lacks access to a root Docker daemon. This is an environment issue, not a code logic flaw. You must configure GitHub actions properly or run `mvn spring-boot:run -DskipTests` during rapid local prototyping.

## Technical Debt
- **Missing Authentication**: The API currently lacks authentication. A user can fetch any Workspace by ID.
- **Hardcoded State**: The frontend auto-initializes the "Default Workspace" and sets it to active immediately.
- **Hardcoded DB Credentials**: While `application.yml` was updated to support env vars, there are still some lingering hardcoded defaults in the docker-compose fallbacks.
- **Global Error Handling on Websockets**: STOMP errors are not explicitly bubbled up to the UI yet.
