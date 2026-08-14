# BridgeMind Native Host Mode Runner
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "🚀 Starting BridgeMind in Native Host Mode (Direct Windows CLI Access)" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan

$bridgeMindRoot = $PSScriptRoot
if (-not $bridgeMindRoot) {
    $bridgeMindRoot = "C:\Users\hy180\BridgeMind"
}

Write-Host "`n1. Ensuring PostgreSQL and Redis are running in Docker..." -ForegroundColor Yellow
Set-Location "$bridgeMindRoot\infrastructure"
docker compose up -d postgres redis

Write-Host "`n2. Stopping Docker backend container so host port 8080 is free..." -ForegroundColor Yellow
docker compose stop backend

Write-Host "`n3. Launching Spring Boot backend natively on Windows..." -ForegroundColor Green
Write-Host "   (BridgeMind now has 100% native access to your real agy.exe, codex.cmd, and gh copilot accounts!)`n" -ForegroundColor Gray

Set-Location "$bridgeMindRoot\backend"
java -jar target\backend-0.0.1-SNAPSHOT.jar
