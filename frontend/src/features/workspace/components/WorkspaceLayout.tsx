import { useState, useEffect } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Sidebar } from './Sidebar';
import { AgentGrid } from './AgentGrid';
import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useCreateMission } from '../../../hooks/useMissions';
import { useWorkspaces, useCreateWorkspace } from '../../../hooks/useWorkspaces';
import { useWebSocket } from '../../../hooks/useWebSocket';
import { Timeline } from './Timeline';

export function WorkspaceLayout() {
  const queryClient = useQueryClient();
  const activeWorkspaceId = useWorkspaceStore((state) => state.activeWorkspaceId);
  const setActiveWorkspace = useWorkspaceStore((state) => state.setActiveWorkspace);
  const activeMissionId = useWorkspaceStore((state) => state.activeMissionId);
  const setActiveMission = useWorkspaceStore((state) => state.setActiveMission);
  
  const [title, setTitle] = useState('');
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
            createMission({ workspaceId: newWs.id, title: trimmedTitle }, {
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

    createMission({ workspaceId: targetWorkspaceId, title: trimmedTitle }, {
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
          className="h-12 border-b border-zinc-800/40 flex items-center px-4 bg-zinc-950 shrink-0"
        >
          <div className="flex-1 flex justify-center">
            <div className="w-full max-w-2xl bg-zinc-900 border border-zinc-800/60 rounded-md flex items-center px-3 py-1.5 text-sm text-zinc-400">
              <span className="mr-2 text-zinc-500">❯</span>
              <input 
                type="text" 
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Build OAuth..." 
                className="bg-transparent border-none outline-none flex-1 text-zinc-200 placeholder:text-zinc-600"
                onKeyDown={(e) => e.key === 'Enter' && handleLaunch()}
              />
              <button 
                onClick={handleLaunch}
                disabled={isPending || !title.trim()}
                className="px-3 py-0.5 bg-zinc-800 hover:bg-zinc-700 disabled:opacity-50 text-zinc-300 rounded text-xs transition-colors ml-2 font-medium"
              >
                {isPending ? 'Launching...' : 'Launch'}
              </button>
            </div>
          </div>
        </header>

        <main className="flex-1 flex flex-col min-h-0">
          <AgentGrid />

          <div className="h-1/3 min-h-[250px] border-t border-zinc-800/40 bg-zinc-950 flex flex-col">
            <div className="h-9 border-b border-zinc-800/40 flex items-center px-4 bg-zinc-900/20">
              <h2 className="text-xs font-medium text-zinc-400 tracking-wide uppercase">Memory Inspector (Live Event Bus)</h2>
            </div>
            <div data-testid="memory-inspector" className="flex-1 overflow-y-auto p-4">
              <ul className="space-y-2 text-sm text-zinc-400 font-mono list-disc list-inside">
                {eventLogs.length > 0 ? (
                  eventLogs.map((log, i) => <li key={i} className="text-zinc-300">{log}</li>)
                ) : activeWorkspaceId ? (
                  <li>Ready to receive agent memory context...</li>
                ) : (
                  <li>Select a workspace to view memory logs.</li>
                )}
              </ul>
            </div>
          </div>
        </main>
      </div>

      <Timeline />
    </div>
  )
}
