import { useState, useEffect } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Sidebar } from './Sidebar';
import { FileExplorer } from './FileExplorer';
import { TaskDagVisualizer } from './TaskDagVisualizer';
import { AgentGrid } from './AgentGrid';
import { LivePreview } from './LivePreview';
import { GitDiffReview } from './GitDiffReview';
import { FileEditor } from './FileEditor';
import { Timeline } from './Timeline';
import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useCreateMission } from '../../../hooks/useMissions';
import { useWorkspaces, useCreateWorkspace } from '../../../hooks/useWorkspaces';
import { useAgents } from '../../../hooks/useAgents';
import { useWebSocket } from '../../../hooks/useWebSocket';
import type { CollaborationMode } from '../../../types';
import { 
  Columns2, 
  TerminalSquare, 
  PlaySquare, 
  ChevronDown, 
  ChevronUp, 
  Sparkles,
  Radio,
  FileDiff,
  Code2,
  Tv,
  PanelLeftClose,
  PanelLeftOpen
} from 'lucide-react';

// Split view modes for the BridgeMind Studio
type StudioViewMode = 'split' | 'terminals' | 'preview';
type RightPanelTab = 'preview' | 'diff' | 'editor';

const QUICK_STARTER_PROMPTS = [
  '🎮 Build Cyberpunk 2D Space Arcade Game with Audio FX',
  '⚡ Scaffold High-Performance Java Spring Boot Microservice',
  '🛡️ Audit Workspace JWT Auth & RBAC Security Pipeline',
];

export function WorkspaceLayout() {
  const queryClient = useQueryClient();
  const activeWorkspaceId = useWorkspaceStore((state) => state.activeWorkspaceId);
  const setActiveWorkspace = useWorkspaceStore((state) => state.setActiveWorkspace);
  const activeMissionId = useWorkspaceStore((state) => state.activeMissionId);
  const setActiveMission = useWorkspaceStore((state) => state.setActiveMission);
  const sidebarPanel = useWorkspaceStore((state) => state.sidebarPanel);
  
  const [title, setTitle] = useState('');
  const [collaborationMode, setCollaborationMode] = useState<CollaborationMode>('COLLABORATIVE');
  const [viewMode, setViewMode] = useState<StudioViewMode>('split');
  const [rightTab, setRightTab] = useState<RightPanelTab>('preview');
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
  const [selectedAgentId, setSelectedAgentId] = useState<string | null>(null);
  const [isMemoryOpen, setIsMemoryOpen] = useState(false);
  const [isFileExplorerOpen, setIsFileExplorerOpen] = useState(true);
  const [eventLogs, setEventLogs] = useState<string[]>([]);
  
  const { data: workspaces, isLoading: isLoadingWorkspaces } = useWorkspaces();
  const { mutate: createWorkspace } = useCreateWorkspace();
  const { mutate: createMission, isPending } = useCreateMission();
  const { data: agents = [] } = useAgents(activeMissionId);
  
  const { lastMessage } = useWebSocket(activeWorkspaceId, activeMissionId);

  // Auto-initialize default workspace if none selected
  useEffect(() => {
    if (!isLoadingWorkspaces && workspaces) {
      if (workspaces.length === 0) {
        createWorkspace("Default Workspace", {
          onSuccess: (w) => setActiveWorkspace(w.id)
        });
      } else if (!activeWorkspaceId) {
        setActiveWorkspace(workspaces[0].id);
      }
    }
  }, [workspaces, isLoadingWorkspaces, activeWorkspaceId, createWorkspace, setActiveWorkspace]);

  // Handle WebSocket Real-time events & audit logs
  useEffect(() => {
    if (lastMessage) {
      const eventType = lastMessage.type;
      
      // 1. Log to the Memory Inspector
      const logEntry = `[${new Date().toLocaleTimeString()}] ${eventType}: ${lastMessage.payload.details || 'OK'}`;
      setEventLogs(prev => [logEntry, ...prev].slice(0, 50));
      
      // 2. Refresh query caches based on event type
      if (eventType?.startsWith('AGENT_')) {
        queryClient.invalidateQueries({ queryKey: ['agents', activeMissionId] });
      } else if (eventType?.startsWith('MISSION_')) {
        queryClient.invalidateQueries({ queryKey: ['missions', activeWorkspaceId] });
        queryClient.invalidateQueries({ queryKey: ['mission-tasks', activeMissionId] });
      }

      // Always invalidate timeline & files on events
      queryClient.invalidateQueries({ queryKey: ['timeline', activeWorkspaceId] });
      queryClient.invalidateQueries({ queryKey: ['workspace-files', activeWorkspaceId] });
      queryClient.invalidateQueries({ queryKey: ['git-diff', activeWorkspaceId] });
    }
  }, [lastMessage, queryClient, activeMissionId, activeWorkspaceId]);

  const handleLaunch = (customTitle?: string) => {
    const trimmedTitle = (customTitle || title).trim();
    if (!trimmedTitle) return;

    let targetWorkspaceId = activeWorkspaceId;
    if (!targetWorkspaceId) {
      if (workspaces && workspaces.length > 0) {
        targetWorkspaceId = workspaces[0].id;
        setActiveWorkspace(targetWorkspaceId);
      } else {
        createWorkspace("Default Workspace", {
          onSuccess: (newWs) => {
            setActiveWorkspace(newWs.id);
            createMission({ workspaceId: newWs.id, title: trimmedTitle, collaborationMode }, {
              onSuccess: (mission) => {
                setActiveMission(mission.id);
              }
            });
          }
        });
        setTitle('');
        return;
      }
    }

    createMission({ workspaceId: targetWorkspaceId, title: trimmedTitle, collaborationMode }, {
      onSuccess: (mission) => {
        setActiveMission(mission.id);
        queryClient.invalidateQueries({ queryKey: ['missions', targetWorkspaceId] });
        queryClient.invalidateQueries({ queryKey: ['agents', mission.id] });
        queryClient.invalidateQueries({ queryKey: ['mission-tasks', mission.id] });
      }
    });
    setTitle('');
  };

  return (
    <div className="flex h-screen w-screen bg-zinc-950 text-zinc-300 font-sans overflow-hidden selection:bg-emerald-950 selection:text-emerald-300">
      {/* Primary Icon Sidebar */}
      <Sidebar />

      {/* Collapsible Left Panel: File Explorer */}
      {sidebarPanel === 'explorer' && isFileExplorerOpen && (
        <aside 
          aria-label="file explorer panel" 
          className="w-60 h-full border-r border-zinc-800/80 bg-zinc-950 shrink-0 flex flex-col min-h-0 z-10"
        >
          <FileExplorer
            workspaceId={activeWorkspaceId}
            selectedFile={selectedFile}
            onSelectFile={(path) => {
              setSelectedFile(path);
              setRightTab('editor');
              if (viewMode === 'terminals') setViewMode('split');
            }}
          />
        </aside>
      )}

      {/* Main Studio Area */}
      <div className="flex-1 flex flex-col min-w-0 min-h-0 bg-zinc-950">
        {/* Top Command Bar */}
        <header 
          role="banner" 
          aria-label="mission command bar" 
          className="h-14 border-b border-zinc-800/80 flex items-center justify-between px-4 bg-zinc-950 shrink-0 gap-3 z-10"
        >
          {/* File Explorer Toggle + Mode Switcher */}
          <div className="flex items-center gap-2 shrink-0">
            {sidebarPanel === 'explorer' && (
              <button
                onClick={() => setIsFileExplorerOpen(!isFileExplorerOpen)}
                className="p-1.5 hover:bg-zinc-900 border border-zinc-800/80 rounded text-zinc-400 hover:text-zinc-200 transition-colors"
                title={isFileExplorerOpen ? 'Hide Files' : 'Show Files'}
              >
                {isFileExplorerOpen ? <PanelLeftClose className="w-4 h-4" /> : <PanelLeftOpen className="w-4 h-4" />}
              </button>
            )}

            {/* Mode Switcher Pill */}
            <div className="flex items-center bg-zinc-900 border border-zinc-800 rounded-lg p-0.5 text-xs font-mono shrink-0">
              <button
                onClick={() => setCollaborationMode('COLLABORATIVE')}
                className={`px-3 py-1 rounded-md transition-all flex items-center gap-1.5 ${
                  collaborationMode === 'COLLABORATIVE'
                    ? 'bg-zinc-800 text-sky-400 font-semibold shadow-sm'
                    : 'text-zinc-500 hover:text-zinc-300'
                }`}
                title="Collaborative Mode: Agents share memory, lock mechanisms, and coordinated Git workspace"
              >
                <span>🤝</span>
                <span className="hidden sm:inline">Collaborative</span>
              </button>
              <button
                onClick={() => setCollaborationMode('ISOLATED')}
                className={`px-3 py-1 rounded-md transition-all flex items-center gap-1.5 ${
                  collaborationMode === 'ISOLATED'
                    ? 'bg-zinc-800 text-emerald-400 font-semibold shadow-sm'
                    : 'text-zinc-500 hover:text-zinc-300'
                }`}
                title="Isolated Mode: Strict sandboxed memory partitions for ultra-fast parallel executions"
              >
                <span>⚡</span>
                <span className="hidden sm:inline">Isolated</span>
              </button>
            </div>
          </div>

          {/* Central Command Bar */}
          <div className="flex-1 flex justify-center max-w-2xl min-w-0">
            <div className="w-full bg-zinc-900 border border-zinc-800/90 rounded-lg flex items-center px-3 py-1.5 text-sm text-zinc-400 focus-within:border-emerald-500/60 transition-colors shadow-inner">
              <span className="mr-2 text-emerald-400 font-mono font-bold">❯</span>
              <input 
                type="text" 
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Initialize project prompt (e.g. Build 2D Space Shooter Game)..." 
                className="bg-transparent border-none outline-none flex-1 text-zinc-200 placeholder:text-zinc-600 font-mono text-xs"
                onKeyDown={(e) => e.key === 'Enter' && handleLaunch()}
              />
              <button 
                onClick={() => handleLaunch()}
                disabled={isPending || !title.trim()}
                className="px-3.5 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded text-xs transition-colors ml-2 font-medium font-mono whitespace-nowrap shadow-sm"
              >
                {isPending ? 'Launching...' : 'Launch Mission'}
              </button>
            </div>
          </div>

          {/* Right Tools: View Switchers & Bus Pulse */}
          <div className="flex items-center gap-2.5 shrink-0">
            <div className="flex items-center bg-zinc-900 border border-zinc-800 rounded-lg p-0.5 text-xs font-mono">
              <button
                onClick={() => setViewMode('split')}
                title="Studio Split View (60% Matrix / 40% Inspection Panel)"
                className={`p-1.5 rounded transition-all flex items-center gap-1 ${
                  viewMode === 'split' ? 'bg-zinc-800 text-emerald-400 font-bold' : 'text-zinc-500 hover:text-zinc-300'
                }`}
              >
                <Columns2 className="w-3.5 h-3.5" />
                <span className="hidden md:inline text-[10px]">Split</span>
              </button>
              <button
                onClick={() => setViewMode('terminals')}
                title="Fullscreen Agent Terminals (100%)"
                className={`p-1.5 rounded transition-all flex items-center gap-1 ${
                  viewMode === 'terminals' ? 'bg-zinc-800 text-emerald-400 font-bold' : 'text-zinc-500 hover:text-zinc-300'
                }`}
              >
                <TerminalSquare className="w-3.5 h-3.5" />
                <span className="hidden md:inline text-[10px]">Terminals</span>
              </button>
              <button
                onClick={() => setViewMode('preview')}
                title="Fullscreen Live Inspection & Preview (100%)"
                className={`p-1.5 rounded transition-all flex items-center gap-1 ${
                  viewMode === 'preview' ? 'bg-zinc-800 text-emerald-400 font-bold' : 'text-zinc-500 hover:text-zinc-300'
                }`}
              >
                <PlaySquare className="w-3.5 h-3.5" />
                <span className="hidden md:inline text-[10px]">Preview</span>
              </button>
            </div>

            {/* Live Indicator */}
            <div className="hidden xl:flex items-center text-xs font-mono text-zinc-500 gap-1.5 pl-1 border-l border-zinc-800/80">
              <Radio className="w-3.5 h-3.5 text-emerald-400 animate-pulse" />
              <span className="text-[11px]">ADE: <strong className="text-zinc-300">Active</strong></span>
            </div>
          </div>
        </header>

        {/* Task DAG Swarm Topology Visualizer */}
        <TaskDagVisualizer
          missionId={activeMissionId}
          agents={agents}
          selectedAgentId={selectedAgentId}
          onSelectAgent={setSelectedAgentId}
        />

        {/* Quick Starter Mission Chips */}
        {!activeMissionId && (
          <div className="px-4 py-2 bg-zinc-950/80 border-b border-zinc-900 flex items-center gap-2 overflow-x-auto text-[11px] font-mono text-zinc-400 shrink-0">
            <span className="text-zinc-600 uppercase text-[9px] font-bold tracking-wider shrink-0 flex items-center gap-1">
              <Sparkles className="w-3 h-3 text-emerald-400" /> Starter Kits:
            </span>
            {QUICK_STARTER_PROMPTS.map((promptText, i) => (
              <button
                key={i}
                onClick={() => handleLaunch(promptText)}
                className="px-2.5 py-0.5 bg-zinc-900 hover:bg-zinc-800/80 hover:text-emerald-400 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap text-left"
              >
                {promptText}
              </button>
            ))}
          </div>
        )}

        {/* Studio Body: Split View Layout */}
        <main className="flex-1 flex min-h-0 relative overflow-hidden">
          {/* Left Side: 4-Pane Multi-Agent Matrix */}
          {(viewMode === 'split' || viewMode === 'terminals') && (
            <section 
              data-testid="agent-matrix-section"
              aria-label="Multi-Agent Matrix"
              className={`h-full flex flex-col min-h-0 transition-all duration-300 ${
                viewMode === 'split' ? 'w-[55%] border-r border-zinc-800/80' : 'w-full'
              }`}
            >
              <AgentGrid />
            </section>
          )}

          {/* Right Side: Tabbed Inspection Flight Deck */}
          {(viewMode === 'split' || viewMode === 'preview') && (
            <section 
              data-testid="preview-section"
              aria-label="Live Game Preview"
              className={`h-full flex flex-col min-h-0 transition-all duration-300 ${
                viewMode === 'split' ? 'w-[45%]' : 'w-full'
              } bg-zinc-950`}
            >
              {/* Inspection Tab Switcher Bar */}
              <div className="h-10 border-b border-zinc-800/80 bg-zinc-950 px-3 flex items-center justify-between shrink-0 font-mono text-xs select-none">
                <div className="flex items-center gap-1 bg-zinc-900/80 p-0.5 rounded border border-zinc-800/80">
                  <button
                    onClick={() => setRightTab('preview')}
                    className={`flex items-center gap-1.5 px-3 py-1 rounded transition-colors text-xs ${
                      rightTab === 'preview'
                        ? 'bg-zinc-800 text-emerald-400 font-semibold shadow-sm'
                        : 'text-zinc-500 hover:text-zinc-300'
                    }`}
                  >
                    <Tv className="w-3.5 h-3.5" />
                    <span>Live Preview</span>
                  </button>
                  <button
                    onClick={() => setRightTab('diff')}
                    className={`flex items-center gap-1.5 px-3 py-1 rounded transition-colors text-xs ${
                      rightTab === 'diff'
                        ? 'bg-zinc-800 text-amber-400 font-semibold shadow-sm'
                        : 'text-zinc-500 hover:text-zinc-300'
                    }`}
                  >
                    <FileDiff className="w-3.5 h-3.5" />
                    <span>Git Changes</span>
                  </button>
                  <button
                    onClick={() => setRightTab('editor')}
                    className={`flex items-center gap-1.5 px-3 py-1 rounded transition-colors text-xs ${
                      rightTab === 'editor'
                        ? 'bg-zinc-800 text-sky-400 font-semibold shadow-sm'
                        : 'text-zinc-500 hover:text-zinc-300'
                    }`}
                  >
                    <Code2 className="w-3.5 h-3.5" />
                    <span>Code Editor</span>
                  </button>
                </div>

                <div className="text-[10px] text-zinc-500 font-mono hidden sm:inline">
                  {rightTab === 'preview' ? 'HTML5 Canvas Live' : rightTab === 'diff' ? 'Git Head Delta' : (selectedFile || 'No file opened')}
                </div>
              </div>

              {/* Tab Content Body */}
              <div className="flex-1 flex flex-col min-h-0 overflow-hidden">
                {rightTab === 'preview' && <LivePreview />}
                {rightTab === 'diff' && <GitDiffReview workspaceId={activeWorkspaceId} />}
                {rightTab === 'editor' && (
                  <FileEditor 
                    workspaceId={activeWorkspaceId} 
                    filePath={selectedFile} 
                    onClose={() => setRightTab('preview')} 
                  />
                )}
              </div>
            </section>
          )}
        </main>

        {/* Bottom Drawer: Memory Inspector & Live Event Stream */}
        <footer className={`border-t border-zinc-800/80 bg-zinc-950 flex flex-col shrink-0 transition-all duration-200 ${
          isMemoryOpen ? 'h-44' : 'h-8'
        }`}>
          <div 
            onClick={() => setIsMemoryOpen(!isMemoryOpen)}
            className="h-8 flex items-center justify-between px-4 bg-zinc-900/60 hover:bg-zinc-900 cursor-pointer transition-colors select-none"
          >
            <h2 className="text-[11px] font-mono font-medium text-zinc-400 tracking-wider uppercase flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
              Memory Inspector &amp; Live WebSocket Stream
            </h2>
            <div className="flex items-center gap-2">
              <span className="text-[10px] font-mono text-zinc-500">{eventLogs.length} events buffered</span>
              {isMemoryOpen ? <ChevronDown className="w-3.5 h-3.5 text-zinc-500" /> : <ChevronUp className="w-3.5 h-3.5 text-zinc-500" />}
            </div>
          </div>

          {isMemoryOpen && (
            <div data-testid="memory-inspector" className="flex-1 overflow-y-auto p-3 font-mono text-xs bg-black/90">
              <ul className="space-y-1 text-zinc-400 font-mono list-disc list-inside">
                {eventLogs.length > 0 ? (
                  eventLogs.map((log, i) => (
                    <li key={i} className="text-zinc-300 text-[11px] leading-relaxed">
                      {log}
                    </li>
                  ))
                ) : activeWorkspaceId ? (
                  <li className="text-zinc-600 text-[11px]">
                    Ready to capture live agent thoughts, tool executions, and Git differential snapshots...
                  </li>
                ) : (
                  <li className="text-zinc-600 text-[11px]">Select a workspace to view memory stream.</li>
                )}
              </ul>
            </div>
          )}
        </footer>
      </div>

      {/* Right Drawer: Timeline */}
      <Timeline />
    </div>
  );
}
