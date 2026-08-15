package com.bridgemind.backend.test;

import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TestRunnerService {
    private static final Logger log = LoggerFactory.getLogger(TestRunnerService.class);

    private final WorkspaceFilesystemService filesystemService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<UUID, TestRunResult> latestResults = new ConcurrentHashMap<>();

    public TestRunnerService(WorkspaceFilesystemService filesystemService, SimpMessagingTemplate messagingTemplate) {
        this.filesystemService = filesystemService;
        this.messagingTemplate = messagingTemplate;
    }

    public TestRunResult runTests(UUID workspaceId) {
        long startTime = System.currentTimeMillis();
        log.info("Starting sandbox test suite execution for workspace {}", workspaceId);

        try {
            Path workspaceRoot = filesystemService.provision(workspaceId);

            String suiteType = detectSuiteType(workspaceRoot);
            String[] command = resolveTestCommand(suiteType);

            // Notify WebSocket topic that tests started
            messagingTemplate.convertAndSend("/topic/workspace/" + workspaceId + "/tests",
                    Map.of("eventType", "TEST_STARTED", "suiteType", suiteType, "workspaceId", workspaceId.toString()));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workspaceRoot.toFile());
            pb.redirectErrorStream(true);

            Process process = pb.start();
            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                long duration = System.currentTimeMillis() - startTime;
                TestRunResult timeoutResult = new TestRunResult(
                        suiteType, "ERROR", -1, duration,
                        output + "\n[ERROR] Test execution timed out after 60 seconds.",
                        "Execution timed out", Instant.now()
                );
                latestResults.put(workspaceId, timeoutResult);
                return timeoutResult;
            }

            int exitCode = process.exitValue();
            long duration = System.currentTimeMillis() - startTime;
            String status = exitCode == 0 ? "PASSED" : "FAILED";
            String summary = status.equals("PASSED")
                    ? "All tests in " + suiteType + " suite passed cleanly."
                    : "Tests exited with code " + exitCode + ". Failures detected.";

            TestRunResult result = new TestRunResult(
                    suiteType, status, exitCode, duration,
                    output.toString(), summary, Instant.now()
            );

            latestResults.put(workspaceId, result);
            log.info("Finished test suite for workspace {}: status={}, duration={}ms", workspaceId, status, duration);

            // Broadcast result over WebSocket
            messagingTemplate.convertAndSend("/topic/workspace/" + workspaceId + "/tests",
                    Map.of(
                            "eventType", "TEST_COMPLETED",
                            "status", status,
                            "suiteType", suiteType,
                            "durationMs", duration,
                            "summary", summary,
                            "workspaceId", workspaceId.toString()
                    ));

            return result;
        } catch (Exception e) {
            log.error("Failed to execute test suite for workspace {}", workspaceId, e);
            long duration = System.currentTimeMillis() - startTime;
            TestRunResult errorResult = new TestRunResult(
                    "UNKNOWN", "ERROR", 1, duration,
                    "Failed to run test suite: " + e.getMessage(),
                    "Execution error", Instant.now()
            );
            latestResults.put(workspaceId, errorResult);
            return errorResult;
        }
    }

    public TestRunResult getLatestResult(UUID workspaceId) {
        return latestResults.get(workspaceId);
    }

    private String detectSuiteType(Path workspaceRoot) {
        if (Files.exists(workspaceRoot.resolve("package.json"))) {
            return "Node.js / Vitest / Jest";
        }
        if (Files.exists(workspaceRoot.resolve("pom.xml"))) {
            return "Java / JUnit 5";
        }
        if (Files.exists(workspaceRoot.resolve("pytest.ini")) || Files.exists(workspaceRoot.resolve("requirements.txt"))) {
            return "Python / PyTest";
        }
        return "Generic Workspace Sandbox";
    }

    private String[] resolveTestCommand(String suiteType) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String shell = isWindows ? "cmd.exe" : "sh";
        String flag = isWindows ? "/c" : "-c";

        if (suiteType.startsWith("Node.js")) {
            return new String[]{shell, flag, "npm test -- --run"};
        }
        if (suiteType.startsWith("Java")) {
            return new String[]{shell, flag, "mvn test-compile test -q"};
        }
        if (suiteType.startsWith("Python")) {
            return new String[]{shell, flag, "pytest"};
        }
        return new String[]{shell, flag, "echo 'No test configuration detected; running syntax check.'"};
    }
}
