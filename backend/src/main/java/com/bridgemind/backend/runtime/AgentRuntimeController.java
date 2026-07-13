package com.bridgemind.backend.runtime;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/runtimes")
public class AgentRuntimeController {
    private final RuntimeRegistry registry;
    private final AgentRuntimeService service;
    public AgentRuntimeController(RuntimeRegistry registry, AgentRuntimeService service) { this.registry = registry; this.service = service; }
    @GetMapping public List<String> list() { return registry.ids(); }
    @PostMapping("/{runtimeId}/executions") public RuntimeExecution launch(@PathVariable String runtimeId, @RequestBody RuntimeLaunchRequest request) { return service.launch(runtimeId, request); }
    @PostMapping("/{runtimeId}/executions/{executionId}/cancel") public RuntimeExecution cancel(@PathVariable String runtimeId, @PathVariable UUID executionId) { return service.cancel(runtimeId, executionId); }
}
