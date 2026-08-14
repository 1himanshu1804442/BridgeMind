/**
 * BridgeMind Space Runner - HTML5 Canvas Live Arcade Engine
 * Complete high-performance game loop with Web Audio API sound synthesis.
 */
(function () {
    'use strict';

    // --- Audio Synthesizer (Zero External Dependencies) ---
    class SoundEngine {
        constructor() {
            this.ctx = null;
        }
        init() {
            if (!this.ctx) {
                const AudioCtx = window.AudioContext || window.webkitAudioContext;
                if (AudioCtx) this.ctx = new AudioCtx();
            }
            if (this.ctx && this.ctx.state === 'suspended') {
                this.ctx.resume();
            }
        }
        playLaser() {
            if (!this.ctx) return;
            try {
                const osc = this.ctx.createOscillator();
                const gain = this.ctx.createGain();
                osc.type = 'sawtooth';
                osc.frequency.setValueAtTime(880, this.ctx.currentTime);
                osc.frequency.exponentialRampToValueAtTime(110, this.ctx.currentTime + 0.12);
                gain.gain.setValueAtTime(0.2, this.ctx.currentTime);
                gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.12);
                osc.connect(gain);
                gain.connect(this.ctx.destination);
                osc.start();
                osc.stop(this.ctx.currentTime + 0.12);
            } catch (e) {}
        }
        playExplosion() {
            if (!this.ctx) return;
            try {
                const bufferSize = this.ctx.sampleRate * 0.3;
                const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);
                const data = buffer.getChannelData(0);
                for (let i = 0; i < bufferSize; i++) {
                    data[i] = Math.random() * 2 - 1;
                }
                const noise = this.ctx.createBufferSource();
                noise.buffer = buffer;
                const filter = this.ctx.createBiquadFilter();
                filter.type = 'lowpass';
                filter.frequency.setValueAtTime(600, this.ctx.currentTime);
                filter.frequency.linearRampToValueAtTime(80, this.ctx.currentTime + 0.3);
                const gain = this.ctx.createGain();
                gain.gain.setValueAtTime(0.35, this.ctx.currentTime);
                gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.3);
                noise.connect(filter);
                filter.connect(gain);
                gain.connect(this.ctx.destination);
                noise.start();
            } catch (e) {}
        }
        playPowerup() {
            if (!this.ctx) return;
            try {
                const notes = [330, 440, 554, 659];
                notes.forEach((freq, idx) => {
                    const osc = this.ctx.createOscillator();
                    const gain = this.ctx.createGain();
                    osc.type = 'triangle';
                    osc.frequency.setValueAtTime(freq, this.ctx.currentTime + idx * 0.05);
                    gain.gain.setValueAtTime(0.2, this.ctx.currentTime + idx * 0.05);
                    gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + idx * 0.05 + 0.1);
                    osc.connect(gain);
                    gain.connect(this.ctx.destination);
                    osc.start(this.ctx.currentTime + idx * 0.05);
                    osc.stop(this.ctx.currentTime + idx * 0.05 + 0.1);
                });
            } catch (e) {}
        }
        playGameOver() {
            if (!this.ctx) return;
            try {
                const notes = [220, 196, 174, 146];
                notes.forEach((freq, idx) => {
                    const osc = this.ctx.createOscillator();
                    const gain = this.ctx.createGain();
                    osc.type = 'sawtooth';
                    osc.frequency.setValueAtTime(freq, this.ctx.currentTime + idx * 0.12);
                    gain.gain.setValueAtTime(0.25, this.ctx.currentTime + idx * 0.12);
                    gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + idx * 0.12 + 0.2);
                    osc.connect(gain);
                    gain.connect(this.ctx.destination);
                    osc.start(this.ctx.currentTime + idx * 0.12);
                    osc.stop(this.ctx.currentTime + idx * 0.12 + 0.2);
                });
            } catch (e) {}
        }
    }

    const sound = new SoundEngine();

    // --- Canvas & DOM Elements ---
    const canvas = document.getElementById('gameCanvas');
    const ctx = canvas.getContext('2d');
    const scoreDisplay = document.getElementById('score-display');
    const highscoreDisplay = document.getElementById('highscore-display');
    const waveDisplay = document.getElementById('wave-display');
    const healthBar = document.getElementById('health-bar');
    const shieldBar = document.getElementById('shield-bar');
    const bossBarContainer = document.getElementById('boss-bar-container');
    const bossHealthBar = document.getElementById('boss-health-bar');
    const overlay = document.getElementById('overlay');
    const overlayTitle = document.getElementById('overlay-title');
    const overlaySubtitle = document.getElementById('overlay-subtitle');
    const statsSummary = document.getElementById('stats-summary');
    const finalScore = document.getElementById('final-score');
    const finalWave = document.getElementById('final-wave');
    const finalKills = document.getElementById('final-kills');
    const startBtn = document.getElementById('start-btn');
    const touchFireBtn = document.getElementById('touch-fire-btn');

    // --- Game State ---
    let width = 800;
    let height = 600;
    let isRunning = false;
    let isPaused = false;
    let score = 0;
    let highScore = parseInt(localStorage.getItem('bridgemind_high_score') || '0', 10);
    let kills = 0;
    let wave = 1;
    let screenShake = 0;
    let lastTime = performance.now();
    let spawnTimer = 0;
    let bossActive = false;
    let boss = null;

    highscoreDisplay.textContent = highScore;

    // --- Starfield Background ---
    const stars = [];
    function initStars() {
        stars.length = 0;
        const count = Math.min(120, Math.floor((width * height) / 6000));
        for (let i = 0; i < count; i++) {
            stars.push({
                x: Math.random() * width,
                y: Math.random() * height,
                size: Math.random() * 2 + 0.5,
                speed: Math.random() * 1.5 + 0.3,
                brightness: Math.random() * 0.8 + 0.2
            });
        }
    }

    function resize() {
        width = window.innerWidth;
        height = window.innerHeight;
        canvas.width = width;
        canvas.height = height;
        initStars();
        if (player) {
            player.x = Math.min(player.x, width - 40);
            player.y = Math.min(player.y, height - 60);
        }
    }
    window.addEventListener('resize', resize);

    // --- Entities ---
    const keys = {};
    window.addEventListener('keydown', (e) => {
        keys[e.code] = true;
        if (e.code === 'KeyP' && isRunning) {
            isPaused = !isPaused;
            overlayTitle.textContent = isPaused ? 'SYSTEM PAUSED' : '';
            overlaySubtitle.textContent = isPaused ? 'PRESS P TO RESUME' : '';
            statsSummary.classList.add('hidden');
            overlay.classList.toggle('hidden', !isPaused);
            startBtn.classList.toggle('hidden', isPaused);
        }
        if (e.code === 'KeyR' && isRunning) restartGame();
    });
    window.addEventListener('keyup', (e) => { keys[e.code] = false; });

    let mouseTargetX = null;
    let mouseTargetY = null;
    let isMouseDown = false;

    canvas.addEventListener('mousemove', (e) => {
        mouseTargetX = e.clientX;
        mouseTargetY = e.clientY;
    });
    canvas.addEventListener('mousedown', (e) => {
        isMouseDown = true;
        sound.init();
    });
    window.addEventListener('mouseup', () => { isMouseDown = false; });

    // Touch Navigation
    canvas.addEventListener('touchmove', (e) => {
        if (e.touches.length > 0) {
            mouseTargetX = e.touches[0].clientX;
            mouseTargetY = e.touches[0].clientY;
        }
    }, { passive: true });
    canvas.addEventListener('touchstart', (e) => {
        sound.init();
        if (e.touches.length > 0) {
            mouseTargetX = e.touches[0].clientX;
            mouseTargetY = e.touches[0].clientY;
        }
    }, { passive: true });

    if (touchFireBtn) {
        touchFireBtn.addEventListener('touchstart', (e) => {
            e.preventDefault();
            isMouseDown = true;
            sound.init();
        });
        touchFireBtn.addEventListener('touchend', (e) => {
            e.preventDefault();
            isMouseDown = false;
        });
    }

    // --- Player Class ---
    class Player {
        constructor() {
            this.width = 36;
            this.height = 42;
            this.x = width / 2;
            this.y = height - 100;
            this.speed = 420;
            this.health = 100;
            this.maxHealth = 100;
            this.shield = 100;
            this.maxShield = 100;
            this.fireCooldown = 0;
            this.fireRate = 0.15;
            this.weaponType = 'STANDARD'; // STANDARD, TRIPLE, RAPID
            this.powerupTimer = 0;
            this.invulnerableTimer = 0;
        }
        update(dt) {
            // Keyboard movement
            let dx = 0;
            let dy = 0;
            if (keys['KeyA'] || keys['ArrowLeft']) dx -= 1;
            if (keys['KeyD'] || keys['ArrowRight']) dx += 1;
            if (keys['KeyW'] || keys['ArrowUp']) dy -= 1;
            if (keys['KeyS'] || keys['ArrowDown']) dy += 1;

            if (dx !== 0 && dy !== 0) {
                dx *= 0.7071;
                dy *= 0.7071;
            }

            if (dx !== 0 || dy !== 0) {
                this.x += dx * this.speed * dt;
                this.y += dy * this.speed * dt;
                mouseTargetX = null;
            } else if (mouseTargetX !== null && mouseTargetY !== null) {
                const dist = mouseTargetX - this.x;
                if (Math.abs(dist) > 5) {
                    this.x += Math.sign(dist) * Math.min(Math.abs(dist), this.speed * 1.2 * dt);
                }
                const distY = mouseTargetY - this.y;
                if (Math.abs(distY) > 5) {
                    this.y += Math.sign(distY) * Math.min(Math.abs(distY), this.speed * 1.2 * dt);
                }
            }

            // Clamp boundaries
            this.x = Math.max(20, Math.min(width - 20, this.x));
            this.y = Math.max(50, Math.min(height - 50, this.y));

            // Regenerate shield slowly
            if (this.shield < this.maxShield) {
                this.shield = Math.min(this.maxShield, this.shield + 4 * dt);
            }

            // Weapon timer
            if (this.powerupTimer > 0) {
                this.powerupTimer -= dt;
                if (this.powerupTimer <= 0) {
                    this.weaponType = 'STANDARD';
                    this.fireRate = 0.15;
                }
            }
            if (this.invulnerableTimer > 0) {
                this.invulnerableTimer -= dt;
            }

            // Fire weapon
            this.fireCooldown -= dt;
            const firing = keys['Space'] || isMouseDown;
            if (firing && this.fireCooldown <= 0) {
                this.shoot();
                this.fireCooldown = this.fireRate;
            }

            // Engine thruster particles
            if (Math.random() < 0.8) {
                particles.push(new Particle(this.x + (Math.random() * 8 - 4), this.y + 20, (Math.random() * 20 - 10), Math.random() * 80 + 120, '#00f3ff', 0.4, 3));
            }
        }
        shoot() {
            sound.playLaser();
            if (this.weaponType === 'TRIPLE') {
                lasers.push(new Laser(this.x, this.y - 15, 0, -750, '#00f3ff'));
                lasers.push(new Laser(this.x - 12, this.y - 10, -180, -720, '#00f3ff'));
                lasers.push(new Laser(this.x + 12, this.y - 10, 180, -720, '#00f3ff'));
            } else if (this.weaponType === 'RAPID') {
                lasers.push(new Laser(this.x - 8, this.y - 15, 0, -850, '#ff007f'));
                lasers.push(new Laser(this.x + 8, this.y - 15, 0, -850, '#ff007f'));
            } else {
                lasers.push(new Laser(this.x - 10, this.y - 15, 0, -750, '#00f3ff'));
                lasers.push(new Laser(this.x + 10, this.y - 15, 0, -750, '#00f3ff'));
            }
        }
        takeDamage(amount) {
            if (this.invulnerableTimer > 0) return;
            screenShake = 12;
            if (this.shield > 0) {
                const absorbed = Math.min(this.shield, amount);
                this.shield -= absorbed;
                amount -= absorbed;
            }
            this.health -= amount;
            this.invulnerableTimer = 0.25;
            if (this.health <= 0) {
                this.health = 0;
                gameOver();
            }
        }
        draw() {
            ctx.save();
            ctx.translate(this.x, this.y);

            // Shield Glow
            if (this.shield > 10) {
                ctx.beginPath();
                ctx.arc(0, 0, 28, 0, Math.PI * 2);
                ctx.strokeStyle = `rgba(0, 243, 255, ${Math.min(0.6, this.shield / 100)})`;
                ctx.lineWidth = 2;
                ctx.shadowColor = '#00f3ff';
                ctx.shadowBlur = 12;
                ctx.stroke();
            }

            // Ship Hull (Sleek Sci-Fi Vector Fighter)
            ctx.beginPath();
            ctx.moveTo(0, -22); // Nose
            ctx.lineTo(18, 16);  // Right Wing
            ctx.lineTo(6, 12);   // Right Thruster
            ctx.lineTo(0, 18);   // Center Engine
            ctx.lineTo(-6, 12);  // Left Thruster
            ctx.lineTo(-18, 16); // Left Wing
            ctx.closePath();

            ctx.fillStyle = '#0f172a';
            ctx.fill();
            ctx.strokeStyle = '#00f3ff';
            ctx.lineWidth = 2;
            ctx.shadowColor = '#00f3ff';
            ctx.shadowBlur = 10;
            ctx.stroke();

            // Cockpit Canopy
            ctx.beginPath();
            ctx.moveTo(0, -12);
            ctx.lineTo(4, 2);
            ctx.lineTo(0, 5);
            ctx.lineTo(-4, 2);
            ctx.closePath();
            ctx.fillStyle = '#ff007f';
            ctx.shadowColor = '#ff007f';
            ctx.shadowBlur = 6;
            ctx.fill();

            ctx.restore();
        }
    }

    // --- Laser Projectile ---
    class Laser {
        constructor(x, y, vx, vy, color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color || '#00f3ff';
            this.alive = true;
        }
        update(dt) {
            this.x += this.vx * dt;
            this.y += this.vy * dt;
            if (this.y < -30 || this.x < -30 || this.x > width + 30) {
                this.alive = false;
            }
        }
        draw() {
            ctx.save();
            ctx.strokeStyle = this.color;
            ctx.lineWidth = 3;
            ctx.shadowColor = this.color;
            ctx.shadowBlur = 8;
            ctx.beginPath();
            ctx.moveTo(this.x, this.y);
            ctx.lineTo(this.x + this.vx * 0.02, this.y + this.vy * 0.02);
            ctx.stroke();
            ctx.restore();
        }
    }

    // --- Enemy & Asteroids ---
    class Enemy {
        constructor(type) {
            this.type = type || (Math.random() < 0.65 ? 'ASTEROID' : 'DRONE');
            this.x = Math.random() * (width - 60) + 30;
            this.y = -40;
            this.alive = true;
            if (this.type === 'ASTEROID') {
                this.radius = Math.random() * 16 + 14;
                this.speed = Math.random() * 120 + 80 + wave * 10;
                this.health = Math.ceil(this.radius / 10);
                this.points = 100;
                this.angle = 0;
                this.rotSpeed = (Math.random() - 0.5) * 3;
                this.vertices = [];
                const numPts = 7;
                for (let i = 0; i < numPts; i++) {
                    const a = (i / numPts) * Math.PI * 2;
                    const r = this.radius * (0.75 + Math.random() * 0.5);
                    this.vertices.push({ x: Math.cos(a) * r, y: Math.sin(a) * r });
                }
            } else {
                this.radius = 18;
                this.speed = Math.random() * 140 + 100 + wave * 15;
                this.health = 3;
                this.points = 250;
                this.baseX = this.x;
                this.swayFreq = Math.random() * 2 + 1;
                this.time = 0;
            }
        }
        update(dt) {
            this.y += this.speed * dt;
            if (this.type === 'ASTEROID') {
                this.angle += this.rotSpeed * dt;
            } else {
                this.time += dt;
                this.x = this.baseX + Math.sin(this.time * this.swayFreq) * 60;
            }
            if (this.y > height + 50) this.alive = false;
        }
        draw() {
            ctx.save();
            ctx.translate(this.x, this.y);
            if (this.type === 'ASTEROID') {
                ctx.rotate(this.angle);
                ctx.beginPath();
                this.vertices.forEach((v, i) => {
                    if (i === 0) ctx.moveTo(v.x, v.y);
                    else ctx.lineTo(v.x, v.y);
                });
                ctx.closePath();
                ctx.fillStyle = '#1e293b';
                ctx.fill();
                ctx.strokeStyle = '#94a3b8';
                ctx.lineWidth = 2;
                ctx.stroke();
            } else {
                // Drone
                ctx.beginPath();
                ctx.moveTo(0, 18);
                ctx.lineTo(14, -14);
                ctx.lineTo(0, -6);
                ctx.lineTo(-14, -14);
                ctx.closePath();
                ctx.fillStyle = '#450a0a';
                ctx.fill();
                ctx.strokeStyle = '#ef4444';
                ctx.lineWidth = 2;
                ctx.shadowColor = '#ef4444';
                ctx.shadowBlur = 8;
                ctx.stroke();
            }
            ctx.restore();
        }
    }

    // --- Boss Class ---
    class Boss {
        constructor() {
            this.x = width / 2;
            this.y = -80;
            this.targetY = 110;
            this.radius = 48;
            this.health = 35 + wave * 15;
            this.maxHealth = this.health;
            this.alive = true;
            this.speedX = 140;
            this.dir = 1;
        }
        update(dt) {
            if (this.y < this.targetY) {
                this.y += 60 * dt;
            } else {
                this.x += this.speedX * this.dir * dt;
                if (this.x > width - 80) this.dir = -1;
                if (this.x < 80) this.dir = 1;
            }
        }
        draw() {
            ctx.save();
            ctx.translate(this.x, this.y);
            ctx.beginPath();
            ctx.moveTo(0, 36);
            ctx.lineTo(44, -20);
            ctx.lineTo(24, -36);
            ctx.lineTo(-24, -36);
            ctx.lineTo(-44, -20);
            ctx.closePath();
            ctx.fillStyle = '#2b0a3d';
            ctx.fill();
            ctx.strokeStyle = '#ff007f';
            ctx.lineWidth = 3;
            ctx.shadowColor = '#ff007f';
            ctx.shadowBlur = 16;
            ctx.stroke();

            // Core Eye
            ctx.beginPath();
            ctx.arc(0, 0, 12, 0, Math.PI * 2);
            ctx.fillStyle = '#00f3ff';
            ctx.shadowColor = '#00f3ff';
            ctx.shadowBlur = 12;
            ctx.fill();
            ctx.restore();
        }
    }

    // --- Powerup Class ---
    class Powerup {
        constructor(x, y) {
            this.x = x;
            this.y = y;
            const kinds = ['TRIPLE', 'RAPID', 'SHIELD'];
            this.kind = kinds[Math.floor(Math.random() * kinds.length)];
            this.alive = true;
            this.speed = 100;
            this.radius = 14;
            this.time = 0;
        }
        update(dt) {
            this.y += this.speed * dt;
            this.time += dt;
            if (this.y > height + 30) this.alive = false;
        }
        draw() {
            ctx.save();
            ctx.translate(this.x, this.y);
            ctx.beginPath();
            ctx.arc(0, 0, this.radius + Math.sin(this.time * 6) * 2, 0, Math.PI * 2);
            ctx.fillStyle = this.kind === 'SHIELD' ? '#00f3ff' : (this.kind === 'TRIPLE' ? '#ffaa00' : '#ff007f');
            ctx.shadowColor = ctx.fillStyle;
            ctx.shadowBlur = 10;
            ctx.fill();
            ctx.fillStyle = '#000';
            ctx.font = 'bold 9px Orbitron';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(this.kind[0], 0, 1);
            ctx.restore();
        }
    }

    // --- Particle System ---
    class Particle {
        constructor(x, y, vx, vy, color, life, size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.life = life || 0.5;
            this.maxLife = this.life;
            this.size = size || 3;
        }
        update(dt) {
            this.x += this.vx * dt;
            this.y += this.vy * dt;
            this.life -= dt;
        }
        draw() {
            if (this.life <= 0) return;
            ctx.save();
            ctx.globalAlpha = Math.max(0, this.life / this.maxLife);
            ctx.fillStyle = this.color;
            ctx.beginPath();
            ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
            ctx.fill();
            ctx.restore();
        }
    }

    function createExplosion(x, y, color, count) {
        sound.playExplosion();
        const n = count || 16;
        for (let i = 0; i < n; i++) {
            const angle = Math.random() * Math.PI * 2;
            const spd = Math.random() * 200 + 40;
            particles.push(new Particle(x, y, Math.cos(angle) * spd, Math.sin(angle) * spd, color || '#ffaa00', Math.random() * 0.4 + 0.3, Math.random() * 3 + 2));
        }
    }

    // --- Floating Text ---
    const floatingTexts = [];
    function addFloatingText(text, x, y, color) {
        floatingTexts.push({
            text: text,
            x: x,
            y: y,
            color: color || '#00ff88',
            life: 0.8,
            maxLife: 0.8
        });
    }

    // --- Collections ---
    let player = null;
    const lasers = [];
    const enemies = [];
    const powerups = [];
    const particles = [];

    function startGame() {
        sound.init();
        score = 0;
        kills = 0;
        wave = 1;
        spawnTimer = 0;
        bossActive = false;
        boss = null;
        lasers.length = 0;
        enemies.length = 0;
        powerups.length = 0;
        particles.length = 0;
        floatingTexts.length = 0;
        player = new Player();
        isRunning = true;
        isPaused = false;
        overlay.classList.add('hidden');
        bossBarContainer.classList.add('hidden');
        updateHUD();
    }

    function restartGame() {
        startGame();
    }

    function gameOver() {
        isRunning = false;
        sound.playGameOver();
        if (score > highScore) {
            highScore = score;
            localStorage.setItem('bridgemind_high_score', highScore.toString());
            highscoreDisplay.textContent = highScore;
        }
        overlayTitle.textContent = 'VESSEL COMPROMISED';
        overlaySubtitle.textContent = 'SECTOR DEFENSE TERMINATED';
        finalScore.textContent = score;
        finalWave.textContent = wave;
        finalKills.textContent = kills;
        statsSummary.classList.remove('hidden');
        startBtn.querySelector('.btn-text').textContent = 'RE-ENGAGE SECTOR';
        startBtn.classList.remove('hidden');
        overlay.classList.remove('hidden');
    }

    function updateHUD() {
        if (!player) return;
        scoreDisplay.textContent = score;
        waveDisplay.textContent = wave;
        healthBar.style.width = Math.max(0, (player.health / player.maxHealth) * 100) + '%';
        shieldBar.style.width = Math.max(0, (player.shield / player.maxShield) * 100) + '%';
        if (boss && bossActive) {
            bossHealthBar.style.width = Math.max(0, (boss.health / boss.maxHealth) * 100) + '%';
        }
    }

    // --- Main Game Loop ---
    function gameLoop(now) {
        const dt = Math.min((now - lastTime) / 1000, 0.1);
        lastTime = now;

        if (isRunning && !isPaused) {
            // Update Player
            if (player) player.update(dt);

            // Spawn Logic
            spawnTimer += dt;
            const spawnInterval = Math.max(0.45, 1.3 - wave * 0.1);
            if (spawnTimer >= spawnInterval && !bossActive) {
                spawnTimer = 0;
                enemies.push(new Enemy());
            }

            // Trigger Boss Fight every 1500 points
            if (!bossActive && score > 0 && score >= wave * 1500) {
                bossActive = true;
                boss = new Boss();
                bossBarContainer.classList.remove('hidden');
                addFloatingText('⚠️ WARNING: BOSS VESSEL DETECTED!', width / 2, height / 3, '#ff007f');
            }

            // Update Lasers
            lasers.forEach(l => l.update(dt));
            for (let i = lasers.length - 1; i >= 0; i--) {
                if (!lasers[i].alive) lasers.splice(i, 1);
            }

            // Update Enemies
            enemies.forEach(e => e.update(dt));
            for (let i = enemies.length - 1; i >= 0; i--) {
                if (!enemies[i].alive) enemies.splice(i, 1);
            }

            // Update Boss
            if (boss && bossActive) {
                boss.update(dt);
                // Collision player laser with boss
                lasers.forEach(laser => {
                    if (!laser.alive) return;
                    const dist = Math.hypot(laser.x - boss.x, laser.y - boss.y);
                    if (dist < boss.radius) {
                        laser.alive = false;
                        boss.health -= 1;
                        createExplosion(laser.x, laser.y, '#ff007f', 4);
                        if (boss.health <= 0) {
                            boss.alive = false;
                            bossActive = false;
                            bossBarContainer.classList.add('hidden');
                            createExplosion(boss.x, boss.y, '#ff007f', 40);
                            score += 1000;
                            kills += 1;
                            wave += 1;
                            addFloatingText('💥 BOSS DESTROYED! +1000', boss.x, boss.y, '#00ff88');
                            powerups.push(new Powerup(boss.x, boss.y));
                        }
                    }
                });
            }

            // Update Powerups
            powerups.forEach(p => p.update(dt));
            for (let i = powerups.length - 1; i >= 0; i--) {
                if (!powerups[i].alive) powerups.splice(i, 1);
            }

            // Collisions: Lasers vs Enemies
            lasers.forEach(laser => {
                if (!laser.alive) return;
                enemies.forEach(enemy => {
                    if (!enemy.alive) return;
                    const dist = Math.hypot(laser.x - enemy.x, laser.y - enemy.y);
                    if (dist < enemy.radius + 6) {
                        laser.alive = false;
                        enemy.health -= 1;
                        createExplosion(laser.x, laser.y, '#00f3ff', 3);
                        if (enemy.health <= 0) {
                            enemy.alive = false;
                            createExplosion(enemy.x, enemy.y, enemy.type === 'DRONE' ? '#ef4444' : '#94a3b8', 14);
                            score += enemy.points;
                            kills += 1;
                            addFloatingText(`+${enemy.points}`, enemy.x, enemy.y, '#00f3ff');
                            // Chance to drop powerup
                            if (Math.random() < 0.15) {
                                powerups.push(new Powerup(enemy.x, enemy.y));
                            }
                        }
                    }
                });
            });

            // Collisions: Player vs Enemies
            if (player) {
                enemies.forEach(enemy => {
                    if (!enemy.alive) return;
                    const dist = Math.hypot(player.x - enemy.x, player.y - enemy.y);
                    if (dist < enemy.radius + 18) {
                        enemy.alive = false;
                        createExplosion(enemy.x, enemy.y, '#ef4444', 16);
                        player.takeDamage(25);
                    }
                });

                // Collisions: Player vs Powerups
                powerups.forEach(p => {
                    if (!p.alive) return;
                    const dist = Math.hypot(player.x - p.x, player.y - p.y);
                    if (dist < p.radius + 20) {
                        p.alive = false;
                        sound.playPowerup();
                        if (p.kind === 'SHIELD') {
                            player.shield = 100;
                            player.health = Math.min(100, player.health + 20);
                            addFloatingText('🛡️ SHIELD MAX', player.x, player.y - 20, '#00f3ff');
                        } else if (p.kind === 'TRIPLE') {
                            player.weaponType = 'TRIPLE';
                            player.powerupTimer = 10;
                            addFloatingText('⚡ TRIPLE CANNON', player.x, player.y - 20, '#ffaa00');
                        } else if (p.kind === 'RAPID') {
                            player.weaponType = 'RAPID';
                            player.fireRate = 0.08;
                            player.powerupTimer = 10;
                            addFloatingText('🔥 OVERCHARGED PULSE', player.x, player.y - 20, '#ff007f');
                        }
                    }
                });
            }

            updateHUD();
        }

        // Update Particles
        particles.forEach(p => p.update(dt));
        for (let i = particles.length - 1; i >= 0; i--) {
            if (particles[i].life <= 0) particles.splice(i, 1);
        }

        // Update Floating Texts
        floatingTexts.forEach(t => {
            t.y -= 30 * dt;
            t.life -= dt;
        });
        for (let i = floatingTexts.length - 1; i >= 0; i--) {
            if (floatingTexts[i].life <= 0) floatingTexts.splice(i, 1);
        }

        // --- Render Frame ---
        ctx.save();
        if (screenShake > 0) {
            ctx.translate((Math.random() - 0.5) * screenShake, (Math.random() - 0.5) * screenShake);
            screenShake = Math.max(0, screenShake - dt * 30);
        }

        ctx.fillStyle = '#070913';
        ctx.fillRect(0, 0, width, height);

        // Render Starfield
        ctx.fillStyle = '#ffffff';
        stars.forEach(s => {
            s.y += s.speed * (isRunning ? 1.5 : 0.4);
            if (s.y > height) {
                s.y = 0;
                s.x = Math.random() * width;
            }
            ctx.globalAlpha = s.brightness;
            ctx.beginPath();
            ctx.arc(s.x, s.y, s.size, 0, Math.PI * 2);
            ctx.fill();
        });
        ctx.globalAlpha = 1;

        // Render Game Entities
        powerups.forEach(p => p.draw());
        lasers.forEach(l => l.draw());
        enemies.forEach(e => e.draw());
        if (boss && bossActive) boss.draw();
        if (player && isRunning) player.draw();
        particles.forEach(p => p.draw());

        // Render Floating Texts
        ctx.font = 'bold 14px Orbitron';
        ctx.textAlign = 'center';
        floatingTexts.forEach(t => {
            ctx.fillStyle = t.color;
            ctx.globalAlpha = Math.max(0, t.life / t.maxLife);
            ctx.shadowColor = t.color;
            ctx.shadowBlur = 8;
            ctx.fillText(t.text, t.x, t.y);
        });
        ctx.globalAlpha = 1;

        ctx.restore();
        requestAnimationFrame(gameLoop);
    }

    // --- Event Listeners ---
    startBtn.addEventListener('click', () => {
        startGame();
    });

    // Auto-initialize canvas & start animation loop
    resize();
    requestAnimationFrame(gameLoop);
})();
