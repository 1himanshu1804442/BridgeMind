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
      className="flex flex-col bg-zinc-950 border border-primary/50 shadow-[0_0_15px_rgba(0,255,170,0.1)] rounded overflow-hidden"
    >
      <div className="flex items-center gap-2 px-3 py-1.5 border-b border-primary/50 bg-black/60 text-[10px] font-mono text-primary/80 uppercase tracking-wider">
        <span className={`w-2 h-2 rounded-full shadow-[0_0_8px_currentColor] ${agent.status === 'RUNNING' ? 'bg-primary text-primary' : 'bg-zinc-500 text-zinc-500'}`} />
        <span className="font-bold text-primary">{agent.role}</span>
        <span className="text-primary/40">|</span>
        <span className="text-primary/70">{agent.model}</span>
        <span className="text-primary/40">|</span>
        <span className="text-primary/70">{agent.status}</span>
        <span className="flex-1" />
        <span className="text-primary/70">${(agent.costCents / 100).toFixed(2)}</span>
        <span className="text-primary/40">|</span>
        <span className="text-primary/70">{Math.floor(agent.elapsedMs / 1000)}s</span>
      </div>
      <div className="flex-1 p-3 overflow-y-auto bg-black/80">
        <div className="text-primary text-[11px] font-mono leading-relaxed whitespace-pre-wrap break-words">
          {agent.lastOutput || `> Initializing environment for ${agent.role}...`}
          <span className="animate-pulse ml-1 inline-block w-1.5 h-3 bg-primary align-middle"></span>
        </div>
        <div ref={terminalEndRef} />
      </div>
    </div>
  );
}

function EmptyAgentPane({ index }: { index: number }) {
  return (
    <div 
      data-testid="agent-pane"
      className="flex flex-col bg-zinc-950 border border-primary/20 rounded overflow-hidden opacity-60"
    >
      <div className="flex items-center gap-2 px-3 py-1.5 border-b border-primary/20 bg-black/40 text-[10px] font-mono text-primary/40 uppercase tracking-wider">
        <span className="w-2 h-2 rounded-full bg-primary/20" />
        <span className="font-bold">SLOT 0{index + 1}</span>
      </div>
      <div className="flex-1 flex items-center justify-center bg-black/40">
        <div className="text-primary/30 text-[11px] font-mono tracking-widest">
          IDLE - AWAITING DEPLOYMENT
        </div>
      </div>
    </div>
  );
}

export function AgentGrid() {
  const activeMissionId = useWorkspaceStore((state) => state.activeMissionId);
  const { data: agents = [], isLoading } = useAgents(activeMissionId);

  if (!activeMissionId) {
    return (
      <div className="flex-1 flex items-center justify-center bg-zinc-950 font-mono text-primary/50 text-sm tracking-widest uppercase">
        SYSTEM STANDBY - SELECT MISSION TO INITIALIZE
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex-1 flex items-center justify-center bg-zinc-950 font-mono text-primary text-sm tracking-widest uppercase">
        <span className="animate-pulse">BOOTING NEURAL NETWORKS...</span>
      </div>
    );
  }

  // Always show exactly 4 panes
  const panes = [];
  for (let i = 0; i < 4; i++) {
    if (agents[i]) {
      panes.push(<AgentCard key={agents[i].id} agent={agents[i]} />);
    } else {
      panes.push(<EmptyAgentPane key={`empty-${i}`} index={i} />);
    }
  }

  return (
    <div data-testid="agent-grid-container" className="flex-1 grid grid-cols-2 grid-rows-2 gap-4 p-4 bg-black">
      {panes}
    </div>
  )
}

