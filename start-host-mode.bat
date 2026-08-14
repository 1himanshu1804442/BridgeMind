@echo off
echo ======================================================================
echo Starting BridgeMind in Native Host Mode (Direct Windows CLI Access)
echo ======================================================================
echo.
echo 1. Ensuring PostgreSQL and Redis containers are running in Docker...
cd /d "%~dp0\infrastructure"
docker compose up -d postgres redis
echo.
echo 2. Stopping Docker backend container so host port 8080 is free...
docker compose stop backend
echo.
echo 3. Launching Spring Boot backend natively on Windows...
echo (This gives BridgeMind full native access to your local agy, codex, and gh sessions!)
echo.
cd /d "%~dp0\backend"
java -jar target\backend-0.0.1-SNAPSHOT.jar
pause
