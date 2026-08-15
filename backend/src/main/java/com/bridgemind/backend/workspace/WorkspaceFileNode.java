package com.bridgemind.backend.workspace;

import java.util.List;

public record WorkspaceFileNode(
        String name,
        String path,
        boolean isDirectory,
        long size,
        String extension,
        List<WorkspaceFileNode> children
) {}
