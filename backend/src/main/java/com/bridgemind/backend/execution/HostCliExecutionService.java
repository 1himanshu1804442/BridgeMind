package com.bridgemind.backend.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
     * Resolves the full path or binary name for standard CLI tools on Windows/Linux.
     */
    public String resolveCliCommand(String baseCommand) {
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        String userHome = System.getProperty("user.home", "C:\\Users\\hy180");
        String lower = baseCommand.toLowerCase().trim();

        // Specific resolution for Google Antigravity agy.exe
        if (lower.startsWith("agy ") || lower.equals("agy")) {
            File agyExe = new File(userHome + "\\AppData\\Local\\agy\\bin\\agy.exe");
            String agyBin = agyExe.exists() ? "\"" + agyExe.getAbsolutePath() + "\"" : "agy";
            String prompt = baseCommand.length() > 3 ? baseCommand.substring(3).trim() : "";
            if (!prompt.isEmpty() && !prompt.startsWith("-")) {
                return agyBin + " -p \"" + prompt.replace("\"", "\\\"") + "\"";
            }
            return agyBin + (prompt.isEmpty() ? "" : " " + prompt);
        }

        // Specific resolution for OpenAI Codex CLI
        if (lower.startsWith("codex ") || lower.equals("codex")) {
            String prompt = baseCommand.length() > 5 ? baseCommand.substring(5).trim() : "";
            if (!prompt.isEmpty() && !prompt.startsWith("exec")) {
                return "codex exec \"" + prompt.replace("\"", "\\\"") + "\"";
            }
            return "codex" + (prompt.isEmpty() ? "" : " " + prompt);
        }

        return baseCommand;
    }

    /**
     * Executes a prompt with Google Antigravity AGY CLI on host.
     */
    public int executeAgy(UUID workspaceId, String prompt, Consumer<String> outputConsumer) {
        String userHome = System.getProperty("user.home", "C:\\Users\\hy180");
        File agyExe = new File(userHome + "\\AppData\\Local\\agy\\bin\\agy.exe");
        String cmd;
        if (agyExe.exists()) {
            cmd = "\"" + agyExe.getAbsolutePath() + "\" -p \"" + prompt.replace("\"", "\\\"") + "\"";
        } else {
            cmd = "agy -p \"" + prompt.replace("\"", "\\\"") + "\"";
        }
        return executeHostCommand(workspaceId, cmd, outputConsumer);
    }

    /**
     * Executes a prompt with OpenAI Codex CLI on host.
     */
    public int executeCodex(UUID workspaceId, String prompt, Consumer<String> outputConsumer) {
        String cmd = "codex exec \"" + prompt.replace("\"", "\\\"") + "\"";
        return executeHostCommand(workspaceId, cmd, outputConsumer);
    }

    /**
     * Executes a command on the host OS inside the workspace directory, streaming output chunks via callback.
     *
     * @param workspaceId target workspace
     * @param command raw shell command (e.g. "agy build game", "codex", "gh copilot")
     * @param outputConsumer callback for stdout/stderr lines
     * @return exit code of the spawned process
     */
    public int executeHostCommand(UUID workspaceId, String command, Consumer<String> outputConsumer) {
        String resolvedCommand = resolveCliCommand(command);
        log.info("Executing host CLI command for workspace {}: {} (resolved: {})", workspaceId, command, resolvedCommand);
        try {
            Path workspaceDir = filesystemService.provision(workspaceId);

            boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
            ProcessBuilder pb;
            if (isWindows) {
                pb = new ProcessBuilder("cmd.exe", "/c", resolvedCommand);
            } else {
                pb = new ProcessBuilder("/bin/sh", "-c", resolvedCommand);
            }

            pb.directory(workspaceDir.toFile());
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (outputConsumer != null) {
                        outputConsumer.accept(line);
                    }
                }
            }
            int exitCode = process.waitFor();
            log.info("Host command '{}' completed with exit code: {}", command, exitCode);
            return exitCode;
        } catch (Exception e) {
            log.error("Failed to execute host command: {}", command, e);
            if (outputConsumer != null) {
                outputConsumer.accept("[Host execution notice: " + e.getMessage() + "]");
            }
            return -1;
        }
    }
}
