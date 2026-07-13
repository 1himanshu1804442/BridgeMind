import { useEffect, useRef } from 'react';
import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useAgents } from '../../../hooks/useAgents';
import type { Agent } from '../../../types';

function AgentCard({ agent }: { agent: Agent }) {
  const terminalEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (terminalEndRef.current?.scrollIntoView) {
      terminalEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [agent.lastOutput]);

  return (
    <div 
      data-testid="agent-pane"
      className="flex flex-col bg-zinc-900/50 border border-zinc-800/50 rounded-lg overflow-hidden"
    >
      <div className="flex items-center gap-2 px-3 py-2 border-b border-zinc-800/50 bg-zinc-900/80 text-xs font-mono text-zinc-400">
        <span className={`w-2 h-2 rounded-full ${agent.status === 'RUNNING' ? 'bg-emerald-500' : 'bg-zinc-500'}`} />
        <span className="font-semibold text-zinc-300">{agent.role}</span>
        <span className="text-zinc-600">|</span>
        <span>{agent.model}</span>
        <span className="text-zinc-600">|</span>
        <span>{agent.status}</span>
        <span className="flex-1" />
        <span>${(agent.costCents / 100).toFixed(2)}</span>
        <span className="text-zinc-600">|</span>
        <span>{Math.floor(agent.elapsedMs / 1000)}s</span>
      </div>
      <div className="flex-1 p-4 overflow-y-auto bg-black/50">
        <div className="text-emerald-400 text-[11px] font-mono leading-relaxed whitespace-pre-wrap break-words">
          {agent.lastOutput || `> Initializing environment for ${agent.role}...`}
          <span className="animate-pulse">█</span>
        </div>
        <div ref={terminalEndRef} />
      </div>
    </div>
  );
}

export function AgentGrid() {
  const activeMissionId = useWorkspaceStore((state) => state.activeMissionId);
  const { data: agents = [], isLoading } = useAgents(activeMissionId);

  if (!activeMissionId) {
    return <div className="flex-1 flex items-center justify-center text-zinc-500">Select or create a mission to view agents</div>;
  }

  if (isLoading) {
    return <div className="flex-1 flex items-center justify-center text-zinc-500">Loading agents...</div>;
  }

  return (
    <div data-testid="agent-grid-container" className="flex-1 grid grid-cols-2 grid-rows-2 gap-4 p-4 bg-zinc-950">
      {agents.map((agent: Agent) => (
        <AgentCard key={agent.id} agent={agent} />
      ))}
    </div>
  )
}

