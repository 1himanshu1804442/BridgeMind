import { useState, useEffect, useRef, useCallback } from 'react';
import { 
  Monitor, 
  Tablet, 
  Smartphone, 
  RotateCw, 
  ExternalLink, 
  Volume2, 
  VolumeX, 
  FolderTree, 
  FileCode, 
  Check, 
  Copy, 
  CheckCircle2, 
  RefreshCw
} from 'lucide-react';
import { useWorkspaceStore } from '../../../store/workspaceStore';

// Viewport sizes for the responsive tester
type ViewportMode = 'desktop' | 'tablet' | 'mobile';

interface FileEntry {
  name: string;
  language: string;
  size: string;
  content: string;
}

// Sample generated workspace project files
const GENERATED_WORKSPACE_FILES: Record<string, FileEntry> = {
  'index.html': {
    name: 'index.html',
    language: 'html',
    size: '1.4 KB',
    content: `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>BridgeMind Cyberpunk Space Arcade</title>
  <link rel="stylesheet" href="style.css" />
</head>
<body>
  <div id="game-container">
    <header class="hud">
      <div class="stat"><span class="label">SCORE:</span> <span id="score-val">00000</span></div>
      <div class="stat"><span class="label">SHIELD:</span> <div class="shield-bar"><div id="shield-fill"></div></div></div>
      <div class="stat"><span class="label">WAVE:</span> <span id="wave-val">01</span></div>
    </header>
    <canvas id="gameCanvas" width="800" height="600"></canvas>
    <div id="game-overlay" class="hidden">
      <h1 id="overlay-title">MISSION COMPLETE</h1>
      <p id="overlay-desc">Press [SPACE] to restart</p>
    </div>
  </div>
  <script type="module" src="game.js"></script>
</body>
</html>`,
  },
  'game.js': {
    name: 'game.js',
    language: 'javascript',
    size: '3.8 KB',
    content: `// BridgeMind Autonomous Space Arcade Game Engine
class SpaceGame {
  constructor(canvasId) {
    this.canvas = document.getElementById(canvasId);
    this.ctx = this.canvas.getContext('2d');
    this.player = { x: 400, y: 520, vx: 0, shield: 100, score: 0 };
    this.lasers = [];
    this.enemies = [];
    this.particles = [];
    this.keys = {};
    this.wave = 1;
    this.running = true;

    this.bindEvents();
    this.spawnEnemyWave();
    this.loop();
  }

  bindEvents() {
    window.addEventListener('keydown', (e) => { this.keys[e.code] = true; });
    window.addEventListener('keyup', (e) => { this.keys[e.code] = false; });
  }

  spawnEnemyWave() {
    for (let i = 0; i < 5 + this.wave * 2; i++) {
      this.enemies.push({
        x: 60 + i * 90,
        y: Math.random() * 150 + 40,
        vx: (Math.random() - 0.5) * 2,
        vy: 0.5 + Math.random() * 0.5,
        hp: 20
      });
    }
  }

  fireLaser() {
    this.lasers.push({ x: this.player.x - 8, y: this.player.y - 12, vy: -10 });
    this.lasers.push({ x: this.player.x + 8, y: this.player.y - 12, vy: -10 });
  }

  update() {
    // Player controls
    if (this.keys['ArrowLeft'] || this.keys['KeyA']) this.player.x = Math.max(30, this.player.x - 6);
    if (this.keys['ArrowRight'] || this.keys['KeyD']) this.player.x = Math.min(770, this.player.x + 6);
    if (this.keys['Space'] && !this.lastShot) {
      this.fireLaser();
      this.lastShot = true;
    } else if (!this.keys['Space']) {
      this.lastShot = false;
    }

    // Update lasers & check collisions
    this.lasers.forEach((l, li) => {
      l.y += l.vy;
      this.enemies.forEach((e, ei) => {
        if (Math.hypot(l.x - e.x, l.y - e.y) < 22) {
          e.hp -= 10;
          this.lasers.splice(li, 1);
          this.createExplosion(e.x, e.y, '#34d399');
          if (e.hp <= 0) {
            this.enemies.splice(ei, 1);
            this.player.score += 150;
          }
        }
      });
    });
  }

  createExplosion(x, y, color) {
    for (let i = 0; i < 12; i++) {
      this.particles.push({
        x, y,
        vx: (Math.random() - 0.5) * 6,
        vy: (Math.random() - 0.5) * 6,
        life: 1.0,
        color
      });
    }
  }

  draw() {
    this.ctx.fillStyle = '#09090b';
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
    // Render glowing player craft
    this.ctx.fillStyle = '#10b981';
    this.ctx.beginPath();
    this.ctx.moveTo(this.player.x, this.player.y - 16);
    this.ctx.lineTo(this.player.x - 14, this.player.y + 14);
    this.ctx.lineTo(this.player.x + 14, this.player.y + 14);
    this.ctx.closePath();
    this.ctx.fill();
  }

  loop() {
    if (this.running) {
      this.update();
      this.draw();
      requestAnimationFrame(() => this.loop());
    }
  }
}
window.game = new SpaceGame('gameCanvas');`,
  },
  'style.css': {
    name: 'style.css',
    language: 'css',
    size: '1.1 KB',
    content: `/* BridgeMind Synthwave Canvas Styling */
:root {
  --neon-emerald: #10b981;
  --neon-sky: #0ea5e9;
  --bg-dark: #09090b;
}

body {
  margin: 0;
  padding: 0;
  background-color: var(--bg-dark);
  font-family: 'JetBrains Mono', monospace;
  overflow: hidden;
  color: #fff;
}

#game-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: radial-gradient(circle at 50% 30%, #18181b 0%, #09090b 100%);
}

.hud {
  display: flex;
  gap: 2rem;
  padding: 0.75rem 1.5rem;
  background: rgba(24, 24, 27, 0.85);
  border: 1px solid rgba(16, 185, 129, 0.3);
  border-radius: 8px;
  box-shadow: 0 0 15px rgba(16, 185, 129, 0.2);
  margin-bottom: 0.5rem;
}

canvas#gameCanvas {
  border: 2px solid rgba(16, 185, 129, 0.4);
  border-radius: 8px;
  box-shadow: 0 0 25px rgba(16, 185, 129, 0.15);
  background: #050507;
}`,
  },
  'package.json': {
    name: 'package.json',
    language: 'json',
    size: '480 B',
    content: `{
  "name": "bridgemind-space-arcade",
  "version": "1.0.0",
  "description": "Autonomous AI-generated Arcade Simulation",
  "main": "game.js",
  "scripts": {
    "start": "vite",
    "build": "vite build",
    "test": "vitest run"
  },
  "dependencies": {
    "canvas-confetti": "^1.9.4"
  }
}`,
  },
  'README.md': {
    name: 'README.md',
    language: 'markdown',
    size: '1.2 KB',
    content: `# 🚀 BridgeMind Space Arcade ADE

Autonomous multi-agent generated arcade simulation built inside the **BridgeMind Studio ADE**.

## 🧠 Architectural Overview
- **Engine**: Pure HTML5 Canvas 2D Physics Pipeline
- **Audio Synthesizer**: Procedural Web Audio API soundwave generator
- **AI Hive Crew**:
  - 🟣 **Claude Code**: Space physics & projectile collision matrix
  - 🟢 **OpenAI Codex**: Wave state machine & enemy AI drone patterns
  - 🔵 **Antigravity AGY**: Synthwave UI styling & reactive canvas HUD
  - ⚡ **Aider**: Code integration & Git differential testing

## 🎮 Controls
- **Arrow Keys / WASD**: Maneuver space fighter
- **[SPACE]**: Fire dual plasma laser blasters
- **Click / Touch**: Target coordinates & fire`,
  },
};

// Git Diff sample entries
const GIT_DIFFS = [
  {
    file: 'game.js',
    additions: 42,
    deletions: 6,
    diff: `@@ -18,6 +18,12 @@ class SpaceGame {
     this.lasers = [];
     this.enemies = [];
     this.particles = [];
+    // Added Web Audio API sound synthesizers
+    this.soundFx = new SoundSynth();
+    this.comboMultiplier = 1;
+    this.highScore = 14500;
@@ -48,8 +54,14 @@ class SpaceGame {
-    this.lasers.push({ x: this.player.x, y: this.player.y });
+    // Dual plasma laser cannons with glowing particle trails
+    this.lasers.push({ x: this.player.x - 8, y: this.player.y - 12, vy: -12 });
+    this.lasers.push({ x: this.player.x + 8, y: this.player.y - 12, vy: -12 });
+    this.soundFx.playLaserPew();
+    this.createLaserFlash(this.player.x, this.player.y);`
  },
  {
    file: 'style.css',
    additions: 18,
    deletions: 2,
    diff: `@@ -12,4 +12,12 @@
 .hud {
-  border: 1px solid #333;
+  border: 1px solid rgba(16, 185, 129, 0.4);
+  box-shadow: 0 0 20px rgba(16, 185, 129, 0.25);
+  backdrop-filter: blur(8px);
+}`
  }
];

export function LivePreview() {
  const activeWorkspaceId = useWorkspaceStore((state) => state.activeWorkspaceId);
  const [activeTab, setActiveTab] = useState<'preview' | 'files' | 'git'>('preview');
  const [viewport, setViewport] = useState<ViewportMode>('desktop');
  const [selectedFile, setSelectedFile] = useState<string>('game.js');
  const [isCopied, setIsCopied] = useState(false);
  const [selectedDiffFile, setSelectedDiffFile] = useState(0);
  const [isMerged, setIsMerged] = useState(false);
  const [iframeKey, setIframeKey] = useState(0);
  const [useFallbackCanvas, setUseFallbackCanvas] = useState(true);

  // Retro Canvas Game Engine State
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [gameScore, setGameScore] = useState(0);
  const [gameShield, setGameShield] = useState(100);
  const [gameWave, setGameWave] = useState(1);
  const [isMuted, setIsMuted] = useState(true);
  const [isGameOver, setIsGameOver] = useState(false);

  // Audio Context synthesizer for retro sound effects
  const audioCtxRef = useRef<AudioContext | null>(null);

  const playSynthSound = useCallback((type: 'laser' | 'explosion') => {
    if (isMuted) return;
    try {
      if (!audioCtxRef.current) {
        const AudioContextClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
        audioCtxRef.current = new AudioContextClass();
      }
      const ctx = audioCtxRef.current;
      if (ctx.state === 'suspended') {
        ctx.resume();
      }

      if (type === 'laser') {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(880, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(110, ctx.currentTime + 0.12);
        gain.gain.setValueAtTime(0.15, ctx.currentTime);
        gain.gain.linearRampToValueAtTime(0.01, ctx.currentTime + 0.12);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start();
        osc.stop(ctx.currentTime + 0.12);
      } else if (type === 'explosion') {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'square';
        osc.frequency.setValueAtTime(150, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(30, ctx.currentTime + 0.25);
        gain.gain.setValueAtTime(0.25, ctx.currentTime);
        gain.gain.linearRampToValueAtTime(0.01, ctx.currentTime + 0.25);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start();
        osc.stop(ctx.currentTime + 0.25);
      }
    } catch {
      // Audio playback fails gracefully if blocked by browser policy
    }
  }, [isMuted]);

  // Playable HTML5 Canvas Game Engine
  useEffect(() => {
    if (activeTab !== 'preview' || !useFallbackCanvas) return;

    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animationFrameId: number;
    let playerX = canvas.width / 2;
    const playerY = canvas.height - 45;
    const keys: Record<string, boolean> = {};
    const lasers: Array<{ x: number; y: number; vy: number }> = [];
    let enemies: Array<{ x: number; y: number; vx: number; vy: number; hp: number; maxHp: number; color: string }> = [];
    const particles: Array<{ x: number; y: number; vx: number; vy: number; life: number; color: string }> = [];
    const stars: Array<{ x: number; y: number; size: number; speed: number; opacity: number }> = [];
    let lastShotTime = 0;
    let score = 0;
    let shield = 100;
    let wave = 1;

    // Generate starry space background
    for (let i = 0; i < 60; i++) {
      stars.push({
        x: Math.random() * canvas.width,
        y: Math.random() * canvas.height,
        size: Math.random() * 2 + 0.5,
        speed: Math.random() * 1.5 + 0.5,
        opacity: Math.random() * 0.8 + 0.2,
      });
    }

    const spawnEnemies = (currentWave: number) => {
      enemies = [];
      const count = 4 + currentWave * 2;
      for (let i = 0; i < count; i++) {
        const col = i % 6;
        const row = Math.floor(i / 6);
        enemies.push({
          x: 50 + col * ((canvas.width - 100) / 5),
          y: 40 + row * 45,
          vx: (Math.random() - 0.5) * 1.5,
          vy: 0.15 + currentWave * 0.05,
          hp: 20 + currentWave * 5,
          maxHp: 20 + currentWave * 5,
          color: currentWave % 2 === 0 ? '#38bdf8' : '#ec4899',
        });
      }
    };

    spawnEnemies(wave);

    const handleKeyDown = (e: KeyboardEvent) => {
      keys[e.code] = true;
      if (e.code === 'Space') {
        e.preventDefault();
      }
    };

    const handleKeyUp = (e: KeyboardEvent) => {
      keys[e.code] = false;
    };

    // Canvas click to fire
    const handleCanvasClick = (e: MouseEvent) => {
      const rect = canvas.getBoundingClientRect();
      const clickX = e.clientX - rect.left;
      playerX = Math.max(25, Math.min(canvas.width - 25, clickX));
      lasers.push({ x: playerX - 10, y: playerY - 10, vy: -9 });
      lasers.push({ x: playerX + 10, y: playerY - 10, vy: -9 });
      playSynthSound('laser');
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    canvas.addEventListener('mousedown', handleCanvasClick);

    const createExplosion = (x: number, y: number, color: string) => {
      for (let i = 0; i < 14; i++) {
        const angle = Math.random() * Math.PI * 2;
        const speed = Math.random() * 4 + 1;
        particles.push({
          x,
          y,
          vx: Math.cos(angle) * speed,
          vy: Math.sin(angle) * speed,
          life: 1.0,
          color,
        });
      }
    };

    const renderLoop = (timestamp: number) => {
      // Clear Canvas
      ctx.fillStyle = '#09090b';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      // Render & scroll stars
      stars.forEach((star) => {
        star.y += star.speed;
        if (star.y > canvas.height) {
          star.y = 0;
          star.x = Math.random() * canvas.width;
        }
        ctx.fillStyle = `rgba(255, 255, 255, ${star.opacity})`;
        ctx.fillRect(star.x, star.y, star.size, star.size);
      });

      if (shield > 0) {
        // Player movement
        if (keys['ArrowLeft'] || keys['KeyA']) {
          playerX = Math.max(25, playerX - 5);
        }
        if (keys['ArrowRight'] || keys['KeyD']) {
          playerX = Math.min(canvas.width - 25, playerX + 5);
        }

        // Firing lasers
        if (keys['Space'] && timestamp - lastShotTime > 160) {
          lasers.push({ x: playerX - 10, y: playerY - 10, vy: -9 });
          lasers.push({ x: playerX + 10, y: playerY - 10, vy: -9 });
          playSynthSound('laser');
          lastShotTime = timestamp;
        }

        // Update lasers
        for (let i = lasers.length - 1; i >= 0; i--) {
          const l = lasers[i];
          l.y += l.vy;
          if (l.y < -10) {
            lasers.splice(i, 1);
            continue;
          }

          // Collision detection with enemies
          for (let j = enemies.length - 1; j >= 0; j--) {
            const e = enemies[j];
            if (Math.hypot(l.x - e.x, l.y - e.y) < 20) {
              e.hp -= 10;
              lasers.splice(i, 1);
              createExplosion(e.x, e.y, '#34d399');
              playSynthSound('explosion');
              score += 100;
              setGameScore(score);

              if (e.hp <= 0) {
                createExplosion(e.x, e.y, e.color);
                enemies.splice(j, 1);
                score += 250;
                setGameScore(score);
              }
              break;
            }
          }
        }

        // Update enemies
        enemies.forEach((e) => {
          e.x += e.vx;
          e.y += e.vy;
          if (e.x < 30 || e.x > canvas.width - 30) {
            e.vx = -e.vx;
          }
          if (e.y > canvas.height - 70) {
            shield = Math.max(0, shield - 20);
            setGameShield(shield);
            e.y = 40;
            createExplosion(playerX, playerY, '#ef4444');
            playSynthSound('explosion');
            if (shield <= 0) {
              setIsGameOver(true);
            }
          }
        });

        // Wave completion check
        if (enemies.length === 0) {
          wave += 1;
          setGameWave(wave);
          shield = Math.min(100, shield + 25);
          setGameShield(shield);
          spawnEnemies(wave);
        }
      }

      // Render Lasers
      lasers.forEach((l) => {
        ctx.fillStyle = '#34d399';
        ctx.shadowColor = '#10b981';
        ctx.shadowBlur = 10;
        ctx.fillRect(l.x - 2, l.y, 4, 12);
      });
      ctx.shadowBlur = 0;

      // Render Enemies
      enemies.forEach((e) => {
        ctx.fillStyle = e.color;
        ctx.shadowColor = e.color;
        ctx.shadowBlur = 8;
        ctx.beginPath();
        ctx.moveTo(e.x, e.y + 12);
        ctx.lineTo(e.x - 14, e.y - 10);
        ctx.lineTo(e.x + 14, e.y - 10);
        ctx.closePath();
        ctx.fill();

        // Enemy Health mini bar
        const hpPercent = e.hp / e.maxHp;
        ctx.fillStyle = '#27272a';
        ctx.fillRect(e.x - 12, e.y - 18, 24, 3);
        ctx.fillStyle = '#10b981';
        ctx.fillRect(e.x - 12, e.y - 18, 24 * hpPercent, 3);
      });
      ctx.shadowBlur = 0;

      // Render Particles
      for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i];
        p.x += p.vx;
        p.y += p.vy;
        p.life -= 0.03;
        if (p.life <= 0) {
          particles.splice(i, 1);
          continue;
        }
        ctx.fillStyle = p.color;
        ctx.globalAlpha = p.life;
        ctx.fillRect(p.x, p.y, 3, 3);
      }
      ctx.globalAlpha = 1.0;

      // Render Player Ship
      ctx.fillStyle = shield > 20 ? '#10b981' : '#f43f5e';
      ctx.shadowColor = '#10b981';
      ctx.shadowBlur = 12;
      ctx.beginPath();
      ctx.moveTo(playerX, playerY - 14);
      ctx.lineTo(playerX - 16, playerY + 12);
      ctx.lineTo(playerX + 16, playerY + 12);
      ctx.closePath();
      ctx.fill();

      // Ship thruster glow
      ctx.fillStyle = '#38bdf8';
      ctx.beginPath();
      ctx.moveTo(playerX - 6, playerY + 13);
      ctx.lineTo(playerX + 6, playerY + 13);
      ctx.lineTo(playerX, playerY + 19 + Math.random() * 5);
      ctx.closePath();
      ctx.fill();
      ctx.shadowBlur = 0;

      animationFrameId = requestAnimationFrame(renderLoop);
    };

    animationFrameId = requestAnimationFrame(renderLoop);

    return () => {
      cancelAnimationFrame(animationFrameId);
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
      canvas.removeEventListener('mousedown', handleCanvasClick);
    };
  }, [activeTab, useFallbackCanvas, playSynthSound]);

  const handleCopyCode = () => {
    const file = GENERATED_WORKSPACE_FILES[selectedFile];
    if (file) {
      navigator.clipboard.writeText(file.content);
      setIsCopied(true);
      setTimeout(() => setIsCopied(false), 2000);
    }
  };

  const handleReload = () => {
    setIframeKey((prev) => prev + 1);
    setGameScore(0);
    setGameShield(100);
    setGameWave(1);
    setIsGameOver(false);
  };

  const handleOpenExternal = () => {
    const previewUrl = `/api/workspaces/${activeWorkspaceId || 'demo'}/preview/index.html`;
    window.open(previewUrl, '_blank');
  };

  const getViewportWidthClass = () => {
    switch (viewport) {
      case 'mobile':
        return 'max-w-[375px]';
      case 'tablet':
        return 'max-w-[768px]';
      case 'desktop':
      default:
        return 'w-full';
    }
  };

  return (
    <aside 
      data-testid="live-preview-panel"
      aria-label="Live Game / App Preview Panel"
      className="h-full flex flex-col bg-zinc-950 border-l border-zinc-800/80 overflow-hidden select-none"
    >
      {/* 1. Top Bar: Viewport Switcher & Controls */}
      <div className="h-12 border-b border-zinc-800/80 bg-zinc-900/60 px-3 flex items-center justify-between shrink-0 gap-2">
        <div className="flex items-center gap-2 min-w-0">
          <div className="flex items-center gap-1.5 text-xs font-mono text-zinc-200 font-semibold tracking-wide truncate">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
            <span className="truncate">BridgeSpace ADE Preview</span>
          </div>
          <span className="hidden sm:inline-block px-1.5 py-0.5 bg-emerald-950/80 border border-emerald-800/50 text-[10px] font-mono text-emerald-400 rounded">
            PORT :3000
          </span>
        </div>

        {/* Viewport switcher: Desktop / Tablet / Mobile */}
        <div className="flex items-center bg-zinc-900 border border-zinc-800 rounded-lg p-0.5 text-xs">
          <button
            onClick={() => setViewport('desktop')}
            title="Desktop View (100%)"
            className={`p-1.5 rounded transition-colors ${
              viewport === 'desktop' ? 'bg-zinc-800 text-emerald-400 font-bold' : 'text-zinc-500 hover:text-zinc-300'
            }`}
          >
            <Monitor className="w-3.5 h-3.5" />
          </button>
          <button
            onClick={() => setViewport('tablet')}
            title="Tablet View (768px)"
            className={`p-1.5 rounded transition-colors ${
              viewport === 'tablet' ? 'bg-zinc-800 text-emerald-400 font-bold' : 'text-zinc-500 hover:text-zinc-300'
            }`}
          >
            <Tablet className="w-3.5 h-3.5" />
          </button>
          <button
            onClick={() => setViewport('mobile')}
            title="Mobile View (375px)"
            className={`p-1.5 rounded transition-colors ${
              viewport === 'mobile' ? 'bg-zinc-800 text-emerald-400 font-bold' : 'text-zinc-500 hover:text-zinc-300'
            }`}
          >
            <Smartphone className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Action icons: Reload & External */}
        <div className="flex items-center gap-1">
          <button
            onClick={handleReload}
            title="Reload Preview"
            className="p-1.5 text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800 rounded transition-colors"
          >
            <RotateCw className="w-3.5 h-3.5" />
          </button>
          <button
            onClick={handleOpenExternal}
            title="Open in New Tab"
            className="p-1.5 text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800 rounded transition-colors"
          >
            <ExternalLink className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

      {/* 2. Main Content Area */}
      <div className="flex-1 flex flex-col items-center justify-center p-3 bg-zinc-950 overflow-hidden relative">
        {/* [🎮 Live App / Game] Tab Content */}
        {activeTab === 'preview' && (
          <div className={`w-full h-full flex flex-col items-center justify-center transition-all duration-300 ${getViewportWidthClass()}`}>
            <div className="w-full h-full border border-zinc-800 rounded-lg overflow-hidden bg-black flex flex-col shadow-2xl relative">
              {/* Game / App Header HUD */}
              <div className="h-9 bg-zinc-900/80 border-b border-zinc-800/80 px-3 flex items-center justify-between text-[11px] font-mono shrink-0">
                <div className="flex items-center gap-3">
                  <div className="flex items-center gap-1.5">
                    <span className="text-zinc-500">SCORE:</span>
                    <span className="text-emerald-400 font-bold tracking-wider">{gameScore.toString().padStart(5, '0')}</span>
                  </div>
                  <div className="hidden sm:flex items-center gap-1.5">
                    <span className="text-zinc-500">WAVE:</span>
                    <span className="text-sky-400 font-bold">{gameWave.toString().padStart(2, '0')}</span>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  {/* Shield Health Bar */}
                  <div className="flex items-center gap-1.5">
                    <span className="text-zinc-500 text-[10px]">SHIELD:</span>
                    <div className="w-16 h-2 bg-zinc-800 rounded-full overflow-hidden border border-zinc-700">
                      <div 
                        className={`h-full transition-all duration-300 ${
                          gameShield > 40 ? 'bg-emerald-400' : 'bg-rose-500'
                        }`}
                        style={{ width: `${gameShield}%` }}
                      />
                    </div>
                  </div>

                  {/* Sound & Mode Controls */}
                  <button
                    onClick={() => setIsMuted(!isMuted)}
                    className="text-zinc-400 hover:text-zinc-200 transition-colors p-1"
                    title={isMuted ? 'Unmute Sound' : 'Mute Sound'}
                  >
                    {isMuted ? <VolumeX className="w-3.5 h-3.5" /> : <Volume2 className="w-3.5 h-3.5 text-emerald-400" />}
                  </button>

                  <button
                    onClick={() => setUseFallbackCanvas(!useFallbackCanvas)}
                    className="text-[10px] px-1.5 py-0.5 rounded bg-zinc-800 hover:bg-zinc-700 text-zinc-300 border border-zinc-700 transition-colors"
                  >
                    {useFallbackCanvas ? 'Canvas Game' : 'Live IFrame'}
                  </button>
                </div>
              </div>

              {/* Viewport Body */}
              <div className="flex-1 w-full h-full relative bg-zinc-950 flex items-center justify-center overflow-hidden">
                {useFallbackCanvas ? (
                  <>
                    <canvas
                      ref={canvasRef}
                      width={640}
                      height={420}
                      className="w-full h-full object-contain cursor-crosshair"
                    />
                    
                    {/* Game Over / Pause Overlay */}
                    {isGameOver && (
                      <div className="absolute inset-0 bg-black/80 backdrop-blur-sm flex flex-col items-center justify-center p-4 text-center z-10">
                        <div className="w-12 h-12 rounded-full bg-rose-950/80 border border-rose-500 flex items-center justify-center mb-3 text-rose-400">
                          💥
                        </div>
                        <h3 className="text-rose-400 font-mono font-bold text-lg mb-1">SHIELD BREACHED</h3>
                        <p className="text-zinc-400 text-xs font-mono mb-4">Final Score: {gameScore}</p>
                        <button
                          onClick={handleReload}
                          className="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded font-mono text-xs font-semibold flex items-center gap-1.5 shadow-lg transition-colors"
                        >
                          <RefreshCw className="w-3.5 h-3.5" />
                          <span>Reboot Simulator</span>
                        </button>
                      </div>
                    )}
                  </>
                ) : (
                  <iframe
                    key={iframeKey}
                    title="BridgeSpace Live Preview"
                    src={`/api/workspaces/${activeWorkspaceId || 'demo'}/preview/index.html`}
                    className="w-full h-full border-none bg-zinc-950"
                  />
                )}
              </div>

              {/* Bottom Instructions Bar */}
              <div className="h-6 bg-zinc-950 border-t border-zinc-900 px-3 flex items-center justify-between text-[10px] font-mono text-zinc-500">
                <span>[A / D / ◄ / ►] Move &bull; [SPACE / Click] Fire Lasers</span>
                <span className="text-emerald-400 font-semibold">60 FPS Hardware Accelerated</span>
              </div>
            </div>
          </div>
        )}

        {/* [📁 Workspace Files] Tab Content */}
        {activeTab === 'files' && (
          <div className="w-full h-full flex border border-zinc-800 rounded-lg overflow-hidden bg-zinc-950 shadow-2xl">
            {/* File Tree Sidebar */}
            <div className="w-44 border-r border-zinc-800/80 bg-zinc-900/50 flex flex-col">
              <div className="p-2.5 border-b border-zinc-800 text-[11px] font-mono text-zinc-400 font-semibold flex items-center gap-1.5 uppercase tracking-wider">
                <FolderTree className="w-3.5 h-3.5 text-emerald-400" />
                <span>Generated Files</span>
              </div>
              <div className="flex-1 overflow-y-auto p-1.5 space-y-0.5">
                {Object.keys(GENERATED_WORKSPACE_FILES).map((filename) => {
                  const isSelected = selectedFile === filename;
                  return (
                    <button
                      key={filename}
                      onClick={() => setSelectedFile(filename)}
                      className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded text-xs font-mono transition-colors text-left ${
                        isSelected
                          ? 'bg-zinc-800 text-emerald-400 font-semibold'
                          : 'text-zinc-400 hover:bg-zinc-900 hover:text-zinc-200'
                      }`}
                    >
                      <span className="flex items-center gap-1.5 truncate">
                        <FileCode className={`w-3.5 h-3.5 ${isSelected ? 'text-emerald-400' : 'text-zinc-500'}`} />
                        <span className="truncate">{filename}</span>
                      </span>
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Code Viewer */}
            <div className="flex-1 flex flex-col bg-zinc-950 overflow-hidden">
              <div className="h-8 border-b border-zinc-800/80 bg-zinc-900/40 px-3 flex items-center justify-between text-xs font-mono">
                <span className="text-zinc-300 font-semibold">{selectedFile}</span>
                <button
                  onClick={handleCopyCode}
                  className="flex items-center gap-1 text-[11px] text-zinc-400 hover:text-zinc-200 px-2 py-0.5 rounded bg-zinc-800/60 hover:bg-zinc-800 transition-colors"
                >
                  {isCopied ? <Check className="w-3 h-3 text-emerald-400" /> : <Copy className="w-3 h-3" />}
                  <span>{isCopied ? 'Copied!' : 'Copy'}</span>
                </button>
              </div>
              <div className="flex-1 overflow-auto p-3 font-mono text-[11px] leading-relaxed text-zinc-300 bg-black/70">
                <pre className="select-text whitespace-pre font-mono text-emerald-400/90">
                  {GENERATED_WORKSPACE_FILES[selectedFile]?.content}
                </pre>
              </div>
            </div>
          </div>
        )}

        {/* [📜 Git Diff Review] Tab Content */}
        {activeTab === 'git' && (
          <div className="w-full h-full flex flex-col border border-zinc-800 rounded-lg overflow-hidden bg-zinc-950 shadow-2xl">
            {/* Git Diff Header */}
            <div className="p-3 bg-zinc-900/60 border-b border-zinc-800/80 flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div>
                <div className="flex items-center gap-2 text-xs font-mono">
                  <span className="px-1.5 py-0.5 rounded bg-sky-950 border border-sky-800 text-sky-400 text-[10px] font-bold">
                    feat/space-arcade
                  </span>
                  <span className="text-zinc-400">&rarr; master</span>
                  <span className="text-zinc-600 font-mono">#9f4e21a</span>
                </div>
                <p className="text-[11px] font-mono text-zinc-300 mt-1">
                  Commit: Add dual plasma laser cannons &amp; reactive HUD
                </p>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center gap-2">
                <button
                  onClick={() => setIsMerged(true)}
                  disabled={isMerged}
                  className="px-3 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:bg-zinc-800 disabled:text-emerald-400 text-white rounded text-xs font-mono font-semibold flex items-center gap-1.5 transition-colors shadow-sm"
                >
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  <span>{isMerged ? 'Merged to Master' : 'Approve & Merge'}</span>
                </button>
              </div>
            </div>

            {/* File Diff Tabs */}
            <div className="flex items-center bg-zinc-900/40 border-b border-zinc-800 px-2 py-1 gap-1">
              {GIT_DIFFS.map((diff, index) => (
                <button
                  key={diff.file}
                  onClick={() => setSelectedDiffFile(index)}
                  className={`px-2.5 py-1 rounded text-xs font-mono transition-colors flex items-center gap-2 ${
                    selectedDiffFile === index
                      ? 'bg-zinc-800 text-zinc-100 font-semibold'
                      : 'text-zinc-400 hover:text-zinc-200'
                  }`}
                >
                  <span>{diff.file}</span>
                  <span className="text-[10px] text-emerald-400">+{diff.additions}</span>
                  <span className="text-[10px] text-rose-400">-{diff.deletions}</span>
                </button>
              ))}
            </div>

            {/* Diff Viewer Body */}
            <div className="flex-1 overflow-auto p-3 font-mono text-xs bg-black/80 select-text">
              <pre className="whitespace-pre font-mono leading-relaxed">
                {GIT_DIFFS[selectedDiffFile]?.diff.split('\n').map((line, idx) => {
                  let lineClass = 'text-zinc-400';
                  if (line.startsWith('+')) lineClass = 'text-emerald-400 bg-emerald-950/30 px-1 rounded-sm';
                  if (line.startsWith('-')) lineClass = 'text-rose-400 bg-rose-950/30 px-1 rounded-sm';
                  if (line.startsWith('@@')) lineClass = 'text-sky-400 font-bold';

                  return (
                    <div key={idx} className={lineClass}>
                      {line}
                    </div>
                  );
                })}
              </pre>
            </div>
          </div>
        )}
      </div>

      {/* 3. Bottom Tab Switcher */}
      <div className="h-10 border-t border-zinc-800/80 bg-zinc-900/80 px-3 flex items-center justify-around shrink-0 text-xs font-mono">
        <button
          onClick={() => setActiveTab('preview')}
          className={`flex-1 py-1.5 flex items-center justify-center gap-1.5 rounded-md transition-all ${
            activeTab === 'preview'
              ? 'bg-zinc-800 text-emerald-400 font-semibold shadow-inner'
              : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          <span>🎮</span>
          <span>Live App / Game</span>
        </button>

        <button
          onClick={() => setActiveTab('files')}
          className={`flex-1 py-1.5 flex items-center justify-center gap-1.5 rounded-md transition-all ${
            activeTab === 'files'
              ? 'bg-zinc-800 text-sky-400 font-semibold shadow-inner'
              : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          <span>📁</span>
          <span>Workspace Files</span>
        </button>

        <button
          onClick={() => setActiveTab('git')}
          className={`flex-1 py-1.5 flex items-center justify-center gap-1.5 rounded-md transition-all ${
            activeTab === 'git'
              ? 'bg-zinc-800 text-purple-400 font-semibold shadow-inner'
              : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          <span>📜</span>
          <span>Git Diff Review</span>
        </button>
      </div>
    </aside>
  );
}
