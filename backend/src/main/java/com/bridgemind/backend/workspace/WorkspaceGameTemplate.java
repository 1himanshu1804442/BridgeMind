package com.bridgemind.backend.workspace;

/**
 * Provides production-ready templates for the HTML5 Canvas Space Runner / Arcade Game.
 * Synthesizes index.html, game.js, and style.css for live previews and agent mission execution.
 */
public final class WorkspaceGameTemplate {

    private WorkspaceGameTemplate() {}

    public static String getHtmlContent(String missionTitle) {
        String safeTitle = (missionTitle != null && !missionTitle.isBlank()) ? missionTitle : "BridgeMind Space Runner";
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
                "        \n" +
                "        <!-- HUD Overlay -->\n" +
                "        <div id=\"hud\">\n" +
                "            <div class=\"hud-panel hud-left\">\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">SCORE</span><span id=\"score-display\" class=\"hud-val\">0</span></div>\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">HIGH</span><span id=\"highscore-display\" class=\"hud-val\">0</span></div>\n" +
                "                <div class=\"hud-stat\"><span class=\"hud-label\">SECTOR</span><span id=\"wave-display\" class=\"hud-val\">1</span></div>\n" +
                "            </div>\n" +
                "            <div class=\"hud-panel hud-center\">\n" +
                "                <div class=\"mission-badge\">" + escapeHtml(safeTitle) + "</div>\n" +
                "                <div id=\"boss-bar-container\" class=\"boss-bar-container hidden\">\n" +
                "                    <div class=\"boss-label\">ALERT: DREADNOUGHT CLASS ENEMY</div>\n" +
                "                    <div class=\"boss-bar-bg\"><div id=\"boss-health-bar\" class=\"boss-bar-fill\"></div></div>\n" +
                "                </div>\n" +
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
                "\n" +
                "        <!-- Start / Game Over Screen -->\n" +
                "        <div id=\"overlay\" class=\"overlay\">\n" +
                "            <div class=\"overlay-card\">\n" +
                "                <h1 id=\"overlay-title\" class=\"cyber-glitch\">SPACE RUNNER</h1>\n" +
                "                <p id=\"overlay-subtitle\" class=\"cyber-sub\">BRIDGE-MIND LIVE RUNTIME</p>\n" +
                "                <div id=\"stats-summary\" class=\"stats-summary hidden\">\n" +
                "                    <div class=\"stat-row\"><span>FINAL SCORE:</span><strong id=\"final-score\">0</strong></div>\n" +
                "                    <div class=\"stat-row\"><span>SECTORS CLEARED:</span><strong id=\"final-wave\">1</strong></div>\n" +
                "                    <div class=\"stat-row\"><span>ENEMIES PURGED:</span><strong id=\"final-kills\">0</strong></div>\n" +
                "                </div>\n" +
                "                <div class=\"controls-guide\">\n" +
                "                    <div class=\"guide-item\"><span>W A S D / ARROWS</span> &bull; <span>NAVIGATE</span></div>\n" +
                "                    <div class=\"guide-item\"><span>SPACE / CLICK / TAP</span> &bull; <span>PLASMA CANNONS</span></div>\n" +
                "                    <div class=\"guide-item\"><span>P</span> &bull; <span>PAUSE</span> &bull; <span>R</span> &bull; <span>RESTART</span></div>\n" +
                "                </div>\n" +
                "                <button id=\"start-btn\" class=\"cyber-btn\">\n" +
                "                    <span class=\"btn-glitch\"></span>\n" +
                "                    <span class=\"btn-text\">LAUNCH VESSEL</span>\n" +
                "                </button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <!-- Mobile Virtual Controls -->\n" +
                "        <div id=\"mobile-controls\" class=\"mobile-controls\">\n" +
                "            <div id=\"touch-fire-btn\" class=\"touch-btn fire-btn\">FIRE</div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script src=\"game.js\"></script>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String getCssContent() {
        return "/* BridgeMind Space Runner & Live Preview Arcade Styling */\n" +
                "* {\n" +
                "    box-sizing: border-box;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    user-select: none;\n" +
                "    -webkit-user-select: none;\n" +
                "}\n" +
                "\n" +
                "html, body {\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    min-height: 100vh;\n" +
                "    background-color: #070913;\n" +
                "    color: #e2e8f0;\n" +
                "    font-family: 'Rajdhani', sans-serif;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                "#game-container {\n" +
                "    position: relative;\n" +
                "    width: 100vw;\n" +
                "    height: 100vh;\n" +
                "    background: radial-gradient(circle at center, #0f172a 0%, #070913 100%);\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                "#gameCanvas {\n" +
                "    position: absolute;\n" +
                "    top: 0;\n" +
                "    left: 0;\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    display: block;\n" +
                "    cursor: crosshair;\n" +
                "}\n" +
                "\n" +
                "/* HUD OVERLAY */\n" +
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
                "\n" +
                ".hud-panel {\n" +
                "    background: rgba(10, 15, 30, 0.75);\n" +
                "    backdrop-filter: blur(8px);\n" +
                "    border: 1px solid rgba(0, 243, 255, 0.25);\n" +
                "    border-radius: 6px;\n" +
                "    padding: 8px 16px;\n" +
                "    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), inset 0 0 15px rgba(0, 243, 255, 0.05);\n" +
                "}\n" +
                "\n" +
                ".hud-left {\n" +
                "    display: flex;\n" +
                "    gap: 20px;\n" +
                "}\n" +
                "\n" +
                ".hud-stat {\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "}\n" +
                "\n" +
                ".hud-label {\n" +
                "    font-size: 11px;\n" +
                "    letter-spacing: 2px;\n" +
                "    color: #64748b;\n" +
                "    font-weight: 700;\n" +
                "}\n" +
                "\n" +
                ".hud-val {\n" +
                "    font-family: 'Orbitron', monospace;\n" +
                "    font-size: 20px;\n" +
                "    font-weight: 700;\n" +
                "    color: #00f3ff;\n" +
                "    text-shadow: 0 0 10px rgba(0, 243, 255, 0.5);\n" +
                "}\n" +
                "\n" +
                ".hud-center {\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    align-items: center;\n" +
                "    border-color: rgba(255, 0, 127, 0.25);\n" +
                "}\n" +
                "\n" +
                ".mission-badge {\n" +
                "    font-family: 'Orbitron', monospace;\n" +
                "    font-size: 13px;\n" +
                "    letter-spacing: 3px;\n" +
                "    color: #ff007f;\n" +
                "    text-shadow: 0 0 8px rgba(255, 0, 127, 0.6);\n" +
                "    text-transform: uppercase;\n" +
                "}\n" +
                "\n" +
                ".hud-right {\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    gap: 8px;\n" +
                "    min-width: 180px;\n" +
                "}\n" +
                "\n" +
                ".meter-container {\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    gap: 2px;\n" +
                "}\n" +
                "\n" +
                ".meter-label {\n" +
                "    font-size: 10px;\n" +
                "    letter-spacing: 1.5px;\n" +
                "    color: #94a3b8;\n" +
                "    font-weight: 700;\n" +
                "}\n" +
                "\n" +
                ".meter-bg {\n" +
                "    width: 100%;\n" +
                "    height: 8px;\n" +
                "    background: rgba(15, 23, 42, 0.9);\n" +
                "    border-radius: 4px;\n" +
                "    overflow: hidden;\n" +
                "    border: 1px solid rgba(255, 255, 255, 0.1);\n" +
                "}\n" +
                "\n" +
                ".meter-fill {\n" +
                "    height: 100%;\n" +
                "    width: 100%;\n" +
                "    transition: width 0.15s ease-out;\n" +
                "}\n" +
                "\n" +
                ".health-fill {\n" +
                "    background: linear-gradient(90deg, #ef4444, #22c55e);\n" +
                "    box-shadow: 0 0 8px rgba(34, 197, 94, 0.5);\n" +
                "}\n" +
                "\n" +
                ".shield-fill {\n" +
                "    background: linear-gradient(90deg, #0284c7, #00f3ff);\n" +
                "    box-shadow: 0 0 8px rgba(0, 243, 255, 0.5);\n" +
                "}\n" +
                "\n" +
                "/* BOSS HEALTH BAR */\n" +
                ".boss-bar-container {\n" +
                "    margin-top: 6px;\n" +
                "    width: 260px;\n" +
                "    text-align: center;\n" +
                "}\n" +
                "\n" +
                ".boss-label {\n" +
                "    font-size: 9px;\n" +
                "    letter-spacing: 2px;\n" +
                "    color: #ef4444;\n" +
                "    font-weight: 900;\n" +
                "    margin-bottom: 2px;\n" +
                "    animation: pulse 1s infinite alternate;\n" +
                "}\n" +
                "\n" +
                ".boss-bar-bg {\n" +
                "    width: 100%;\n" +
                "    height: 10px;\n" +
                "    background: rgba(0,0,0,0.8);\n" +
                "    border: 1px solid #ef4444;\n" +
                "    border-radius: 3px;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                ".boss-bar-fill {\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    background: linear-gradient(90deg, #ef4444, #ff007f);\n" +
                "    box-shadow: 0 0 10px #ef4444;\n" +
                "    transition: width 0.1s;\n" +
                "}\n" +
                "\n" +
                "/* OVERLAY MODAL */\n" +
                ".overlay {\n" +
                "    position: absolute;\n" +
                "    inset: 0;\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    align-items: center;\n" +
                "    background: rgba(7, 9, 19, 0.85);\n" +
                "    backdrop-filter: blur(10px);\n" +
                "    z-index: 50;\n" +
                "    transition: opacity 0.3s ease;\n" +
                "}\n" +
                "\n" +
                ".overlay-card {\n" +
                "    background: rgba(13, 20, 40, 0.95);\n" +
                "    border: 1px solid #00f3ff;\n" +
                "    box-shadow: 0 0 35px rgba(0, 243, 255, 0.3), inset 0 0 20px rgba(0, 243, 255, 0.08);\n" +
                "    border-radius: 12px;\n" +
                "    padding: 36px 44px;\n" +
                "    text-align: center;\n" +
                "    max-width: 480px;\n" +
                "    width: 90%;\n" +
                "}\n" +
                "\n" +
                ".cyber-glitch {\n" +
                "    font-family: 'Orbitron', monospace;\n" +
                "    font-size: 32px;\n" +
                "    font-weight: 900;\n" +
                "    color: #ffffff;\n" +
                "    letter-spacing: 4px;\n" +
                "    text-shadow: 0 0 15px rgba(0, 243, 255, 0.8);\n" +
                "    margin-bottom: 4px;\n" +
                "}\n" +
                "\n" +
                ".cyber-sub {\n" +
                "    font-size: 13px;\n" +
                "    letter-spacing: 3px;\n" +
                "    color: #00f3ff;\n" +
                "    margin-bottom: 24px;\n" +
                "}\n" +
                "\n" +
                ".stats-summary {\n" +
                "    background: rgba(0, 0, 0, 0.4);\n" +
                "    border: 1px solid rgba(255, 255, 255, 0.1);\n" +
                "    border-radius: 6px;\n" +
                "    padding: 12px 18px;\n" +
                "    margin-bottom: 20px;\n" +
                "}\n" +
                "\n" +
                ".stat-row {\n" +
                "    display: flex;\n" +
                "    justify-content: space-between;\n" +
                "    font-size: 14px;\n" +
                "    margin: 6px 0;\n" +
                "    color: #94a3b8;\n" +
                "}\n" +
                "\n" +
                ".stat-row strong {\n" +
                "    font-family: 'Orbitron', monospace;\n" +
                "    color: #00ff88;\n" +
                "}\n" +
                "\n" +
                ".controls-guide {\n" +
                "    background: rgba(0, 243, 255, 0.05);\n" +
                "    border: 1px dashed rgba(0, 243, 255, 0.3);\n" +
                "    border-radius: 6px;\n" +
                "    padding: 12px;\n" +
                "    margin-bottom: 28px;\n" +
                "    font-size: 13px;\n" +
                "    line-height: 1.6;\n" +
                "    color: #cbd5e1;\n" +
                "}\n" +
                "\n" +
                ".controls-guide span {\n" +
                "    color: #00f3ff;\n" +
                "    font-weight: 700;\n" +
                "}\n" +
                "\n" +
                ".cyber-btn {\n" +
                "    position: relative;\n" +
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
                "    transition: all 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".cyber-btn:hover {\n" +
                "    transform: translateY(-2px) scale(1.03);\n" +
                "    box-shadow: 0 0 35px rgba(0, 243, 255, 0.9);\n" +
                "}\n" +
                "\n" +
                ".cyber-btn:active {\n" +
                "    transform: translateY(1px) scale(0.98);\n" +
                "}\n" +
                "\n" +
                ".hidden {\n" +
                "    display: none !important;\n" +
                "}\n" +
                "\n" +
                "/* MOBILE CONTROLS */\n" +
                ".mobile-controls {\n" +
                "    position: absolute;\n" +
                "    bottom: 24px;\n" +
                "    right: 24px;\n" +
                "    z-index: 20;\n" +
                "    display: none;\n" +
                "}\n" +
                "\n" +
                "@media (max-width: 768px) {\n" +
                "    .mobile-controls {\n" +
                "        display: block;\n" +
                "    }\n" +
                "    .overlay-card {\n" +
                "        padding: 24px 20px;\n" +
                "    }\n" +
                "    .cyber-glitch {\n" +
                "        font-size: 24px;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                ".touch-btn {\n" +
                "    width: 68px;\n" +
                "    height: 68px;\n" +
                "    border-radius: 50%;\n" +
                "    background: rgba(0, 243, 255, 0.2);\n" +
                "    border: 2px solid #00f3ff;\n" +
                "    color: #00f3ff;\n" +
                "    font-family: 'Orbitron', monospace;\n" +
                "    font-size: 13px;\n" +
                "    font-weight: 700;\n" +
                "    display: flex;\n" +
                "    justify-content: center;\n" +
                "    align-items: center;\n" +
                "    box-shadow: 0 0 15px rgba(0, 243, 255, 0.4);\n" +
                "    user-select: none;\n" +
                "    touch-action: manipulation;\n" +
                "}\n" +
                "\n" +
                "@keyframes pulse {\n" +
                "    from { opacity: 0.7; }\n" +
                "    to { opacity: 1; }\n" +
                "}\n";
    }

    public static String getJsContent() {
        return "/**\n" +
                " * BridgeMind Space Runner - HTML5 Canvas Live Arcade Engine\n" +
                " * Complete high-performance game loop with Web Audio API sound synthesis.\n" +
                " */\n" +
                "(function () {\n" +
                "    'use strict';\n" +
                "\n" +
                "    // --- Audio Synthesizer (Zero External Dependencies) ---\n" +
                "    class SoundEngine {\n" +
                "        constructor() {\n" +
                "            this.ctx = null;\n" +
                "        }\n" +
                "        init() {\n" +
                "            if (!this.ctx) {\n" +
                "                const AudioCtx = window.AudioContext || window.webkitAudioContext;\n" +
                "                if (AudioCtx) this.ctx = new AudioCtx();\n" +
                "            }\n" +
                "            if (this.ctx && this.ctx.state === 'suspended') {\n" +
                "                this.ctx.resume();\n" +
                "            }\n" +
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
                "                osc.connect(gain);\n" +
                "                gain.connect(this.ctx.destination);\n" +
                "                osc.start();\n" +
                "                osc.stop(this.ctx.currentTime + 0.12);\n" +
                "            } catch (e) {}\n" +
                "        }\n" +
                "        playExplosion() {\n" +
                "            if (!this.ctx) return;\n" +
                "            try {\n" +
                "                const bufferSize = this.ctx.sampleRate * 0.3;\n" +
                "                const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);\n" +
                "                const data = buffer.getChannelData(0);\n" +
                "                for (let i = 0; i < bufferSize; i++) {\n" +
                "                    data[i] = Math.random() * 2 - 1;\n" +
                "                }\n" +
                "                const noise = this.ctx.createBufferSource();\n" +
                "                noise.buffer = buffer;\n" +
                "                const filter = this.ctx.createBiquadFilter();\n" +
                "                filter.type = 'lowpass';\n" +
                "                filter.frequency.setValueAtTime(600, this.ctx.currentTime);\n" +
                "                filter.frequency.linearRampToValueAtTime(80, this.ctx.currentTime + 0.3);\n" +
                "                const gain = this.ctx.createGain();\n" +
                "                gain.gain.setValueAtTime(0.35, this.ctx.currentTime);\n" +
                "                gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.3);\n" +
                "                noise.connect(filter);\n" +
                "                filter.connect(gain);\n" +
                "                gain.connect(this.ctx.destination);\n" +
                "                noise.start();\n" +
                "            } catch (e) {}\n" +
                "        }\n" +
                "        playPowerup() {\n" +
                "            if (!this.ctx) return;\n" +
                "            try {\n" +
                "                const notes = [330, 440, 554, 659];\n" +
                "                notes.forEach((freq, idx) => {\n" +
                "                    const osc = this.ctx.createOscillator();\n" +
                "                    const gain = this.ctx.createGain();\n" +
                "                    osc.type = 'triangle';\n" +
                "                    osc.frequency.setValueAtTime(freq, this.ctx.currentTime + idx * 0.05);\n" +
                "                    gain.gain.setValueAtTime(0.2, this.ctx.currentTime + idx * 0.05);\n" +
                "                    gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + idx * 0.05 + 0.1);\n" +
                "                    osc.connect(gain);\n" +
                "                    gain.connect(this.ctx.destination);\n" +
                "                    osc.start(this.ctx.currentTime + idx * 0.05);\n" +
                "                    osc.stop(this.ctx.currentTime + idx * 0.05 + 0.1);\n" +
                "                });\n" +
                "            } catch (e) {}\n" +
                "        }\n" +
                "        playGameOver() {\n" +
                "            if (!this.ctx) return;\n" +
                "            try {\n" +
                "                const notes = [220, 196, 174, 146];\n" +
                "                notes.forEach((freq, idx) => {\n" +
                "                    const osc = this.ctx.createOscillator();\n" +
                "                    const gain = this.ctx.createGain();\n" +
                "                    osc.type = 'sawtooth';\n" +
                "                    osc.frequency.setValueAtTime(freq, this.ctx.currentTime + idx * 0.12);\n" +
                "                    gain.gain.setValueAtTime(0.25, this.ctx.currentTime + idx * 0.12);\n" +
                "                    gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + idx * 0.12 + 0.2);\n" +
                "                    osc.connect(gain);\n" +
                "                    gain.connect(this.ctx.destination);\n" +
                "                    osc.start(this.ctx.currentTime + idx * 0.12);\n" +
                "                    osc.stop(this.ctx.currentTime + idx * 0.12 + 0.2);\n" +
                "                });\n" +
                "            } catch (e) {}\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    const sound = new SoundEngine();\n" +
                "\n" +
                "    // --- Canvas & DOM Elements ---\n" +
                "    const canvas = document.getElementById('gameCanvas');\n" +
                "    const ctx = canvas.getContext('2d');\n" +
                "    const scoreDisplay = document.getElementById('score-display');\n" +
                "    const highscoreDisplay = document.getElementById('highscore-display');\n" +
                "    const waveDisplay = document.getElementById('wave-display');\n" +
                "    const healthBar = document.getElementById('health-bar');\n" +
                "    const shieldBar = document.getElementById('shield-bar');\n" +
                "    const bossBarContainer = document.getElementById('boss-bar-container');\n" +
                "    const bossHealthBar = document.getElementById('boss-health-bar');\n" +
                "    const overlay = document.getElementById('overlay');\n" +
                "    const overlayTitle = document.getElementById('overlay-title');\n" +
                "    const overlaySubtitle = document.getElementById('overlay-subtitle');\n" +
                "    const statsSummary = document.getElementById('stats-summary');\n" +
                "    const finalScore = document.getElementById('final-score');\n" +
                "    const finalWave = document.getElementById('final-wave');\n" +
                "    const finalKills = document.getElementById('final-kills');\n" +
                "    const startBtn = document.getElementById('start-btn');\n" +
                "    const touchFireBtn = document.getElementById('touch-fire-btn');\n" +
                "\n" +
                "    // --- Game State ---\n" +
                "    let width = 800;\n" +
                "    let height = 600;\n" +
                "    let isRunning = false;\n" +
                "    let isPaused = false;\n" +
                "    let score = 0;\n" +
                "    let highScore = parseInt(localStorage.getItem('bridgemind_high_score') || '0', 10);\n" +
                "    let kills = 0;\n" +
                "    let wave = 1;\n" +
                "    let screenShake = 0;\n" +
                "    let lastTime = performance.now();\n" +
                "    let spawnTimer = 0;\n" +
                "    let bossActive = false;\n" +
                "    let boss = null;\n" +
                "\n" +
                "    highscoreDisplay.textContent = highScore;\n" +
                "\n" +
                "    // --- Starfield Background ---\n" +
                "    const stars = [];\n" +
                "    function initStars() {\n" +
                "        stars.length = 0;\n" +
                "        const count = Math.min(120, Math.floor((width * height) / 6000));\n" +
                "        for (let i = 0; i < count; i++) {\n" +
                "            stars.push({\n" +
                "                x: Math.random() * width,\n" +
                "                y: Math.random() * height,\n" +
                "                size: Math.random() * 2 + 0.5,\n" +
                "                speed: Math.random() * 1.5 + 0.3,\n" +
                "                brightness: Math.random() * 0.8 + 0.2\n" +
                "            });\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    function resize() {\n" +
                "        width = window.innerWidth;\n" +
                "        height = window.innerHeight;\n" +
                "        canvas.width = width;\n" +
                "        canvas.height = height;\n" +
                "        initStars();\n" +
                "        if (player) {\n" +
                "            player.x = Math.min(player.x, width - 40);\n" +
                "            player.y = Math.min(player.y, height - 60);\n" +
                "        }\n" +
                "    }\n" +
                "    window.addEventListener('resize', resize);\n" +
                "\n" +
                "    // --- Entities ---\n" +
                "    const keys = {};\n" +
                "    window.addEventListener('keydown', (e) => {\n" +
                "        keys[e.code] = true;\n" +
                "        if (e.code === 'KeyP' && isRunning) {\n" +
                "            isPaused = !isPaused;\n" +
                "            overlayTitle.textContent = isPaused ? 'SYSTEM PAUSED' : '';\n" +
                "            overlaySubtitle.textContent = isPaused ? 'PRESS P TO RESUME' : '';\n" +
                "            statsSummary.classList.add('hidden');\n" +
                "            overlay.classList.toggle('hidden', !isPaused);\n" +
                "            startBtn.classList.toggle('hidden', isPaused);\n" +
                "        }\n" +
                "        if (e.code === 'KeyR' && isRunning) restartGame();\n" +
                "    });\n" +
                "    window.addEventListener('keyup', (e) => { keys[e.code] = false; });\n" +
                "\n" +
                "    let mouseTargetX = null;\n" +
                "    let mouseTargetY = null;\n" +
                "    let isMouseDown = false;\n" +
                "\n" +
                "    canvas.addEventListener('mousemove', (e) => {\n" +
                "        mouseTargetX = e.clientX;\n" +
                "        mouseTargetY = e.clientY;\n" +
                "    });\n" +
                "    canvas.addEventListener('mousedown', (e) => {\n" +
                "        isMouseDown = true;\n" +
                "        sound.init();\n" +
                "    });\n" +
                "    window.addEventListener('mouseup', () => { isMouseDown = false; });\n" +
                "\n" +
                "    // Touch Navigation\n" +
                "    canvas.addEventListener('touchmove', (e) => {\n" +
                "        if (e.touches.length > 0) {\n" +
                "            mouseTargetX = e.touches[0].clientX;\n" +
                "            mouseTargetY = e.touches[0].clientY;\n" +
                "        }\n" +
                "    }, { passive: true });\n" +
                "    canvas.addEventListener('touchstart', (e) => {\n" +
                "        sound.init();\n" +
                "        if (e.touches.length > 0) {\n" +
                "            mouseTargetX = e.touches[0].clientX;\n" +
                "            mouseTargetY = e.touches[0].clientY;\n" +
                "        }\n" +
                "    }, { passive: true });\n" +
                "\n" +
                "    if (touchFireBtn) {\n" +
                "        touchFireBtn.addEventListener('touchstart', (e) => {\n" +
                "            e.preventDefault();\n" +
                "            isMouseDown = true;\n" +
                "            sound.init();\n" +
                "        });\n" +
                "        touchFireBtn.addEventListener('touchend', (e) => {\n" +
                "            e.preventDefault();\n" +
                "            isMouseDown = false;\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    // --- Player Class ---\n" +
                "    class Player {\n" +
                "        constructor() {\n" +
                "            this.width = 36;\n" +
                "            this.height = 42;\n" +
                "            this.x = width / 2;\n" +
                "            this.y = height - 100;\n" +
                "            this.speed = 420;\n" +
                "            this.health = 100;\n" +
                "            this.maxHealth = 100;\n" +
                "            this.shield = 100;\n" +
                "            this.maxShield = 100;\n" +
                "            this.fireCooldown = 0;\n" +
                "            this.fireRate = 0.15;\n" +
                "            this.weaponType = 'STANDARD'; // STANDARD, TRIPLE, RAPID\n" +
                "            this.powerupTimer = 0;\n" +
                "            this.invulnerableTimer = 0;\n" +
                "        }\n" +
                "        update(dt) {\n" +
                "            // Keyboard movement\n" +
                "            let dx = 0;\n" +
                "            let dy = 0;\n" +
                "            if (keys['KeyA'] || keys['ArrowLeft']) dx -= 1;\n" +
                "            if (keys['KeyD'] || keys['ArrowRight']) dx += 1;\n" +
                "            if (keys['KeyW'] || keys['ArrowUp']) dy -= 1;\n" +
                "            if (keys['KeyS'] || keys['ArrowDown']) dy += 1;\n" +
                "\n" +
                "            if (dx !== 0 && dy !== 0) {\n" +
                "                dx *= 0.7071;\n" +
                "                dy *= 0.7071;\n" +
                "            }\n" +
                "\n" +
                "            if (dx !== 0 || dy !== 0) {\n" +
                "                this.x += dx * this.speed * dt;\n" +
                "                this.y += dy * this.speed * dt;\n" +
                "                mouseTargetX = null;\n" +
                "            } else if (mouseTargetX !== null && mouseTargetY !== null) {\n" +
                "                const dist = mouseTargetX - this.x;\n" +
                "                if (Math.abs(dist) > 5) {\n" +
                "                    this.x += Math.sign(dist) * Math.min(Math.abs(dist), this.speed * 1.2 * dt);\n" +
                "                }\n" +
                "                const distY = mouseTargetY - this.y;\n" +
                "                if (Math.abs(distY) > 5) {\n" +
                "                    this.y += Math.sign(distY) * Math.min(Math.abs(distY), this.speed * 1.2 * dt);\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            // Clamp boundaries\n" +
                "            this.x = Math.max(20, Math.min(width - 20, this.x));\n" +
                "            this.y = Math.max(50, Math.min(height - 50, this.y));\n" +
                "\n" +
                "            // Regenerate shield slowly\n" +
                "            if (this.shield < this.maxShield) {\n" +
                "                this.shield = Math.min(this.maxShield, this.shield + 4 * dt);\n" +
                "            }\n" +
                "\n" +
                "            // Weapon timer\n" +
                "            if (this.powerupTimer > 0) {\n" +
                "                this.powerupTimer -= dt;\n" +
                "                if (this.powerupTimer <= 0) {\n" +
                "                    this.weaponType = 'STANDARD';\n" +
                "                    this.fireRate = 0.15;\n" +
                "                }\n" +
                "            }\n" +
                "            if (this.invulnerableTimer > 0) {\n" +
                "                this.invulnerableTimer -= dt;\n" +
                "            }\n" +
                "\n" +
                "            // Fire weapon\n" +
                "            this.fireCooldown -= dt;\n" +
                "            const firing = keys['Space'] || isMouseDown;\n" +
                "            if (firing && this.fireCooldown <= 0) {\n" +
                "                this.shoot();\n" +
                "                this.fireCooldown = this.fireRate;\n" +
                "            }\n" +
                "\n" +
                "            // Engine thruster particles\n" +
                "            if (Math.random() < 0.8) {\n" +
                "                particles.push(new Particle(this.x + (Math.random() * 8 - 4), this.y + 20, (Math.random() * 20 - 10), Math.random() * 80 + 120, '#00f3ff', 0.4, 3));\n" +
                "            }\n" +
                "        }\n" +
                "        shoot() {\n" +
                "            sound.playLaser();\n" +
                "            if (this.weaponType === 'TRIPLE') {\n" +
                "                lasers.push(new Laser(this.x, this.y - 15, 0, -750, '#00f3ff'));\n" +
                "                lasers.push(new Laser(this.x - 12, this.y - 10, -180, -720, '#00f3ff'));\n" +
                "                lasers.push(new Laser(this.x + 12, this.y - 10, 180, -720, '#00f3ff'));\n" +
                "            } else if (this.weaponType === 'RAPID') {\n" +
                "                lasers.push(new Laser(this.x - 8, this.y - 15, 0, -850, '#ff007f'));\n" +
                "                lasers.push(new Laser(this.x + 8, this.y - 15, 0, -850, '#ff007f'));\n" +
                "            } else {\n" +
                "                lasers.push(new Laser(this.x - 10, this.y - 15, 0, -750, '#00f3ff'));\n" +
                "                lasers.push(new Laser(this.x + 10, this.y - 15, 0, -750, '#00f3ff'));\n" +
                "            }\n" +
                "        }\n" +
                "        takeDamage(amount) {\n" +
                "            if (this.invulnerableTimer > 0) return;\n" +
                "            screenShake = 12;\n" +
                "            if (this.shield > 0) {\n" +
                "                const absorbed = Math.min(this.shield, amount);\n" +
                "                this.shield -= absorbed;\n" +
                "                amount -= absorbed;\n" +
                "            }\n" +
                "            this.health -= amount;\n" +
                "            this.invulnerableTimer = 0.25;\n" +
                "            if (this.health <= 0) {\n" +
                "                this.health = 0;\n" +
                "                gameOver();\n" +
                "            }\n" +
                "        }\n" +
                "        draw() {\n" +
                "            ctx.save();\n" +
                "            ctx.translate(this.x, this.y);\n" +
                "\n" +
                "            // Shield Glow\n" +
                "            if (this.shield > 10) {\n" +
                "                ctx.beginPath();\n" +
                "                ctx.arc(0, 0, 28, 0, Math.PI * 2);\n" +
                "                ctx.strokeStyle = `rgba(0, 243, 255, ${Math.min(0.6, this.shield / 100)})`;\n" +
                "                ctx.lineWidth = 2;\n" +
                "                ctx.shadowColor = '#00f3ff';\n" +
                "                ctx.shadowBlur = 12;\n" +
                "                ctx.stroke();\n" +
                "            }\n" +
                "\n" +
                "            // Ship Hull (Sleek Sci-Fi Vector Fighter)\n" +
                "            ctx.beginPath();\n" +
                "            ctx.moveTo(0, -22); // Nose\n" +
                "            ctx.lineTo(18, 16);  // Right Wing\n" +
                "            ctx.lineTo(6, 12);   // Right Thruster\n" +
                "            ctx.lineTo(0, 18);   // Center Engine\n" +
                "            ctx.lineTo(-6, 12);  // Left Thruster\n" +
                "            ctx.lineTo(-18, 16); // Left Wing\n" +
                "            ctx.closePath();\n" +
                "\n" +
                "            ctx.fillStyle = '#0f172a';\n" +
                "            ctx.fill();\n" +
                "            ctx.strokeStyle = '#00f3ff';\n" +
                "            ctx.lineWidth = 2;\n" +
                "            ctx.shadowColor = '#00f3ff';\n" +
                "            ctx.shadowBlur = 10;\n" +
                "            ctx.stroke();\n" +
                "\n" +
                "            // Cockpit Canopy\n" +
                "            ctx.beginPath();\n" +
                "            ctx.moveTo(0, -12);\n" +
                "            ctx.lineTo(4, 2);\n" +
                "            ctx.lineTo(0, 5);\n" +
                "            ctx.lineTo(-4, 2);\n" +
                "            ctx.closePath();\n" +
                "            ctx.fillStyle = '#ff007f';\n" +
                "            ctx.shadowColor = '#ff007f';\n" +
                "            ctx.shadowBlur = 6;\n" +
                "            ctx.fill();\n" +
                "\n" +
                "            ctx.restore();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // --- Laser Projectile ---\n" +
                "    class Laser {\n" +
                "        constructor(x, y, vx, vy, color) {\n" +
                "            this.x = x;\n" +
                "            this.y = y;\n" +
                "            this.vx = vx;\n" +
                "            this.vy = vy;\n" +
                "            this.color = color || '#00f3ff';\n" +
                "            this.alive = true;\n" +
                "        }\n" +
                "        update(dt) {\n" +
                "            this.x += this.vx * dt;\n" +
                "            this.y += this.vy * dt;\n" +
                "            if (this.y < -30 || this.x < -30 || this.x > width + 30) {\n" +
                "                this.alive = false;\n" +
                "            }\n" +
                "        }\n" +
                "        draw() {\n" +
                "            ctx.save();\n" +
                "            ctx.strokeStyle = this.color;\n" +
                "            ctx.lineWidth = 3;\n" +
                "            ctx.shadowColor = this.color;\n" +
                "            ctx.shadowBlur = 8;\n" +
                "            ctx.beginPath();\n" +
                "            ctx.moveTo(this.x, this.y);\n" +
                "            ctx.lineTo(this.x + this.vx * 0.02, this.y + this.vy * 0.02);\n" +
                "            ctx.stroke();\n" +
                "            ctx.restore();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // --- Enemy & Asteroids ---\n" +
                "    class Enemy {\n" +
                "        constructor(type) {\n" +
                "            this.type = type || (Math.random() < 0.65 ? 'ASTEROID' : 'DRONE');\n" +
                "            this.x = Math.random() * (width - 60) + 30;\n" +
                "            this.y = -40;\n" +
                "            this.alive = true;\n" +
                "            if (this.type === 'ASTEROID') {\n" +
                "                this.radius = Math.random() * 16 + 14;\n" +
                "                this.speed = Math.random() * 120 + 80 + wave * 10;\n" +
                "                this.health = Math.ceil(this.radius / 10);\n" +
                "                this.points = 100;\n" +
                "                this.angle = 0;\n" +
                "                this.rotSpeed = (Math.random() - 0.5) * 3;\n" +
                "                this.vertices = [];\n" +
                "                const numPts = 7;\n" +
                "                for (let i = 0; i < numPts; i++) {\n" +
                "                    const a = (i / numPts) * Math.PI * 2;\n" +
                "                    const r = this.radius * (0.75 + Math.random() * 0.5);\n" +
                "                    this.vertices.push({ x: Math.cos(a) * r, y: Math.sin(a) * r });\n" +
                "                }\n" +
                "            } else {\n" +
                "                this.radius = 18;\n" +
                "                this.speed = Math.random() * 140 + 100 + wave * 15;\n" +
                "                this.health = 3;\n" +
                "                this.points = 250;\n" +
                "                this.baseX = this.x;\n" +
                "                this.swayFreq = Math.random() * 2 + 1;\n" +
                "                this.time = 0;\n" +
                "            }\n" +
                "        }\n" +
                "        update(dt) {\n" +
                "            this.y += this.speed * dt;\n" +
                "            if (this.type === 'ASTEROID') {\n" +
                "                this.angle += this.rotSpeed * dt;\n" +
                "            } else {\n" +
                "                this.time += dt;\n" +
                "                this.x = this.baseX + Math.sin(this.time * this.swayFreq) * 60;\n" +
                "            }\n" +
                "            if (this.y > height + 50) this.alive = false;\n" +
                "        }\n" +
                "        draw() {\n" +
                "            ctx.save();\n" +
                "            ctx.translate(this.x, this.y);\n" +
                "            if (this.type === 'ASTEROID') {\n" +
                "                ctx.rotate(this.angle);\n" +
                "                ctx.beginPath();\n" +
                "                this.vertices.forEach((v, i) => {\n" +
                "                    if (i === 0) ctx.moveTo(v.x, v.y);\n" +
                "                    else ctx.lineTo(v.x, v.y);\n" +
                "                });\n" +
                "                ctx.closePath();\n" +
                "                ctx.fillStyle = '#1e293b';\n" +
                "                ctx.fill();\n" +
                "                ctx.strokeStyle = '#94a3b8';\n" +
                "                ctx.lineWidth = 2;\n" +
                "                ctx.stroke();\n" +
                "            } else {\n" +
                "                // Drone\n" +
                "                ctx.beginPath();\n" +
                "                ctx.moveTo(0, 18);\n" +
                "                ctx.lineTo(14, -14);\n" +
                "                ctx.lineTo(0, -6);\n" +
                "                ctx.lineTo(-14, -14);\n" +
                "                ctx.closePath();\n" +
                "                ctx.fillStyle = '#450a0a';\n" +
                "                ctx.fill();\n" +
                "                ctx.strokeStyle = '#ef4444';\n" +
                "                ctx.lineWidth = 2;\n" +
                "                ctx.shadowColor = '#ef4444';\n" +
                "                ctx.shadowBlur = 8;\n" +
                "                ctx.stroke();\n" +
                "            }\n" +
                "            ctx.restore();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // --- Boss Class ---\n" +
                "    class Boss {\n" +
                "        constructor() {\n" +
                "            this.x = width / 2;\n" +
                "            this.y = -80;\n" +
                "            this.targetY = 110;\n" +
                "            this.radius = 48;\n" +
                "            this.health = 35 + wave * 15;\n" +
                "            this.maxHealth = this.health;\n" +
                "            this.alive = true;\n" +
                "            this.speedX = 140;\n" +
                "            this.dir = 1;\n" +
                "        }\n" +
                "        update(dt) {\n" +
                "            if (this.y < this.targetY) {\n" +
                "                this.y += 60 * dt;\n" +
                "            } else {\n" +
                "                this.x += this.speedX * this.dir * dt;\n" +
                "                if (this.x > width - 80) this.dir = -1;\n" +
                "                if (this.x < 80) this.dir = 1;\n" +
                "            }\n" +
                "        }\n" +
                "        draw() {\n" +
                "            ctx.save();\n" +
                "            ctx.translate(this.x, this.y);\n" +
                "            ctx.beginPath();\n" +
                "            ctx.moveTo(0, 36);\n" +
                "            ctx.lineTo(44, -20);\n" +
                "            ctx.lineTo(24, -36);\n" +
                "            ctx.lineTo(-24, -36);\n" +
                "            ctx.lineTo(-44, -20);\n" +
                "            ctx.closePath();\n" +
                "            ctx.fillStyle = '#2b0a3d';\n" +
                "            ctx.fill();\n" +
                "            ctx.strokeStyle = '#ff007f';\n" +
                "            ctx.lineWidth = 3;\n" +
                "            ctx.shadowColor = '#ff007f';\n" +
                "            ctx.shadowBlur = 16;\n" +
                "            ctx.stroke();\n" +
                "\n" +
                "            // Core Eye\n" +
                "            ctx.beginPath();\n" +
                "            ctx.arc(0, 0, 12, 0, Math.PI * 2);\n" +
                "            ctx.fillStyle = '#00f3ff';\n" +
                "            ctx.shadowColor = '#00f3ff';\n" +
                "            ctx.shadowBlur = 12;\n" +
                "            ctx.fill();\n" +
                "            ctx.restore();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // --- Powerup Class ---\n" +
                "    class Powerup {\n" +
                "        constructor(x, y) {\n" +
                "            this.x = x;\n" +
                "            this.y = y;\n" +
                "            const kinds = ['TRIPLE', 'RAPID', 'SHIELD'];\n" +
                "            this.kind = kinds[Math.floor(Math.random() * kinds.length)];\n" +
                "            this.alive = true;\n" +
                "            this.speed = 100;\n" +
                "            this.radius = 14;\n" +
                "            this.time = 0;\n" +
                "        }\n" +
                "        update(dt) {\n" +
                "            this.y += this.speed * dt;\n" +
                "            this.time += dt;\n" +
                "            if (this.y > height + 30) this.alive = false;\n" +
                "        }\n" +
                "        draw() {\n" +
                "            ctx.save();\n" +
                "            ctx.translate(this.x, this.y);\n" +
                "            ctx.beginPath();\n" +
                "            ctx.arc(0, 0, this.radius + Math.sin(this.time * 6) * 2, 0, Math.PI * 2);\n" +
                "            ctx.fillStyle = this.kind === 'SHIELD' ? '#00f3ff' : (this.kind === 'TRIPLE' ? '#ffaa00' : '#ff007f');\n" +
                "            ctx.shadowColor = ctx.fillStyle;\n" +
                "            ctx.shadowBlur = 10;\n" +
                "            ctx.fill();\n" +
                "            ctx.fillStyle = '#000';\n" +
                "            ctx.font = 'bold 9px Orbitron';\n" +
                "            ctx.textAlign = 'center';\n" +
                "            ctx.textBaseline = 'middle';\n" +
                "            ctx.fillText(this.kind[0], 0, 1);\n" +
                "            ctx.restore();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // --- Particle System ---\n" +
                "    class Particle {\n" +
                "        constructor(x, y, vx, vy, color, life, size) {\n" +
                "            this.x = x;\n" +
                "            this.y = y;\n" +
                "            this.vx = vx;\n" +
                "            this.vy = vy;\n" +
                "            this.color = color;\n" +
                "            this.life = life || 0.5;\n" +
                "            this.maxLife = this.life;\n" +
                "            this.size = size || 3;\n" +
                "        }\n" +
                "        update(dt) {\n" +
                "            this.x += this.vx * dt;\n" +
                "            this.y += this.vy * dt;\n" +
                "            this.life -= dt;\n" +
                "        }\n" +
                "        draw() {\n" +
                "            if (this.life <= 0) return;\n" +
                "            ctx.save();\n" +
                "            ctx.globalAlpha = Math.max(0, this.life / this.maxLife);\n" +
                "            ctx.fillStyle = this.color;\n" +
                "            ctx.beginPath();\n" +
                "            ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);\n" +
                "            ctx.fill();\n" +
                "            ctx.restore();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    function createExplosion(x, y, color, count) {\n" +
                "        sound.playExplosion();\n" +
                "        const n = count || 16;\n" +
                "        for (let i = 0; i < n; i++) {\n" +
                "            const angle = Math.random() * Math.PI * 2;\n" +
                "            const spd = Math.random() * 200 + 40;\n" +
                "            particles.push(new Particle(x, y, Math.cos(angle) * spd, Math.sin(angle) * spd, color || '#ffaa00', Math.random() * 0.4 + 0.3, Math.random() * 3 + 2));\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // --- Floating Text ---\n" +
                "    const floatingTexts = [];\n" +
                "    function addFloatingText(text, x, y, color) {\n" +
                "        floatingTexts.push({\n" +
                "            text: text,\n" +
                "            x: x,\n" +
                "            y: y,\n" +
                "            color: color || '#00ff88',\n" +
                "            life: 0.8,\n" +
                "            maxLife: 0.8\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    // --- Collections ---\n" +
                "    let player = null;\n" +
                "    const lasers = [];\n" +
                "    const enemies = [];\n" +
                "    const powerups = [];\n" +
                "    const particles = [];\n" +
                "\n" +
                "    function startGame() {\n" +
                "        sound.init();\n" +
                "        score = 0;\n" +
                "        kills = 0;\n" +
                "        wave = 1;\n" +
                "        spawnTimer = 0;\n" +
                "        bossActive = false;\n" +
                "        boss = null;\n" +
                "        lasers.length = 0;\n" +
                "        enemies.length = 0;\n" +
                "        powerups.length = 0;\n" +
                "        particles.length = 0;\n" +
                "        floatingTexts.length = 0;\n" +
                "        player = new Player();\n" +
                "        isRunning = true;\n" +
                "        isPaused = false;\n" +
                "        overlay.classList.add('hidden');\n" +
                "        bossBarContainer.classList.add('hidden');\n" +
                "        updateHUD();\n" +
                "    }\n" +
                "\n" +
                "    function restartGame() {\n" +
                "        startGame();\n" +
                "    }\n" +
                "\n" +
                "    function gameOver() {\n" +
                "        isRunning = false;\n" +
                "        sound.playGameOver();\n" +
                "        if (score > highScore) {\n" +
                "            highScore = score;\n" +
                "            localStorage.setItem('bridgemind_high_score', highScore.toString());\n" +
                "            highscoreDisplay.textContent = highScore;\n" +
                "        }\n" +
                "        overlayTitle.textContent = 'VESSEL COMPROMISED';\n" +
                "        overlaySubtitle.textContent = 'SECTOR DEFENSE TERMINATED';\n" +
                "        finalScore.textContent = score;\n" +
                "        finalWave.textContent = wave;\n" +
                "        finalKills.textContent = kills;\n" +
                "        statsSummary.classList.remove('hidden');\n" +
                "        startBtn.querySelector('.btn-text').textContent = 'RE-ENGAGE SECTOR';\n" +
                "        startBtn.classList.remove('hidden');\n" +
                "        overlay.classList.remove('hidden');\n" +
                "    }\n" +
                "\n" +
                "    function updateHUD() {\n" +
                "        if (!player) return;\n" +
                "        scoreDisplay.textContent = score;\n" +
                "        waveDisplay.textContent = wave;\n" +
                "        healthBar.style.width = Math.max(0, (player.health / player.maxHealth) * 100) + '%';\n" +
                "        shieldBar.style.width = Math.max(0, (player.shield / player.maxShield) * 100) + '%';\n" +
                "        if (boss && bossActive) {\n" +
                "            bossHealthBar.style.width = Math.max(0, (boss.health / boss.maxHealth) * 100) + '%';\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // --- Main Game Loop ---\n" +
                "    function gameLoop(now) {\n" +
                "        const dt = Math.min((now - lastTime) / 1000, 0.1);\n" +
                "        lastTime = now;\n" +
                "\n" +
                "        if (isRunning && !isPaused) {\n" +
                "            // Update Player\n" +
                "            if (player) player.update(dt);\n" +
                "\n" +
                "            // Spawn Logic\n" +
                "            spawnTimer += dt;\n" +
                "            const spawnInterval = Math.max(0.45, 1.3 - wave * 0.1);\n" +
                "            if (spawnTimer >= spawnInterval && !bossActive) {\n" +
                "                spawnTimer = 0;\n" +
                "                enemies.push(new Enemy());\n" +
                "            }\n" +
                "\n" +
                "            // Trigger Boss Fight every 1500 points\n" +
                "            if (!bossActive && score > 0 && score >= wave * 1500) {\n" +
                "                bossActive = true;\n" +
                "                boss = new Boss();\n" +
                "                bossBarContainer.classList.remove('hidden');\n" +
                "                addFloatingText('⚠️ WARNING: BOSS VESSEL DETECTED!', width / 2, height / 3, '#ff007f');\n" +
                "            }\n" +
                "\n" +
                "            // Update Lasers\n" +
                "            lasers.forEach(l => l.update(dt));\n" +
                "            for (let i = lasers.length - 1; i >= 0; i--) {\n" +
                "                if (!lasers[i].alive) lasers.splice(i, 1);\n" +
                "            }\n" +
                "\n" +
                "            // Update Enemies\n" +
                "            enemies.forEach(e => e.update(dt));\n" +
                "            for (let i = enemies.length - 1; i >= 0; i--) {\n" +
                "                if (!enemies[i].alive) enemies.splice(i, 1);\n" +
                "            }\n" +
                "\n" +
                "            // Update Boss\n" +
                "            if (boss && bossActive) {\n" +
                "                boss.update(dt);\n" +
                "                // Collision player laser with boss\n" +
                "                lasers.forEach(laser => {\n" +
                "                    if (!laser.alive) return;\n" +
                "                    const dist = Math.hypot(laser.x - boss.x, laser.y - boss.y);\n" +
                "                    if (dist < boss.radius) {\n" +
                "                        laser.alive = false;\n" +
                "                        boss.health -= 1;\n" +
                "                        createExplosion(laser.x, laser.y, '#ff007f', 4);\n" +
                "                        if (boss.health <= 0) {\n" +
                "                            boss.alive = false;\n" +
                "                            bossActive = false;\n" +
                "                            bossBarContainer.classList.add('hidden');\n" +
                "                            createExplosion(boss.x, boss.y, '#ff007f', 40);\n" +
                "                            score += 1000;\n" +
                "                            kills += 1;\n" +
                "                            wave += 1;\n" +
                "                            addFloatingText('💥 BOSS DESTROYED! +1000', boss.x, boss.y, '#00ff88');\n" +
                "                            powerups.push(new Powerup(boss.x, boss.y));\n" +
                "                        }\n" +
                "                    }\n" +
                "                });\n" +
                "            }\n" +
                "\n" +
                "            // Update Powerups\n" +
                "            powerups.forEach(p => p.update(dt));\n" +
                "            for (let i = powerups.length - 1; i >= 0; i--) {\n" +
                "                if (!powerups[i].alive) powerups.splice(i, 1);\n" +
                "            }\n" +
                "\n" +
                "            // Collisions: Lasers vs Enemies\n" +
                "            lasers.forEach(laser => {\n" +
                "                if (!laser.alive) return;\n" +
                "                enemies.forEach(enemy => {\n" +
                "                    if (!enemy.alive) return;\n" +
                "                    const dist = Math.hypot(laser.x - enemy.x, laser.y - enemy.y);\n" +
                "                    if (dist < enemy.radius + 6) {\n" +
                "                        laser.alive = false;\n" +
                "                        enemy.health -= 1;\n" +
                "                        createExplosion(laser.x, laser.y, '#00f3ff', 3);\n" +
                "                        if (enemy.health <= 0) {\n" +
                "                            enemy.alive = false;\n" +
                "                            createExplosion(enemy.x, enemy.y, enemy.type === 'DRONE' ? '#ef4444' : '#94a3b8', 14);\n" +
                "                            score += enemy.points;\n" +
                "                            kills += 1;\n" +
                "                            addFloatingText(`+${enemy.points}`, enemy.x, enemy.y, '#00f3ff');\n" +
                "                            // Chance to drop powerup\n" +
                "                            if (Math.random() < 0.15) {\n" +
                "                                powerups.push(new Powerup(enemy.x, enemy.y));\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                });\n" +
                "            });\n" +
                "\n" +
                "            // Collisions: Player vs Enemies\n" +
                "            if (player) {\n" +
                "                enemies.forEach(enemy => {\n" +
                "                    if (!enemy.alive) return;\n" +
                "                    const dist = Math.hypot(player.x - enemy.x, player.y - enemy.y);\n" +
                "                    if (dist < enemy.radius + 18) {\n" +
                "                        enemy.alive = false;\n" +
                "                        createExplosion(enemy.x, enemy.y, '#ef4444', 16);\n" +
                "                        player.takeDamage(25);\n" +
                "                    }\n" +
                "                });\n" +
                "\n" +
                "                // Collisions: Player vs Powerups\n" +
                "                powerups.forEach(p => {\n" +
                "                    if (!p.alive) return;\n" +
                "                    const dist = Math.hypot(player.x - p.x, player.y - p.y);\n" +
                "                    if (dist < p.radius + 20) {\n" +
                "                        p.alive = false;\n" +
                "                        sound.playPowerup();\n" +
                "                        if (p.kind === 'SHIELD') {\n" +
                "                            player.shield = 100;\n" +
                "                            player.health = Math.min(100, player.health + 20);\n" +
                "                            addFloatingText('🛡️ SHIELD MAX', player.x, player.y - 20, '#00f3ff');\n" +
                "                        } else if (p.kind === 'TRIPLE') {\n" +
                "                            player.weaponType = 'TRIPLE';\n" +
                "                            player.powerupTimer = 10;\n" +
                "                            addFloatingText('⚡ TRIPLE CANNON', player.x, player.y - 20, '#ffaa00');\n" +
                "                        } else if (p.kind === 'RAPID') {\n" +
                "                            player.weaponType = 'RAPID';\n" +
                "                            player.fireRate = 0.08;\n" +
                "                            player.powerupTimer = 10;\n" +
                "                            addFloatingText('🔥 OVERCHARGED PULSE', player.x, player.y - 20, '#ff007f');\n" +
                "                        }\n" +
                "                    }\n" +
                "                });\n" +
                "            }\n" +
                "\n" +
                "            updateHUD();\n" +
                "        }\n" +
                "\n" +
                "        // Update Particles\n" +
                "        particles.forEach(p => p.update(dt));\n" +
                "        for (let i = particles.length - 1; i >= 0; i--) {\n" +
                "            if (particles[i].life <= 0) particles.splice(i, 1);\n" +
                "        }\n" +
                "\n" +
                "        // Update Floating Texts\n" +
                "        floatingTexts.forEach(t => {\n" +
                "            t.y -= 30 * dt;\n" +
                "            t.life -= dt;\n" +
                "        });\n" +
                "        for (let i = floatingTexts.length - 1; i >= 0; i--) {\n" +
                "            if (floatingTexts[i].life <= 0) floatingTexts.splice(i, 1);\n" +
                "        }\n" +
                "\n" +
                "        // --- Render Frame ---\n" +
                "        ctx.save();\n" +
                "        if (screenShake > 0) {\n" +
                "            ctx.translate((Math.random() - 0.5) * screenShake, (Math.random() - 0.5) * screenShake);\n" +
                "            screenShake = Math.max(0, screenShake - dt * 30);\n" +
                "        }\n" +
                "\n" +
                "        ctx.fillStyle = '#070913';\n" +
                "        ctx.fillRect(0, 0, width, height);\n" +
                "\n" +
                "        // Render Starfield\n" +
                "        ctx.fillStyle = '#ffffff';\n" +
                "        stars.forEach(s => {\n" +
                "            s.y += s.speed * (isRunning ? 1.5 : 0.4);\n" +
                "            if (s.y > height) {\n" +
                "                s.y = 0;\n" +
                "                s.x = Math.random() * width;\n" +
                "            }\n" +
                "            ctx.globalAlpha = s.brightness;\n" +
                "            ctx.beginPath();\n" +
                "            ctx.arc(s.x, s.y, s.size, 0, Math.PI * 2);\n" +
                "            ctx.fill();\n" +
                "        });\n" +
                "        ctx.globalAlpha = 1;\n" +
                "\n" +
                "        // Render Game Entities\n" +
                "        powerups.forEach(p => p.draw());\n" +
                "        lasers.forEach(l => l.draw());\n" +
                "        enemies.forEach(e => e.draw());\n" +
                "        if (boss && bossActive) boss.draw();\n" +
                "        if (player && isRunning) player.draw();\n" +
                "        particles.forEach(p => p.draw());\n" +
                "\n" +
                "        // Render Floating Texts\n" +
                "        ctx.font = 'bold 14px Orbitron';\n" +
                "        ctx.textAlign = 'center';\n" +
                "        floatingTexts.forEach(t => {\n" +
                "            ctx.fillStyle = t.color;\n" +
                "            ctx.globalAlpha = Math.max(0, t.life / t.maxLife);\n" +
                "            ctx.shadowColor = t.color;\n" +
                "            ctx.shadowBlur = 8;\n" +
                "            ctx.fillText(t.text, t.x, t.y);\n" +
                "        });\n" +
                "        ctx.globalAlpha = 1;\n" +
                "\n" +
                "        ctx.restore();\n" +
                "        requestAnimationFrame(gameLoop);\n" +
                "    }\n" +
                "\n" +
                "    // --- Event Listeners ---\n" +
                "    startBtn.addEventListener('click', () => {\n" +
                "        startGame();\n" +
                "    });\n" +
                "\n" +
                "    // Auto-initialize canvas & start animation loop\n" +
                "    resize();\n" +
                "    requestAnimationFrame(gameLoop);\n" +
                "})();\n";
    }

    public static String getStandalonePlayableHtml(String missionTitle) {
        String safeTitle = (missionTitle != null && !missionTitle.isBlank()) ? missionTitle : "BridgeMind Space Runner";
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
                "    <title>" + escapeHtml(safeTitle) + "</title>\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Rajdhani:wght@500;700&display=swap\" rel=\"stylesheet\">\n" +
                "    <style>\n" +
                getCssContent() +
                "    </style>\n" +
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
                "                <div id=\"boss-bar-container\" class=\"boss-bar-container hidden\">\n" +
                "                    <div class=\"boss-label\">ALERT: DREADNOUGHT CLASS ENEMY</div>\n" +
                "                    <div class=\"boss-bar-bg\"><div id=\"boss-health-bar\" class=\"boss-bar-fill\"></div></div>\n" +
                "                </div>\n" +
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
                "                    <div class=\"stat-row\"><span>ENEMIES PURGED:</span><strong id=\"final-kills\">0</strong></div>\n" +
                "                </div>\n" +
                "                <div class=\"controls-guide\">\n" +
                "                    <div class=\"guide-item\"><span>W A S D / ARROWS</span> &bull; <span>NAVIGATE</span></div>\n" +
                "                    <div class=\"guide-item\"><span>SPACE / CLICK / TAP</span> &bull; <span>PLASMA CANNONS</span></div>\n" +
                "                    <div class=\"guide-item\"><span>P</span> &bull; <span>PAUSE</span> &bull; <span>R</span> &bull; <span>RESTART</span></div>\n" +
                "                </div>\n" +
                "                <button id=\"start-btn\" class=\"cyber-btn\">\n" +
                "                    <span class=\"btn-glitch\"></span>\n" +
                "                    <span class=\"btn-text\">LAUNCH VESSEL</span>\n" +
                "                </button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div id=\"mobile-controls\" class=\"mobile-controls\">\n" +
                "            <div id=\"touch-fire-btn\" class=\"touch-btn fire-btn\">FIRE</div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                getJsContent() +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
