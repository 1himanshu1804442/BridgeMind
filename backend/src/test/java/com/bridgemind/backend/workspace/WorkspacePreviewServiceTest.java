package com.bridgemind.backend.workspace;

import com.bridgemind.backend.execution.WorkspaceFilesystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkspacePreviewService Unit Tests")
public class WorkspacePreviewServiceTest {

    @Mock
    private WorkspaceFilesystemService filesystemService;

    @Mock
    private WorkspaceRepository workspaceRepository;

    private WorkspacePreviewService previewService;
    private Path tempWorkspaceDir;
    private UUID workspaceId;

    @BeforeEach
    void setUp() throws Exception {
        previewService = new WorkspacePreviewService(filesystemService, workspaceRepository);
        tempWorkspaceDir = Files.createTempDirectory("test_preview_workspace_");
        workspaceId = UUID.randomUUID();
        Mockito.lenient().when(filesystemService.provision(workspaceId)).thenReturn(tempWorkspaceDir);
    }

    @Test
    @DisplayName("Should synthesize default game files when index.html is missing")
    void testSynthesizeDefaultGameFilesOnMissingIndex() throws Exception {
        WorkspacePreviewService.PreviewResource result = previewService.getPreviewFile(workspaceId, "index.html");

        assertThat(result.status()).isEqualTo(HttpStatus.OK);
        assertThat(result.contentType()).isEqualTo("text/html;charset=UTF-8");
        assertThat(new String(result.content(), StandardCharsets.UTF_8)).contains("SPACE RUNNER");

        // Verify synthesized files exist on disk
        assertThat(Files.exists(tempWorkspaceDir.resolve("index.html"))).isTrue();
        assertThat(Files.exists(tempWorkspaceDir.resolve("game.js"))).isTrue();
        assertThat(Files.exists(tempWorkspaceDir.resolve("style.css"))).isTrue();
    }

    @Test
    @DisplayName("Should serve existing files correctly")
    void testServeExistingFile() throws Exception {
        Path customFile = tempWorkspaceDir.resolve("app.json");
        Files.writeString(customFile, "{\"name\":\"custom-app\"}", StandardCharsets.UTF_8);

        WorkspacePreviewService.PreviewResource result = previewService.getPreviewFile(workspaceId, "app.json");

        assertThat(result.status()).isEqualTo(HttpStatus.OK);
        assertThat(result.contentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(new String(result.content(), StandardCharsets.UTF_8)).isEqualTo("{\"name\":\"custom-app\"}");
    }

    @Test
    @DisplayName("Should reject path traversal attempts")
    void testRejectPathTraversal() {
        assertThatThrownBy(() -> previewService.getPreviewFile(workspaceId, "../../etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("directory traversal forbidden");
    }

    @Test
    @DisplayName("Should correctly determine MIME types")
    void testDetermineContentType() {
        assertThat(WorkspacePreviewService.determineContentType("index.html")).isEqualTo("text/html;charset=UTF-8");
        assertThat(WorkspacePreviewService.determineContentType("game.js")).isEqualTo("application/javascript;charset=UTF-8");
        assertThat(WorkspacePreviewService.determineContentType("style.css")).isEqualTo("text/css;charset=UTF-8");
        assertThat(WorkspacePreviewService.determineContentType("logo.png")).isEqualTo("image/png");
        assertThat(WorkspacePreviewService.determineContentType("vector.svg")).isEqualTo("image/svg+xml");
        assertThat(WorkspacePreviewService.determineContentType("data.json")).isEqualTo("application/json;charset=UTF-8");
    }
}
