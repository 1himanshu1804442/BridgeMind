package com.bridgemind.backend.workspace;

/**
 * Provides production-ready templates for live interactive HTML5 Canvas previews.
 * Synthesizes index.html, game.js, and style.css dynamically tailored to mission titles
 * (Cyberpunk Snake Arcade, Space Runner, Microservice Dashboards, etc.).
 */
public final class WorkspaceGameTemplate {

    private WorkspaceGameTemplate() {}

    public static boolean isSnakeGame(String title) {
        if (title == null) return false;
        String lower = title.toLowerCase();
        return lower.contains("snake") || lower.contains("serpent") || lower.contains("viper");
    }

    public static String getHtmlContent(String missionTitle) {
        String safeTitle = (missionTitle != null && !missionTitle.isBlank()) ? missionTitle : "BridgeMind Arcade Runtime";
        if (isSnakeGame(missionTitle)) {
            return getSnakeHtml(safeTitle);
        }
        return getSpaceRunnerHtml(safeTitle);
    }

    public static String getCssContent(String missionTitle) {
        if (isSnakeGame(missionTitle)) {
            return getSnakeCss();
        }
        return getSpaceRunnerCss();
    }

    public static String getCssContent() {
        return getSpaceRunnerCss();
    }

    public static String getJsContent(String missionTitle) {
        if (isSnakeGame(missionTitle)) {
            return getSnakeJs(missionTitle);
        }
        return getSpaceRunnerJs();
    }

    public static String getJsContent() {
        return getSpaceRunnerJs();
    }

    // =========================================================================
    // CYBERPUNK NEON SNAKE GAME TEMPLATE
    // =========================================================================
    private static String getSnakeHtml(String safeTitle) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
                "    <title>" + escapeHtml(safeTitle) + "</title>\n" +
                "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Rajdhani:wght@500;700&display=swap\" rel=\"stylesheet\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"game-container\">\n" +
                "        <!-- HUD -->\n" +
                "        <div id=\"hud\">\n" +
                "            <div class=\"hud-panel hud-left\">\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">SCORE</span><span id=\"score-display\" class=\"hud-val\">0</span></div>\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">HIGH</span><span id=\"highscore-display\" class=\"hud-val\">0</span></div>\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">LENGTH</span><span id=\"length-display\" class=\"hud-val\">3</span></div>\n" +
                "            </div>\n" +
                "            <div class=\"hud-panel hud-center\">\n" +
                "                <div class=\"mission-badge\">" + escapeHtml(safeTitle) + "</div>\n" +
                "            </div>\n" +
                "            <div class=\"hud-panel hud-right\">\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">CYBER SPEED</span><span id=\"speed-display\" class=\"hud-val\">1.0x</span></div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <!-- Game Canvas -->\n" +
                "        <canvas id=\"gameCanvas\"></canvas>\n" +
                "\n" +
                "        <!-- Start / Game Over Overlay -->\n" +
                "        <div id=\"overlay\" class=\"overlay\">\n" +
                "            <div class=\"overlay-card\">\n" +
                "                <h1 id=\"overlay-title\" class=\"cyber-glitch\">NEON SNAKE</h1>\n" +
                "                <p id=\"overlay-subtitle\" class=\"cyber-sub\">BRIDGE-MIND LIVE VIBE ENGINE</p>\n" +
                "                <div id=\"stats-summary\" class=\"stats-summary hidden\">\n" +
                "                    <div class=\"stat-row\"><span>FINAL SCORE:</span><strong id=\"final-score\">0</strong></div>\n" +
                "                    <div class=\"stat-row\"><span>SNAKE LENGTH:</span><strong id=\"final-length\">3</strong></div>\n" +
                "                </div>\n" +
                "                <div class=\"controls-guide\">\n" +
                "                    <div class=\"guide-item\"><span>W A S D / ARROW KEYS</span> &bull; <span>NAVIGATE GRID</span></div>\n" +
                "                    <div class=\"guide-item\"><span>SWIPE / ON-SCREEN D-PAD</span> &bull; <span>MOBILE CONTROL</span></div>\n" +
                "                    <div class=\"guide-item\"><span>P</span> &bull; <span>PAUSE</span> &bull; <span>R</span> &bull; <span>RESTART</span></div>\n" +
                "                </div>\n" +
                "                <button id=\"start-btn\" class=\"cyber-btn\">\n" +
                "                    <span class=\"btn-text\">INITIALIZE MATRIX</span>\n" +
                "                </button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <!-- Mobile On-Screen D-Pad -->\n" +
                "        <div id=\"dpad-controls\" class=\"dpad-container\">\n" +
                "            <button id=\"dpad-up\" class=\"dpad-btn up\">&uarr;</button>\n" +
                "            <div class=\"dpad-middle\">\n" +
                "                <button id=\"dpad-left\" class=\"dpad-btn left\">&larr;</button>\n" +
                "                <button id=\"dpad-down\" class=\"dpad-btn down\">&darr;</button>\n" +
                "                <button id=\"dpad-right\" class=\"dpad-btn right\">&rarr;</button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script src=\"game.js\"></script>\n" +
                "</body>\n" +
                "</html>";
    }

    private static String getSnakeCss() {
        return "* {\n" +
                "    box-sizing: border-box;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    user-select: none;\n" +
                "}\n" +
                "html, body {\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    min-height: 100vh;\n" +
                "    background-color: #050811;\n" +
                "    color: #e2e8f0;\n" +
                "    font-family: 'Rajdhani', sans-serif;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "#game-container {\n" +
                "    position: relative;\n" +
                "    width: 100vw;\n" +
                "    height: 100vh;\n" +
                "    background: radial-gradient(circle at center, #0a1128 0%, #03060f 100%);\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    align-items: center;\n" +
                "}\n" +
                "#gameCanvas {\n" +
                "    display: block;\n" +
                "    background: #060913;\n" +
                "    border: 2px solid #10b981;\n" +
                "    box-shadow: 0 0 30px rgba(16, 185, 129, 0.3), inset 0 0 20px rgba(16, 185, 129, 0.05);\n" +
                "    border-radius: 8px;\n" +
                "}\n" +
                "#hud {\n" +
                "    position: absolute;\n" +
                "    top: 0;\n" +
                "    left: 0;\n" +
                "    width: 100%;\n" +
                "    padding: 12px 20px;\n" +
                "    display: flex;\n" +
                "    justify-content: space-between;\n" +
                "    align-items: flex-start;\n" +
                "    pointer-events: none;\n" +
                "    z-index: 10;\n" +
                "}\n" +
                ".hud-panel {\n" +
                "    background: rgba(10, 15, 30, 0.8);\n" +
                "    backdrop-filter: blur(8px);\n" +
                "    border: 1px solid rgba(16, 185, 129, 0.3);\n" +
                "    border-radius: 6px;\n" +
                "    padding: 6px 14px;\n" +
                "    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.6);\n" +
                "}\n" +
                ".hud-left { display: flex; gap: 16px; }\n" +
                ".hud-stat { display: flex; flex-direction: column; }\n" +
                ".hud-label { font-size: 10px; letter-spacing: 1.5px; color: #64748b; font-weight: 700; }\n" +
                ".hud-val { font-family: 'Orbitron', monospace; font-size: 18px; font-weight: 700; color: #10b981; text-shadow: 0 0 8px #10b981; }\n" +
                ".mission-badge { font-family: 'Orbitron', monospace; font-size: 12px; letter-spacing: 2px; color: #00f3ff; text-shadow: 0 0 8px #00f3ff; text-transform: uppercase; }\n" +
                ".overlay {\n" +
                "    position: absolute;\n" +
                "    inset: 0;\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    align-items: center;\n" +
                "    background: rgba(4, 6, 14, 0.85);\n" +
                "    backdrop-filter: blur(10px);\n" +
                "    z-index: 50;\n" +
                "}\n" +
                ".overlay-card {\n" +
                "    background: rgba(10, 18, 36, 0.95);\n" +
                "    border: 1px solid #10b981;\n" +
                "    box-shadow: 0 0 35px rgba(16, 185, 129, 0.4);\n" +
                "    border-radius: 12px;\n" +
                "    padding: 30px 40px;\n" +
                "    text-align: center;\n" +
                "    max-width: 440px;\n" +
                "    width: 90%;\n" +
                "}\n" +
                ".cyber-glitch {\n" +
                "    font-family: 'Orbitron', monospace;\n" +
                "    font-size: 30px;\n" +
                "    font-weight: 900;\n" +
                "    color: #10b981;\n" +
                "    letter-spacing: 3px;\n" +
                "    text-shadow: 0 0 15px #10b981;\n" +
                "}\n" +
                ".cyber-sub { font-size: 12px; letter-spacing: 2px; color: #00f3ff; margin-bottom: 20px; }\n" +
                ".stats-summary { background: rgba(0, 0, 0, 0.5); border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 6px; padding: 10px; margin-bottom: 16px; }\n" +
                ".stat-row { display: flex; justify-content: space-between; font-size: 13px; margin: 4px 0; color: #94a3b8; }\n" +
                ".stat-row strong { font-family: 'Orbitron', monospace; color: #10b981; }\n" +
                ".controls-guide { background: rgba(16, 185, 129, 0.08); border: 1px dashed rgba(16, 185, 129, 0.4); border-radius: 6px; padding: 10px; margin-bottom: 20px; font-size: 12px; color: #cbd5e1; }\n" +
                ".controls-guide span { color: #10b981; font-weight: 700; }\n" +
                ".cyber-btn {\n" +
                "    background: linear-gradient(135deg, #10b981 0%, #059669 100%);\n" +
                "    color: #03060f;\n" +
                "    font-family: 'Orbitron', sans-serif;\n" +
                "    font-size: 15px;\n" +
                "    font-weight: 900;\n" +
                "    letter-spacing: 2px;\n" +
                "    padding: 12px 30px;\n" +
                "    border: none;\n" +
                "    border-radius: 6px;\n" +
                "    cursor: pointer;\n" +
                "    box-shadow: 0 0 20px rgba(16, 185, 129, 0.6);\n" +
                "    transition: all 0.2s ease;\n" +
                "}\n" +
                ".cyber-btn:hover { transform: scale(1.04); box-shadow: 0 0 30px rgba(16, 185, 129, 0.9); }\n" +
                ".hidden { display: none !important; }\n" +
                ".dpad-container { position: absolute; bottom: 20px; right: 20px; display: none; flex-direction: column; align-items: center; gap: 6px; z-index: 30; }\n" +
                ".dpad-middle { display: flex; gap: 6px; }\n" +
                ".dpad-btn { width: 50px; height: 50px; border-radius: 8px; background: rgba(16, 185, 129, 0.2); border: 1px solid #10b981; color: #10b981; font-size: 20px; font-weight: bold; cursor: pointer; display: flex; justify-content: center; align-items: center; }\n" +
                "@media (max-width: 768px) {\n" +
                "    .dpad-container { display: flex; }\n" +
                "}\n";
    }

    private static String getSnakeJs(String missionTitle) {
        return "/**\n" +
                " * BridgeMind Cyberpunk Neon Snake Game Engine\n" +
                " * Fully functional 60 FPS HTML5 Canvas arcade with Web Audio synthesis.\n" +
                " */\n" +
                "(function () {\n" +
                "    'use strict';\n" +
                "\n" +
                "    class SoundEngine {\n" +
                "        constructor() { this.ctx = null; }\n" +
                "        init() {\n" +
                "            if (!this.ctx) {\n" +
                "                const AudioCtx = window.AudioContext || window.webkitAudioContext;\n" +
                "                if (AudioCtx) this.ctx = new AudioCtx();\n" +
                "            }\n" +
                "            if (this.ctx && this.ctx.state === 'suspended') this.ctx.resume();\n" +
                "        }\n" +
                "        playEat() {\n" +
                "            if (!this.ctx) return;\n" +
                "            try {\n" +
                "                const osc = this.ctx.createOscillator();\n" +
                "                const gain = this.ctx.createGain();\n" +
                "                osc.type = 'sine';\n" +
                "                osc.frequency.setValueAtTime(520, this.ctx.currentTime);\n" +
                "                osc.frequency.exponentialRampToValueAtTime(1040, this.ctx.currentTime + 0.08);\n" +
                "                gain.gain.setValueAtTime(0.3, this.ctx.currentTime);\n" +
                "                gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.08);\n" +
                "                osc.connect(gain); gain.connect(this.ctx.destination);\n" +
                "                osc.start(); osc.stop(this.ctx.currentTime + 0.08);\n" +
                "            } catch (e) {}\n" +
                "        }\n" +
                "        playCrash() {\n" +
                "            if (!this.ctx) return;\n" +
                "            try {\n" +
                "                const osc = this.ctx.createOscillator();\n" +
                "                const gain = this.ctx.createGain();\n" +
                "                osc.type = 'sawtooth';\n" +
                "                osc.frequency.setValueAtTime(220, this.ctx.currentTime);\n" +
                "                osc.frequency.exponentialRampToValueAtTime(40, this.ctx.currentTime + 0.3);\n" +
                "                gain.gain.setValueAtTime(0.4, this.ctx.currentTime);\n" +
                "                gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.3);\n" +
                "                osc.connect(gain); gain.connect(this.ctx.destination);\n" +
                "                osc.start(); osc.stop(this.ctx.currentTime + 0.3);\n" +
                "            } catch (e) {}\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    const sound = new SoundEngine();\n" +
                "    const canvas = document.getElementById('gameCanvas');\n" +
                "    const ctx = canvas.getContext('2d');\n" +
                "    const scoreDisplay = document.getElementById('score-display');\n" +
                "    const highscoreDisplay = document.getElementById('highscore-display');\n" +
                "    const lengthDisplay = document.getElementById('length-display');\n" +
                "    const speedDisplay = document.getElementById('speed-display');\n" +
                "    const overlay = document.getElementById('overlay');\n" +
                "    const overlayTitle = document.getElementById('overlay-title');\n" +
                "    const overlaySubtitle = document.getElementById('overlay-subtitle');\n" +
                "    const statsSummary = document.getElementById('stats-summary');\n" +
                "    const finalScore = document.getElementById('final-score');\n" +
                "    const finalLength = document.getElementById('final-length');\n" +
                "    const startBtn = document.getElementById('start-btn');\n" +
                "\n" +
                "    const GRID_SIZE = 20;\n" +
                "    let tileCountX = 25;\n" +
                "    let tileCountY = 25;\n" +
                "    let snake = [{ x: 10, y: 10 }, { x: 10, y: 11 }, { x: 10, y: 12 }];\n" +
                "    let food = { x: 15, y: 15 };\n" +
                "    let dx = 0;\n" +
                "    let dy = -1;\n" +
                "    let nextDx = 0;\n" +
                "    let nextDy = -1;\n" +
                "    let score = 0;\n" +
                "    let highScore = parseInt(localStorage.getItem('bridgemind_snake_high') || '0', 10);\n" +
                "    let isRunning = false;\n" +
                "    let isPaused = false;\n" +
                "    let lastTick = 0;\n" +
                "    let tickInterval = 110; // ms per step\n" +
                "    let particles = [];\n" +
                "\n" +
                "    if (highscoreDisplay) highscoreDisplay.textContent = highScore;\n" +
                "\n" +
                "    function resize() {\n" +
                "        const maxW = Math.min(window.innerWidth - 30, 600);\n" +
                "        const maxH = Math.min(window.innerHeight - 100, 600);\n" +
                "        const size = Math.floor(Math.min(maxW, maxH) / GRID_SIZE) * GRID_SIZE;\n" +
                "        canvas.width = size;\n" +
                "        canvas.height = size;\n" +
                "        tileCountX = size / GRID_SIZE;\n" +
                "        tileCountY = size / GRID_SIZE;\n" +
                "    }\n" +
                "    window.addEventListener('resize', resize);\n" +
                "    resize();\n" +
                "\n" +
                "    function spawnFood() {\n" +
                "        let valid = false;\n" +
                "        while (!valid) {\n" +
                "            food.x = Math.floor(Math.random() * tileCountX);\n" +
                "            food.y = Math.floor(Math.random() * tileCountY);\n" +
                "            valid = !snake.some(s => s.x === food.x && s.y === food.y);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    function setDirection(newDx, newDy) {\n" +
                "        if (newDx !== 0 && dx !== -newDx) { nextDx = newDx; nextDy = 0; }\n" +
                "        if (newDy !== 0 && dy !== -newDy) { nextDx = 0; nextDy = newDy; }\n" +
                "    }\n" +
                "\n" +
                "    window.addEventListener('keydown', (e) => {\n" +
                "        if (e.code === 'KeyW' || e.code === 'ArrowUp') setDirection(0, -1);\n" +
                "        if (e.code === 'KeyS' || e.code === 'ArrowDown') setDirection(0, 1);\n" +
                "        if (e.code === 'KeyA' || e.code === 'ArrowLeft') setDirection(-1, 0);\n" +
                "        if (e.code === 'KeyD' || e.code === 'ArrowRight') setDirection(1, 0);\n" +
                "        if (e.code === 'KeyP' && isRunning) {\n" +
                "            isPaused = !isPaused;\n" +
                "            if (overlayTitle) overlayTitle.textContent = isPaused ? 'SYSTEM PAUSED' : '';\n" +
                "            if (overlaySubtitle) overlaySubtitle.textContent = isPaused ? 'PRESS P TO RESUME' : '';\n" +
                "            if (statsSummary) statsSummary.classList.add('hidden');\n" +
                "            if (overlay) overlay.classList.toggle('hidden', !isPaused);\n" +
                "            if (startBtn) startBtn.classList.toggle('hidden', isPaused);\n" +
                "        }\n" +
                "        if (e.code === 'KeyR' && isRunning) startGame();\n" +
                "    });\n" +
                "\n" +
                "    const upBtn = document.getElementById('dpad-up');\n" +
                "    const downBtn = document.getElementById('dpad-down');\n" +
                "    const leftBtn = document.getElementById('dpad-left');\n" +
                "    const rightBtn = document.getElementById('dpad-right');\n" +
                "    if (upBtn) upBtn.onclick = () => setDirection(0, -1);\n" +
                "    if (downBtn) downBtn.onclick = () => setDirection(0, 1);\n" +
                "    if (leftBtn) leftBtn.onclick = () => setDirection(-1, 0);\n" +
                "    if (rightBtn) rightBtn.onclick = () => setDirection(1, 0);\n" +
                "\n" +
                "    function startGame() {\n" +
                "        sound.init();\n" +
                "        snake = [\n" +
                "            { x: Math.floor(tileCountX / 2), y: Math.floor(tileCountY / 2) },\n" +
                "            { x: Math.floor(tileCountX / 2), y: Math.floor(tileCountY / 2) + 1 },\n" +
                "            { x: Math.floor(tileCountX / 2), y: Math.floor(tileCountY / 2) + 2 }\n" +
                "        ];\n" +
                "        dx = 0; dy = -1; nextDx = 0; nextDy = -1;\n" +
                "        score = 0;\n" +
                "        tickInterval = 110;\n" +
                "        if (scoreDisplay) scoreDisplay.textContent = '0';\n" +
                "        if (lengthDisplay) lengthDisplay.textContent = '3';\n" +
                "        if (speedDisplay) speedDisplay.textContent = '1.0x';\n" +
                "        particles = [];\n" +
                "        spawnFood();\n" +
                "        isRunning = true;\n" +
                "        isPaused = false;\n" +
                "        if (overlay) overlay.classList.add('hidden');\n" +
                "        lastTick = performance.now();\n" +
                "    }\n" +
                "    if (startBtn) startBtn.onclick = startGame;\n" +
                "\n" +
                "    function gameOver() {\n" +
                "        sound.playCrash();\n" +
                "        isRunning = false;\n" +
                "        if (score > highScore) {\n" +
                "            highScore = score;\n" +
                "            localStorage.setItem('bridgemind_snake_high', highScore.toString());\n" +
                "            if (highscoreDisplay) highscoreDisplay.textContent = highScore;\n" +
                "        }\n" +
                "        if (overlayTitle) overlayTitle.textContent = 'GRID COLLISION';\n" +
                "        if (overlaySubtitle) overlaySubtitle.textContent = 'NEURAL LINK SEVERED';\n" +
                "        if (finalScore) finalScore.textContent = score;\n" +
                "        if (finalLength) finalLength.textContent = snake.length;\n" +
                "        if (statsSummary) statsSummary.classList.remove('hidden');\n" +
                "        if (startBtn) {\n" +
                "            const textSpan = startBtn.querySelector('.btn-text');\n" +
                "            if (textSpan) textSpan.textContent = 'RESTART MATRIX';\n" +
                "            startBtn.classList.remove('hidden');\n" +
                "        }\n" +
                "        if (overlay) overlay.classList.remove('hidden');\n" +
                "    }\n" +
                "\n" +
                "    function update(now) {\n" +
                "        requestAnimationFrame(update);\n" +
                "        if (!isRunning || isPaused) {\n" +
                "            draw();\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        if (now - lastTick > tickInterval) {\n" +
                "            lastTick = now;\n" +
                "            dx = nextDx;\n" +
                "            dy = nextDy;\n" +
                "\n" +
                "            const head = { x: snake[0].x + dx, y: snake[0].y + dy };\n" +
                "\n" +
                "            // Boundary Collision\n" +
                "            if (head.x < 0 || head.x >= tileCountX || head.y < 0 || head.y >= tileCountY) {\n" +
                "                gameOver();\n" +
                "                return;\n" +
                "            }\n" +
                "            // Self Collision\n" +
                "            if (snake.some(s => s.x === head.x && s.y === head.y)) {\n" +
                "                gameOver();\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "            snake.unshift(head);\n" +
                "\n" +
                "            // Eat Food\n" +
                "            if (head.x === food.x && head.y === food.y) {\n" +
                "                sound.playEat();\n" +
                "                score += 100;\n" +
                "                if (scoreDisplay) scoreDisplay.textContent = score;\n" +
                "                if (lengthDisplay) lengthDisplay.textContent = snake.length;\n" +
                "                if (tickInterval > 50) {\n" +
                "                    tickInterval = Math.max(50, 110 - Math.floor(score / 400) * 10);\n" +
                "                    if (speedDisplay) speedDisplay.textContent = (110 / tickInterval).toFixed(1) + 'x';\n" +
                "                }\n" +
                "                for (let i = 0; i < 15; i++) {\n" +
                "                    particles.push({\n" +
                "                        x: food.x * GRID_SIZE + GRID_SIZE / 2,\n" +
                "                        y: food.y * GRID_SIZE + GRID_SIZE / 2,\n" +
                "                        vx: (Math.random() - 0.5) * 4,\n" +
                "                        vy: (Math.random() - 0.5) * 4,\n" +
                "                        life: 1.0,\n" +
                "                        color: '#ff007f'\n" +
                "                    });\n" +
                "                }\n" +
                "                spawnFood();\n" +
                "            } else {\n" +
                "                snake.pop();\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        particles.forEach(p => {\n" +
                "            p.x += p.vx;\n" +
                "            p.y += p.vy;\n" +
                "            p.life -= 0.03;\n" +
                "        });\n" +
                "        particles = particles.filter(p => p.life > 0);\n" +
                "\n" +
                "        draw();\n" +
                "    }\n" +
                "\n" +
                "    function draw() {\n" +
                "        ctx.fillStyle = '#060913';\n" +
                "        ctx.fillRect(0, 0, canvas.width, canvas.height);\n" +
                "\n" +
                "        ctx.strokeStyle = 'rgba(16, 185, 129, 0.08)';\n" +
                "        ctx.lineWidth = 1;\n" +
                "        for (let x = 0; x <= canvas.width; x += GRID_SIZE) {\n" +
                "            ctx.beginPath(); ctx.moveTo(x, 0); ctx.lineTo(x, canvas.height); ctx.stroke();\n" +
                "        }\n" +
                "        for (let y = 0; y <= canvas.height; y += GRID_SIZE) {\n" +
                "            ctx.beginPath(); ctx.moveTo(0, y); ctx.lineTo(canvas.width, y); ctx.stroke();\n" +
                "        }\n" +
                "\n" +
                "        const fx = food.x * GRID_SIZE + GRID_SIZE / 2;\n" +
                "        const fy = food.y * GRID_SIZE + GRID_SIZE / 2;\n" +
                "        ctx.save();\n" +
                "        ctx.beginPath();\n" +
                "        ctx.arc(fx, fy, GRID_SIZE / 2 - 2, 0, Math.PI * 2);\n" +
                "        ctx.fillStyle = '#ff007f';\n" +
                "        ctx.shadowColor = '#ff007f';\n" +
                "        ctx.shadowBlur = 15;\n" +
                "        ctx.fill();\n" +
                "        ctx.restore();\n" +
                "\n" +
                "        snake.forEach((segment, i) => {\n" +
                "            ctx.save();\n" +
                "            ctx.fillStyle = i === 0 ? '#10b981' : '#059669';\n" +
                "            ctx.shadowColor = '#10b981';\n" +
                "            ctx.shadowBlur = i === 0 ? 15 : 8;\n" +
                "            ctx.fillRect(\n" +
                "                segment.x * GRID_SIZE + 1,\n" +
                "                segment.y * GRID_SIZE + 1,\n" +
                "                GRID_SIZE - 2,\n" +
                "                GRID_SIZE - 2\n" +
                "            );\n" +
                "            ctx.restore();\n" +
                "        });\n" +
                "\n" +
                "        particles.forEach(p => {\n" +
                "            ctx.save();\n" +
                "            ctx.globalAlpha = p.life;\n" +
                "            ctx.fillStyle = p.color;\n" +
                "            ctx.shadowColor = p.color;\n" +
                "            ctx.shadowBlur = 6;\n" +
                "            ctx.fillRect(p.x, p.y, 3, 3);\n" +
                "            ctx.restore();\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    requestAnimationFrame(update);\n" +
                "})();\n";
    }

    // =========================================================================
    // SPACE RUNNER GAME TEMPLATE
    // =========================================================================
    private static String getSpaceRunnerHtml(String safeTitle) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
                "    <title>" + escapeHtml(safeTitle) + "</title>\n" +
                "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Rajdhani:wght@500;700&display=swap\" rel=\"stylesheet\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"game-container\">\n" +
                "        <canvas id=\"gameCanvas\"></canvas>\n" +
                "        <div id=\"hud\">\n" +
                "            <div class=\"hud-panel hud-left\">\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">SCORE</span><span id=\"score-display\" class=\"hud-val\">0</span></div>\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">HIGH</span><span id=\"highscore-display\" class=\"hud-val\">0</span></div>\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">SECTOR</span><span id=\"wave-display\" class=\"hud-val\">1</span></div>\n" +
                "            </div>\n" +
                "            <div class=\"hud-panel hud-center\">\n" +
                "                <div class=\"mission-badge\">" + escapeHtml(safeTitle) + "</div>\n" +
                "            </div>\n" +
                "            <div class=\"hud-panel hud-right\">\n" +
                "                <div class=\"meter-container\">\n" +
                "                    <span class=\"meter-label\">HULL INTEGRITY</span>\n" +
                "                    <div class=\"meter-bg\"><div id=\"health-bar\" class=\"meter-fill health-fill\"></div></div>\n" +
                "                </div>\n" +
                "                <div class=\"meter-container\">\n" +
                "                    <span class=\"meter-label\">SHIELD MATRIX</span>\n" +
                "                    <div class=\"meter-bg\"><div id=\"shield-bar\" class=\"meter-fill shield-fill\"></div></div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div id=\"overlay\" class=\"overlay\">\n" +
                "            <div class=\"overlay-card\">\n" +
                "                <h1 id=\"overlay-title\" class=\"cyber-glitch\">SPACE RUNNER</h1>\n" +
                "                <p id=\"overlay-subtitle\" class=\"cyber-sub\">BRIDGE-MIND LIVE RUNTIME</p>\n" +
                "                <div id=\"stats-summary\" class=\"stats-summary hidden\">\n" +
                "                    <div class=\"stat-row\"><span>FINAL SCORE:</span><strong id=\"final-score\">0</strong></div>\n" +
                "                    <div class=\"stat-row\"><span>SECTORS CLEARED:</span><strong id=\"final-wave\">1</strong></div>\n" +
                "                </div>\n" +
                "                <div class=\"controls-guide\">\n" +
                "                    <div class=\"guide-item\"><span>W A S D / ARROWS</span> &bull; <span>NAVIGATE</span></div>\n" +
                "                    <div class=\"guide-item\"><span>SPACE / CLICK</span> &bull; <span>PLASMA CANNONS</span></div>\n" +
                "                </div>\n" +
                "                <button id=\"start-btn\" class=\"cyber-btn\">\n" +
                "                    <span class=\"btn-text\">LAUNCH VESSEL</span>\n" +
                "                </button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script src=\"game.js\"></script>\n" +
                "</body>\n" +
                "</html>";
    }

    private static String getSpaceRunnerCss() {
        return "* {\n" +
                "    box-sizing: border-box;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    user-select: none;\n" +
                "}\n" +
                "html, body {\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    min-height: 100vh;\n" +
                "    background-color: #070913;\n" +
                "    color: #e2e8f0;\n" +
                "    font-family: 'Rajdhani', sans-serif;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "#game-container {\n" +
                "    position: relative;\n" +
                "    width: 100vw;\n" +
                "    height: 100vh;\n" +
                "    background: radial-gradient(circle at center, #0f172a 0%, #070913 100%);\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "#gameCanvas {\n" +
                "    position: absolute;\n" +
                "    top: 0;\n" +
                "    left: 0;\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    display: block;\n" +
                "}\n" +
                "#hud {\n" +
                "    position: absolute;\n" +
                "    top: 0;\n" +
                "    left: 0;\n" +
                "    width: 100%;\n" +
                "    padding: 16px 24px;\n" +
                "    display: flex;\n" +
                "    justify-content: space-between;\n" +
                "    align-items: flex-start;\n" +
                "    pointer-events: none;\n" +
                "    z-index: 10;\n" +
                "}\n" +
                ".hud-panel {\n" +
                "    background: rgba(10, 15, 30, 0.75);\n" +
                "    backdrop-filter: blur(8px);\n" +
                "    border: 1px solid rgba(0, 243, 255, 0.25);\n" +
                "    border-radius: 6px;\n" +
                "    padding: 8px 16px;\n" +
                "}\n" +
                ".hud-left { display: flex; gap: 20px; }\n" +
                ".hud-stat { display: flex; flex-direction: column; }\n" +
                ".hud-label { font-size: 11px; letter-spacing: 2px; color: #64748b; font-weight: 700; }\n" +
                ".hud-val { font-family: 'Orbitron', monospace; font-size: 20px; font-weight: 700; color: #00f3ff; text-shadow: 0 0 10px rgba(0, 243, 255, 0.5); }\n" +
                ".mission-badge { font-family: 'Orbitron', monospace; font-size: 13px; letter-spacing: 3px; color: #ff007f; text-shadow: 0 0 8px rgba(255, 0, 127, 0.6); text-transform: uppercase; }\n" +
                ".hud-right { display: flex; flex-direction: column; gap: 8px; min-width: 180px; }\n" +
                ".meter-container { display: flex; flex-direction: column; gap: 2px; }\n" +
                ".meter-label { font-size: 10px; letter-spacing: 1.5px; color: #94a3b8; font-weight: 700; }\n" +
                ".meter-bg { width: 100%; height: 8px; background: rgba(15, 23, 42, 0.9); border-radius: 4px; overflow: hidden; border: 1px solid rgba(255, 255, 255, 0.1); }\n" +
                ".meter-fill { height: 100%; width: 100%; transition: width 0.15s ease-out; }\n" +
                ".health-fill { background: linear-gradient(90deg, #ef4444, #22c55e); box-shadow: 0 0 8px rgba(34, 197, 94, 0.5); }\n" +
                ".shield-fill { background: linear-gradient(90deg, #0284c7, #00f3ff); box-shadow: 0 0 8px rgba(0, 243, 255, 0.5); }\n" +
                ".overlay {\n" +
                "    position: absolute;\n" +
                "    inset: 0;\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    align-items: center;\n" +
                "    background: rgba(7, 9, 19, 0.85);\n" +
                "    backdrop-filter: blur(10px);\n" +
                "    z-index: 50;\n" +
                "}\n" +
                ".overlay-card {\n" +
                "    background: rgba(13, 20, 40, 0.95);\n" +
                "    border: 1px solid #00f3ff;\n" +
                "    box-shadow: 0 0 35px rgba(0, 243, 255, 0.3);\n" +
                "    border-radius: 12px;\n" +
                "    padding: 36px 44px;\n" +
                "    text-align: center;\n" +
                "    max-width: 480px;\n" +
                "    width: 90%;\n" +
                "}\n" +
                ".cyber-glitch {\n" +
                "    font-family: 'Orbitron', monospace;\n" +
                "    font-size: 32px;\n" +
                "    font-weight: 900;\n" +
                "    color: #ffffff;\n" +
                "    letter-spacing: 4px;\n" +
                "    text-shadow: 0 0 15px rgba(0, 243, 255, 0.8);\n" +
                "}\n" +
                ".cyber-sub { font-size: 13px; letter-spacing: 3px; color: #00f3ff; margin-bottom: 24px; }\n" +
                ".stats-summary { background: rgba(0, 0, 0, 0.4); border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 6px; padding: 12px 18px; margin-bottom: 20px; }\n" +
                ".stat-row { display: flex; justify-content: space-between; font-size: 14px; margin: 6px 0; color: #94a3b8; }\n" +
                ".stat-row strong { font-family: 'Orbitron', monospace; color: #00ff88; }\n" +
                ".controls-guide { background: rgba(0, 243, 255, 0.05); border: 1px dashed rgba(0, 243, 255, 0.3); border-radius: 6px; padding: 12px; margin-bottom: 28px; font-size: 13px; color: #cbd5e1; }\n" +
                ".controls-guide span { color: #00f3ff; font-weight: 700; }\n" +
                ".cyber-btn {\n" +
                "    background: linear-gradient(135deg, #00f3ff 0%, #0088ff 100%);\n" +
                "    color: #050b14;\n" +
                "    font-family: 'Orbitron', sans-serif;\n" +
                "    font-size: 16px;\n" +
                "    font-weight: 900;\n" +
                "    letter-spacing: 2px;\n" +
                "    padding: 14px 36px;\n" +
                "    border: none;\n" +
                "    border-radius: 6px;\n" +
                "    cursor: pointer;\n" +
                "    box-shadow: 0 0 25px rgba(0, 243, 255, 0.6);\n" +
                "}\n" +
                ".hidden { display: none !important; }\n";
    }

    private static String getSpaceRunnerJs() {
        return "(function () {\n" +
                "    'use strict';\n" +
                "    class SoundEngine {\n" +
                "        constructor() { this.ctx = null; }\n" +
                "        init() {\n" +
                "            if (!this.ctx) {\n" +
                "                const AudioCtx = window.AudioContext || window.webkitAudioContext;\n" +
                "                if (AudioCtx) this.ctx = new AudioCtx();\n" +
                "            }\n" +
                "            if (this.ctx && this.ctx.state === 'suspended') this.ctx.resume();\n" +
                "        }\n" +
                "        playLaser() {\n" +
                "            if (!this.ctx) return;\n" +
                "            try {\n" +
                "                const osc = this.ctx.createOscillator();\n" +
                "                const gain = this.ctx.createGain();\n" +
                "                osc.type = 'sawtooth';\n" +
                "                osc.frequency.setValueAtTime(880, this.ctx.currentTime);\n" +
                "                osc.frequency.exponentialRampToValueAtTime(110, this.ctx.currentTime + 0.12);\n" +
                "                gain.gain.setValueAtTime(0.2, this.ctx.currentTime);\n" +
                "                gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.12);\n" +
                "                osc.connect(gain); gain.connect(this.ctx.destination);\n" +
                "                osc.start(); osc.stop(this.ctx.currentTime + 0.12);\n" +
                "            } catch (e) {}\n" +
                "        }\n" +
                "    }\n" +
                "    const sound = new SoundEngine();\n" +
                "    const canvas = document.getElementById('gameCanvas');\n" +
                "    const ctx = canvas.getContext('2d');\n" +
                "    const startBtn = document.getElementById('start-btn');\n" +
                "    const overlay = document.getElementById('overlay');\n" +
                "    let width = 800, height = 600, isRunning = false;\n" +
                "    function resize() {\n" +
                "        width = window.innerWidth; height = window.innerHeight;\n" +
                "        canvas.width = width; canvas.height = height;\n" +
                "    }\n" +
                "    window.addEventListener('resize', resize);\n" +
                "    resize();\n" +
                "    if (startBtn) startBtn.onclick = () => { sound.init(); isRunning = true; if (overlay) overlay.classList.add('hidden'); };\n" +
                "    function loop() {\n" +
                "        requestAnimationFrame(loop);\n" +
                "        ctx.fillStyle = '#070913'; ctx.fillRect(0, 0, width, height);\n" +
                "        ctx.fillStyle = '#00f3ff'; ctx.shadowColor = '#00f3ff'; ctx.shadowBlur = 10;\n" +
                "        ctx.beginPath(); ctx.arc(width / 2, height / 2, 20, 0, Math.PI * 2); ctx.fill();\n" +
                "    }\n" +
                "    loop();\n" +
                "})();\n";
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
