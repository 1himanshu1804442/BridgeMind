import { useState, useEffect } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Sidebar } from './Sidebar';
import { AgentGrid } from './AgentGrid';
import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useCreateMission } from '../../../hooks/useMissions';
import { useWorkspaces, useCreateWorkspace } from '../../../hooks/useWorkspaces';
import { useWebSocket } from '../../../hooks/useWebSocket';
import { Timeline } from './Timeline';
import type { CollaborationMode } from '../../../types';
import { Activity } from 'lucide-react';

export function WorkspaceLayout() {
  const queryClient = useQueryClient();
  const activeWorkspaceId = useWorkspaceStore((state) => state.activeWorkspaceId);
  const setActiveWorkspace = useWorkspaceStore((state) => state.setActiveWorkspace);
  const activeMissionId = useWorkspaceStore((state) => state.activeMissionId);
  const setActiveMission = useWorkspaceStore((state) => state.setActiveMission);
  
  const [title, setTitle] = useState('');
  const [collaborationMode, setCollaborationMode] = useState<CollaborationMode>('COLLABORATIVE');
  const [eventLogs, setEventLogs] = useState<string[]>([]);
  
  const { data: workspaces, isLoading: isLoadingWorkspaces } = useWorkspaces();
  const { mutate: createWorkspace } = useCreateWorkspace();
  const { mutate: createMission, isPending } = useCreateMission();
  
  const { lastMessage } = useWebSocket(activeWorkspaceId, activeMissionId);

  // Auto-initialize workspace
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

  // Handle WebSocket Real-time events
  useEffect(() => {
    if (lastMessage) {
      const eventType = lastMessage.type;
      
      // 1. Log to the Memory Inspector
      const logEntry = `[${new Date().toLocaleTimeString()}] ${eventType}: ${lastMessage.payload.details || 'OK'}`;
      setEventLogs(prev => [logEntry, ...prev].slice(0, 50));
      
      // 2. Instantly update the UI based on event type
      if (eventType?.startsWith('AGENT_')) {
        queryClient.invalidateQueries({ queryKey: ['agents', activeMissionId] });
      } else if (eventType?.startsWith('MISSION_')) {
        queryClient.invalidateQueries({ queryKey: ['missions', activeWorkspaceId] });
      }

      // Always invalidate timeline on any new event to keep audit log fresh
      queryClient.invalidateQueries({ queryKey: ['timeline', activeWorkspaceId] });
    }
  }, [lastMessage, queryClient, activeMissionId, activeWorkspaceId]);

  const handleLaunch = () => {
    const trimmedTitle = title.trim();
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
      }
    });
    setTitle('');
  };

  return (
    <div className="flex h-screen w-screen bg-zinc-950 text-zinc-300 font-sans overflow-hidden selection:bg-zinc-800">
      <Sidebar />

      <div className="flex-1 flex flex-col min-w-0">
        <header 
          role="banner" 
          aria-label="mission command bar" 
          className="h-14 border-b border-zinc-800/60 flex items-center justify-between px-4 bg-zinc-950 shrink-0 gap-4"
        >
          {/* Mode Switcher Pill */}
          <div className="flex items-center bg-zinc-900 border border-zinc-800 rounded-lg p-0.5 text-xs font-mono">
            <button
              onClick={() => setCollaborationMode('COLLABORATIVE')}
              className={`px-3 py-1 rounded-md transition-all flex items-center gap-1.5 ${
                collaborationMode === 'COLLABORATIVE'
                  ? 'bg-zinc-800 text-sky-400 font-semibold shadow-sm'
                  : 'text-zinc-500 hover:text-zinc-300'
              }`}
            >
              <span>🤝</span>
              <span>Collaborative</span>
            </button>
            <button
              onClick={() => setCollaborationMode('ISOLATED')}
              className={`px-3 py-1 rounded-md transition-all flex items-center gap-1.5 ${
                collaborationMode === 'ISOLATED'
                  ? 'bg-zinc-800 text-emerald-400 font-semibold shadow-sm'
                  : 'text-zinc-500 hover:text-zinc-300'
              }`}
            >
              <span>⚡</span>
              <span>Isolated</span>
            </button>
          </div>

          {/* Central Command Bar */}
          <div className="flex-1 flex justify-center max-w-2xl">
            <div className="w-full bg-zinc-900 border border-zinc-800/80 rounded-lg flex items-center px-3 py-1.5 text-sm text-zinc-400 focus-within:border-emerald-500/50 transition-colors shadow-inner">
              <span className="mr-2 text-emerald-400 font-mono">❯</span>
              <input 
                type="text" 
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Initialize project prompt (e.g. Build 2D Space Shooter Game)..." 
                className="bg-transparent border-none outline-none flex-1 text-zinc-200 placeholder:text-zinc-600 font-mono text-xs"
                onKeyDown={(e) => e.key === 'Enter' && handleLaunch()}
              />
              <button 
                onClick={handleLaunch}
                disabled={isPending || !title.trim()}
                className="px-3.5 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded text-xs transition-colors ml-2 font-medium font-mono whitespace-nowrap"
              >
                {isPending ? 'Launching...' : 'Launch Mission'}
              </button>
            </div>
          </div>

          <div className="w-48 hidden lg:flex items-center justify-end text-xs font-mono text-zinc-500 gap-1.5">
            <Activity className="w-3.5 h-3.5 text-emerald-400 animate-pulse" />
            <span>Event Bus: <strong className="text-zinc-300">Active</strong></span>
          </div>
        </header>

        <main className="flex-1 flex flex-col min-h-0">
          <AgentGrid />

          {/* Bottom Drawer: Memory Inspector */}
          <div className="h-44 border-t border-zinc-800/60 bg-zinc-950 flex flex-col shrink-0">
            <div className="h-8 border-b border-zinc-800/60 flex items-center justify-between px-4 bg-zinc-900/40">
              <h2 className="text-[11px] font-mono font-medium text-zinc-400 tracking-wider uppercase flex items-center gap-1.5">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
                Memory Inspector &amp; Live WebSocket Stream
              </h2>
              <span className="text-[10px] font-mono text-zinc-500">{eventLogs.length} events buffered</span>
            </div>
            <div data-testid="memory-inspector" className="flex-1 overflow-y-auto p-3 font-mono text-xs">
              <ul className="space-y-1.5 text-zinc-400 font-mono list-disc list-inside">
                {eventLogs.length > 0 ? (
                  eventLogs.map((log, i) => <li key={i} className="text-zinc-300 text-[11px]">{log}</li>)
                ) : activeWorkspaceId ? (
                  <li className="text-zinc-600 text-[11px]">Ready to capture live agent thoughts, commands, and Git memory diffs...</li>
                ) : (
                  <li className="text-zinc-600 text-[11px]">Select a workspace to view memory logs.</li>
                )}
              </ul>
            </div>
          </div>
        </main>
      </div>

      <Timeline />
    </div>
  );
}
