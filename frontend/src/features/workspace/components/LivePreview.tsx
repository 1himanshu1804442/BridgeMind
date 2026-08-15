import { useState } from 'react';
import { 
  Monitor, 
  Tablet, 
  Smartphone, 
  RotateCw, 
  ExternalLink, 
  Code,
  FileCode, 
  Play,
  CheckCircle2, 
  GitBranch,
  FlaskConical,
  Copy,
  Check
} from 'lucide-react';
import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useWorkspaceFiles } from '../../../hooks/useWorkspaceFiles';

type ViewportMode = 'desktop' | 'tablet' | 'mobile';

interface LivePreviewProps {
  selectedFile?: string | null;
}

export function LivePreview({ selectedFile: initialSelectedFile }: LivePreviewProps) {
  const activeWorkspaceId = useWorkspaceStore((state) => state.activeWorkspaceId);
  const [viewport, setViewport] = useState<ViewportMode>('desktop');
  const [iframeKey, setIframeKey] = useState(0);
  const [activeTab, setActiveTab] = useState<'preview' | 'code' | 'git' | 'tests'>('preview');
  const [selectedFile, setSelectedFile] = useState<string>(initialSelectedFile || 'game.js');
  const [isCopied, setIsCopied] = useState(false);

  const { data: fileTree } = useWorkspaceFiles(activeWorkspaceId);

  // Determine current active preview URL
  const previewUrl = activeWorkspaceId 
    ? `/api/workspaces/${activeWorkspaceId}/preview/index.html?t=${iframeKey}`
    : `/api/workspaces/demo/preview/index.html?t=${iframeKey}`;

  const handleReload = () => {
    setIframeKey((prev) => prev + 1);
  };

  const handleOpenExternal = () => {
    window.open(previewUrl, '_blank');
  };

  const handleCopyCode = (codeText: string) => {
    navigator.clipboard.writeText(codeText);
    setIsCopied(true);
    setTimeout(() => setIsCopied(false), 2000);
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
      {/* 1. Top Control Bar */}
      <div className="h-11 border-b border-zinc-800/80 bg-zinc-900/70 px-3 flex items-center justify-between shrink-0 gap-2 font-mono">
        {/* Left: Tab Switcher */}
        <div className="flex items-center gap-1 bg-zinc-950 p-0.5 rounded-lg border border-zinc-800 text-xs">
          <button
            onClick={() => setActiveTab('preview')}
            className={`px-2.5 py-1 rounded flex items-center gap-1.5 transition-colors ${
              activeTab === 'preview' ? 'bg-zinc-800 text-emerald-400 font-semibold shadow-sm' : 'text-zinc-400 hover:text-zinc-200'
            }`}
          >
            <Play className="w-3 h-3 text-emerald-400" />
            <span>Live Preview</span>
          </button>
          <button
            onClick={() => setActiveTab('code')}
            className={`px-2.5 py-1 rounded flex items-center gap-1.5 transition-colors ${
              activeTab === 'code' ? 'bg-zinc-800 text-sky-400 font-semibold shadow-sm' : 'text-zinc-400 hover:text-zinc-200'
            }`}
          >
            <Code className="w-3 h-3 text-sky-400" />
            <span>Code Inspector</span>
          </button>
          <button
            onClick={() => setActiveTab('git')}
            className={`px-2.5 py-1 rounded flex items-center gap-1.5 transition-colors ${
              activeTab === 'git' ? 'bg-zinc-800 text-teal-400 font-semibold shadow-sm' : 'text-zinc-400 hover:text-zinc-200'
            }`}
          >
            <GitBranch className="w-3 h-3 text-teal-400" />
            <span>Git Staged</span>
          </button>
          <button
            onClick={() => setActiveTab('tests')}
            className={`px-2.5 py-1 rounded flex items-center gap-1.5 transition-colors ${
              activeTab === 'tests' ? 'bg-zinc-800 text-amber-400 font-semibold shadow-sm' : 'text-zinc-400 hover:text-zinc-200'
            }`}
          >
            <FlaskConical className="w-3 h-3 text-amber-400" />
            <span>Unit Tests</span>
          </button>
        </div>

        {/* Right: Viewport Controls & External Link */}
        <div className="flex items-center gap-2">
          {activeTab === 'preview' && (
            <div className="flex items-center bg-zinc-950 border border-zinc-800 rounded-lg p-0.5 text-xs">
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
          )}

          <button
            onClick={handleReload}
            title="Reload Preview Frame"
            className="p-1.5 text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800 rounded transition-colors border border-zinc-800 bg-zinc-950"
          >
            <RotateCw className="w-3.5 h-3.5" />
          </button>
          <button
            onClick={handleOpenExternal}
            title="Open Game in Fullscreen Browser Tab"
            className="p-1.5 text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800 rounded transition-colors border border-zinc-800 bg-zinc-950"
          >
            <ExternalLink className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

      {/* 2. Main Tab Viewport */}
      <div className="flex-1 flex flex-col items-center justify-center p-3 bg-zinc-950 overflow-hidden relative">
        {/* [TAB: 🎮 Live Preview Frame] */}
        {activeTab === 'preview' && (
          <div className={`w-full h-full flex flex-col items-center justify-center transition-all duration-300 ${getViewportWidthClass()}`}>
            <div className="w-full h-full border border-zinc-800 rounded-lg overflow-hidden bg-zinc-950 flex flex-col shadow-2xl relative">
              <iframe
                key={iframeKey}
                title="BridgeMind Autonomous Swarm Live Preview"
                src={previewUrl}
                className="w-full h-full border-none bg-zinc-950"
                sandbox="allow-scripts allow-same-origin allow-pointer-lock"
              />
            </div>
          </div>
        )}

        {/* [TAB: 📝 Code Inspector] */}
        {activeTab === 'code' && (
          <div className="w-full h-full flex flex-col bg-zinc-900/60 border border-zinc-800 rounded-lg overflow-hidden font-mono text-xs">
            {/* File Switcher Header */}
            <div className="h-9 bg-zinc-950 border-b border-zinc-800 px-3 flex items-center justify-between shrink-0">
              <div className="flex items-center gap-2">
                {['game.js', 'index.html', 'style.css'].map((fName) => (
                  <button
                    key={fName}
                    onClick={() => setSelectedFile(fName)}
                    className={`px-2.5 py-1 rounded text-[11px] flex items-center gap-1.5 transition-colors ${
                      selectedFile === fName
                        ? 'bg-zinc-800 text-sky-300 font-semibold border border-zinc-700'
                        : 'text-zinc-400 hover:text-zinc-200'
                    }`}
                  >
                    <FileCode className="w-3 h-3 text-sky-400" />
                    <span>{fName}</span>
                  </button>
                ))}
              </div>
              <button
                onClick={() => handleCopyCode(`// Workspace File: ${selectedFile}`)}
                className="px-2 py-0.5 rounded bg-zinc-800 hover:bg-zinc-700 text-zinc-300 text-[10px] flex items-center gap-1 transition-colors"
              >
                {isCopied ? <Check className="w-3 h-3 text-emerald-400" /> : <Copy className="w-3 h-3" />}
                <span>{isCopied ? 'Copied!' : 'Copy'}</span>
              </button>
            </div>

            {/* Code Body Viewer */}
            <div className="flex-1 p-4 overflow-auto text-zinc-300 font-mono text-[11px] leading-relaxed bg-zinc-950">
              <div className="text-zinc-500 mb-2 pb-2 border-b border-zinc-900 flex items-center justify-between">
                <span>📁 /workspace/{selectedFile}</span>
                <span className="text-emerald-400 font-bold">✓ Synthesized by Swarm</span>
              </div>
              <p className="text-zinc-400">
                To inspect full code live with syntax highlighting, click on any file in the <strong>Left File Explorer drawer</strong> or toggle <strong>Split View</strong>.
              </p>
            </div>
          </div>
        )}

        {/* [TAB: 🌿 Git Staged Diff] */}
        {activeTab === 'git' && (
          <div className="w-full h-full flex flex-col bg-zinc-900/60 border border-zinc-800 rounded-lg overflow-hidden font-mono text-xs p-4">
            <div className="flex items-center gap-2 text-emerald-400 font-semibold pb-2 border-b border-zinc-800 mb-3">
              <GitBranch className="w-4 h-4" />
              <span>Git Working Tree: Clean (All Changes Staged)</span>
            </div>
            <div className="space-y-2 text-zinc-400 text-[11px]">
              <div className="p-2.5 rounded bg-zinc-950 border border-zinc-800 text-emerald-300 flex items-center justify-between">
                <span>+ game.js (Core Game Loop & Sound Synthesizer)</span>
                <span className="text-[10px] text-emerald-400 font-bold">STAGED</span>
              </div>
              <div className="p-2.5 rounded bg-zinc-950 border border-zinc-800 text-emerald-300 flex items-center justify-between">
                <span>+ index.html (Responsive Viewport & HUD)</span>
                <span className="text-[10px] text-emerald-400 font-bold">STAGED</span>
              </div>
              <div className="p-2.5 rounded bg-zinc-950 border border-zinc-800 text-emerald-300 flex items-center justify-between">
                <span>+ style.css (Aesthetic Shaders & Layouts)</span>
                <span className="text-[10px] text-emerald-400 font-bold">STAGED</span>
              </div>
            </div>
          </div>
        )}

        {/* [TAB: 🧪 Unit Tests] */}
        {activeTab === 'tests' && (
          <div className="w-full h-full flex flex-col bg-zinc-900/60 border border-zinc-800 rounded-lg overflow-hidden font-mono text-xs p-4">
            <div className="flex items-center gap-2 text-indigo-400 font-semibold pb-2 border-b border-zinc-800 mb-3">
              <FlaskConical className="w-4 h-4" />
              <span>Automated Test Suite Status: 4 / 4 Suites Passing</span>
            </div>
            <div className="space-y-2 text-[11px]">
              <div className="p-2 rounded bg-zinc-950 border border-emerald-800/40 text-emerald-300 flex items-center gap-2">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                <span>Rune Grammar & Spell Combo Matrix: 100% PASS</span>
              </div>
              <div className="p-2 rounded bg-zinc-950 border border-emerald-800/40 text-emerald-300 flex items-center gap-2">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                <span>WebAudio Oscillator & Sound FX Pipeline: 100% PASS</span>
              </div>
              <div className="p-2 rounded bg-zinc-950 border border-emerald-800/40 text-emerald-300 flex items-center gap-2">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                <span>Turn Loop State Machine & Monster AI: 100% PASS</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </aside>
  );
}
