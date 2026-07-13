package com.bridgemind.backend.runtime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface RuntimeExecutionRepository extends JpaRepository<RuntimeExecution, UUID> { }
