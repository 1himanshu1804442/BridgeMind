package com.bridgemind.backend.workspace;

import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class WorkspaceFileServiceTest {

    @TempDir
    Path tempDir;

    private WorkspaceFilesystemService filesystemService;
    private WorkspaceFileService fileService;
    private UUID workspaceId;

    @BeforeEach
    void setUp() throws IOException {
        filesystemService = Mockito.mock(WorkspaceFilesystemService.class);
        workspaceId = UUID.randomUUID();
        when(filesystemService.provision(any(UUID.class))).thenReturn(tempDir);
        fileService = new WorkspaceFileService(filesystemService);
    }

    @Test
    void testSaveAndReadContent() {
        fileService.saveFileContent(workspaceId, "src/main.ts", "console.log('Hello ADE');");
        String content = fileService.getFileContent(workspaceId, "src/main.ts");

        assertThat(content).isEqualTo("console.log('Hello ADE');");
    }

    @Test
    void testListFilesTree() throws IOException {
        Files.createDirectories(tempDir.resolve("src"));
        Files.writeString(tempDir.resolve("src/index.ts"), "export const a = 1;");
        Files.writeString(tempDir.resolve("README.md"), "# Project");

        List<WorkspaceFileNode> files = fileService.listFiles(workspaceId);

        assertThat(files).hasSize(2);
        WorkspaceFileNode srcDir = files.stream().filter(f -> f.name().equals("src")).findFirst().orElseThrow();
        assertThat(srcDir.isDirectory()).isTrue();
        assertThat(srcDir.children()).hasSize(1);
        assertThat(srcDir.children().get(0).name()).isEqualTo("index.ts");
    }

    @Test
    void testPathTraversalSecurityCheck() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileService.getFileContent(workspaceId, "../../secret.txt");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fileService.saveFileContent(workspaceId, "../../../etc/passwd", "root");
        });
    }
}
