# =============================================================================
# HivePilot Backend - Multi-stage Dockerfile
# Stage 1: Build the Spring Boot application with Maven
# Stage 2: Run the JAR in a minimal JRE image with a non-root user
# =============================================================================

# ---------------------
# Stage 1: Build
# ---------------------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy source code and POM
COPY pom.xml ./
COPY src ./src

# Build the application, skipping tests since CI already runs them
RUN mvn clean package -DskipTests -B

# ---------------------
# Stage 2: Runtime
# ---------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user for security — running as root inside a container
# is a well-known security anti-pattern because a container escape would
# grant host-level root access.
RUN groupadd --system appgroup && \
    useradd --system --gid appgroup --no-create-home appuser

# Copy only the built JAR from the build stage (keeps the image small)
COPY --from=build /app/target/*.jar app.jar

# Change ownership so the non-root user can read the JAR
RUN chown appuser:appgroup app.jar

# Switch to non-root user before running
USER appuser

EXPOSE 8080

# Healthcheck uses curl to ping Spring Boot Actuator's health endpoint.
# The container orchestrator (Docker Compose, K8s) uses this to determine
# if the service is ready to accept traffic.
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Use exec form so the JVM receives OS signals (SIGTERM) directly,
# enabling graceful shutdown.
ENTRYPOINT ["java", "-jar", "app.jar"]
