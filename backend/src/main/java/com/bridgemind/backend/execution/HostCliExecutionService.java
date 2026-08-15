package com.bridgemind.backend.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Service that executes real host CLI tools (agy, codex, gh copilot, claude, aider, etc.)
 * directly in the workspace directory, using the user's host environment and paid CLI sessions.
 */
@Service
public class HostCliExecutionService {

    private static final Logger log = LoggerFactory.getLogger(HostCliExecutionService.class);
    private final WorkspaceFilesystemService filesystemService;

    public HostCliExecutionService(WorkspaceFilesystemService filesystemService) {
        this.filesystemService = filesystemService;
    }

    /**
     * Executes a prompt with Google Antigravity AGY CLI on host.
     */
    public int executeAgy(UUID workspaceId, String prompt, Consumer<String> outputConsumer) {
        String userHome = System.getProperty("user.home", "C:\\Users\\hy180");
        File agyExe = new File(userHome + "\\AppData\\Local\\agy\\bin\\agy.exe");
        List<String> commandList = new ArrayList<>();
        if (agyExe.exists()) {
            commandList.add(agyExe.getAbsolutePath());
        } else {
            commandList.add("agy");
        }
        commandList.add("--dangerously-skip-permissions");
        commandList.add("-p");
        commandList.add(prompt);
        return executeCommandList(workspaceId, commandList, outputConsumer);
    }

    /**
     * Executes a prompt with OpenAI Codex CLI on host.
     */
    public int executeCodex(UUID workspaceId, String prompt, Consumer<String> outputConsumer) {
        List<String> commandList = new ArrayList<>();
        commandList.add("cmd.exe");
        commandList.add("/c");
        commandList.add("codex");
        commandList.add("exec");
        commandList.add("--dangerously-bypass-approvals-and-sandbox");
        commandList.add("--skip-git-repo-check");
        commandList.add(prompt);
        return executeCommandList(workspaceId, commandList, outputConsumer);
    }

    /**
     * Executes a command on the host OS inside the workspace directory, streaming output chunks via callback.
     */
    public int executeHostCommand(UUID workspaceId, String command, Consumer<String> outputConsumer) {
        String lower = command.toLowerCase().trim();
        if (lower.startsWith("agy ") || lower.equals("agy")) {
            String prompt = command.length() > 3 ? command.substring(3).trim() : "hello";
            return executeAgy(workspaceId, prompt, outputConsumer);
        }
        if (lower.startsWith("codex ") || lower.equals("codex")) {
            String prompt = command.length() > 5 ? command.substring(5).trim() : "hello";
            return executeCodex(workspaceId, prompt, outputConsumer);
        }

        List<String> commandList = new ArrayList<>();
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (isWindows) {
            commandList.add("cmd.exe");
            commandList.add("/c");
            commandList.add(command);
        } else {
            commandList.add("/bin/sh");
            commandList.add("-c");
            commandList.add(command);
        }
        return executeCommandList(workspaceId, commandList, outputConsumer);
    }

    public int executeCommandList(UUID workspaceId, List<String> commandList, Consumer<String> outputConsumer) {
        log.info("Executing host process for workspace {}: {}", workspaceId, commandList);
        try {
            Path workspaceDir = filesystemService.provision(workspaceId);
            ProcessBuilder pb = new ProcessBuilder(commandList);
            pb.directory(workspaceDir.toFile());
            pb.redirectErrorStream(true);

            Process process = pb.start();
            // Crucial: immediately close STDIN so child CLIs (codex, agy) do not block waiting for input!
            try {
                process.getOutputStream().close();
            } catch (Exception ignored) {}

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (outputConsumer != null) {
                        outputConsumer.accept(line);
                    }
                }
            }
            
            boolean completed = process.waitFor(60, java.util.concurrent.TimeUnit.SECONDS);
            if (!completed) {
                log.warn("Host process timed out after 60s: {}", commandList);
                process.destroyForcibly();
                if (outputConsumer != null) {
                    outputConsumer.accept("[Process timed out after 60s - terminated]");
                }
                return -1;
            }

            int exitCode = process.exitValue();
            log.info("Host process {} completed with exit code: {}", commandList, exitCode);
            return exitCode;
        } catch (Exception e) {
            log.error("Failed to execute host process: {}", commandList, e);
            if (outputConsumer != null) {
                outputConsumer.accept("[Host execution notice: " + e.getMessage() + "]");
            }
            return -1;
        }
    }
}
