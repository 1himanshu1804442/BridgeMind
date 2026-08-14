package com.bridgemind.backend.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HostCliExecutionServiceTest {

    @TempDir
    Path tempDir;

    private WorkspaceFilesystemService filesystemService;
    private HostCliExecutionService hostCliExecutionService;

    @BeforeEach
    void setUp() throws Exception {
        filesystemService = mock(WorkspaceFilesystemService.class);
        when(filesystemService.provision(any(UUID.class))).thenReturn(tempDir);
        hostCliExecutionService = new HostCliExecutionService(filesystemService);
    }

    @Test
    void executeHostCommand_runsSimpleEchoCommand() {
        UUID workspaceId = UUID.randomUUID();
        List<String> outputLines = new ArrayList<>();

        int exitCode = hostCliExecutionService.executeHostCommand(workspaceId, "echo BridgeMind Host CLI Ready", outputLines::add);

        assertThat(exitCode).isEqualTo(0);
        assertThat(outputLines).anyMatch(line -> line.contains("BridgeMind Host CLI Ready"));
    }
}
