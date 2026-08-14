package com.bridgemind.backend.execution;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "execution.docker")
public class DockerExecutionProperties {
    private Path workspaceRoot = Path.of("./workspaces");
    private String allowedImages = "alpine:3.20,codex-cli:latest,claude-code:latest,aider:latest,deepseek-ai/deepseek-v4:latest,google/antigravity-agy:latest";
    private long timeoutSeconds = 60;
    private String memoryLimit = "512m";
    private String cpuLimit = "1.0";

    public Path getWorkspaceRoot() { return workspaceRoot; }
    public void setWorkspaceRoot(Path workspaceRoot) { this.workspaceRoot = workspaceRoot; }
    public long getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(long timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    public String getMemoryLimit() { return memoryLimit; }
    public void setMemoryLimit(String memoryLimit) { this.memoryLimit = memoryLimit; }
    public String getCpuLimit() { return cpuLimit; }
    public void setCpuLimit(String cpuLimit) { this.cpuLimit = cpuLimit; }
    public void setAllowedImages(String allowedImages) { this.allowedImages = allowedImages; }

    public Set<String> allowedImages() {
        return Arrays.stream(allowedImages.split(","))
                .map(String::trim).filter(value -> !value.isEmpty()).collect(Collectors.toUnmodifiableSet());
    }
}
