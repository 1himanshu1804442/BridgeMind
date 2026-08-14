import { useState, useEffect, useRef } from 'react';
import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useAgents, useSendAgentCommand, useUpdateAgentModel } from '../../../hooks/useAgents';
import type { Agent } from '../../../types';
import { Bot, Terminal, Send, Cpu, Zap, CheckCircle2, Loader2, Sparkles } from 'lucide-react';

const AVAILABLE_MODELS = [
  { id: 'claude-code', label: 'Claude Code 3.5 Sonnet', icon: '🟣', badge: 'Anthropic' },
  { id: 'codex', label: 'OpenAI Codex (GPT-4o)', icon: '🟢', badge: 'OpenAI' },
  { id: 'antigravity-agy', label: 'Antigravity AGY Engine', icon: '🔵', badge: 'Google DeepMind' },
  { id: 'aider', label: 'Aider Multi-File Architect', icon: '🟠', badge: 'Git-Pair' },
  { id: 'gemini-1.5-pro', label: 'Gemini 1.5 Pro Experimental', icon: '✨', badge: 'Google' },
];

function AgentCard({ agent, missionId }: { agent: Agent; missionId: string }) {
  const terminalEndRef = useRef<HTMLDivElement>(null);
  const [commandInput, setCommandInput] = useState('');
  
  const { mutate: sendCommand, isPending: isSendingCommand } = useSendAgentCommand();
  const { mutate: updateModel } = useUpdateAgentModel();

  useEffect(() => {
    if (terminalEndRef.current?.scrollIntoView) {
      terminalEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [agent.lastOutput]);

  const handleSendCommand = (cmd?: string) => {
    const textToSend = cmd || commandInput;
    if (!textToSend.trim() || isSendingCommand) return;
    
    sendCommand({
      missionId,
      agentId: agent.id,
      command: textToSend.trim(),
    });
    setCommandInput('');
  };

  const handleModelChange = (newModel: string) => {
    updateModel({
      missionId,
      agentId: agent.id,
      model: newModel,
    });
  };

  const isRunning = agent.status === 'RUNNING' || agent.status === 'THINKING' || isSendingCommand;
  const isDone = agent.status === 'COMPLETED';

  return (
    <div 
      data-testid="agent-pane"
      className="flex flex-col bg-zinc-950/90 border border-zinc-800/80 hover:border-emerald-500/40 transition-colors shadow-2xl rounded-lg overflow-hidden backdrop-blur-md"
    >
      {/* Pane Header */}
      <div className="flex items-center justify-between gap-2 px-3 py-2 border-b border-zinc-800/80 bg-zinc-900/60 text-xs font-mono">
        <div className="flex items-center gap-2 min-w-0">
          <span className={`relative flex h-2.5 w-2.5`}>
            {isRunning && (
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            )}
            <span className={`relative inline-flex rounded-full h-2.5 w-2.5 ${
              isRunning ? 'bg-emerald-500' : isDone ? 'bg-teal-400' : 'bg-zinc-600'
            }`} />
          </span>
          
          <span className="font-bold text-zinc-100 tracking-wide truncate flex items-center gap-1.5">
            <Terminal className="w-3.5 h-3.5 text-emerald-400" />
            {agent.displayName || agent.role}
          </span>
        </div>

        {/* Model Switcher Dropdown */}
        <div className="flex items-center gap-2">
          <div className="relative flex items-center">
            <Cpu className="w-3 h-3 text-zinc-500 absolute left-2 pointer-events-none" />
            <select
              value={agent.model || 'claude-code'}
              onChange={(e) => handleModelChange(e.target.value)}
              className="bg-zinc-900 border border-zinc-700/60 text-zinc-200 text-[11px] font-mono rounded pl-6 pr-2 py-0.5 outline-none hover:border-zinc-500 cursor-pointer transition-colors"
            >
              {AVAILABLE_MODELS.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.icon} {m.label}
                </option>
              ))}
            </select>
          </div>

          <span className={`px-1.5 py-0.5 rounded text-[10px] font-semibold uppercase tracking-wider ${
            isRunning 
              ? 'bg-emerald-950/80 text-emerald-400 border border-emerald-800/60' 
              : isDone 
              ? 'bg-zinc-800 text-zinc-300 border border-zinc-700/60' 
              : 'bg-zinc-900 text-zinc-500'
          }`}>
            {agent.status}
          </span>
        </div>
      </div>

      {/* Terminal Viewport */}
      <div className="flex-1 p-3 overflow-y-auto bg-black/90 font-mono text-[11px] leading-relaxed select-text">
        <div className="text-emerald-400/90 whitespace-pre-wrap break-words font-mono">
          {agent.lastOutput ? (
            agent.lastOutput
          ) : (
            <div className="text-zinc-600 flex items-center gap-2">
              <Sparkles className="w-3.5 h-3.5 text-zinc-600" />
              <span>Terminal ready. Type a command or instruction below...</span>
            </div>
          )}
          {isRunning && (
            <span className="animate-pulse ml-1 inline-block w-1.5 h-3 bg-emerald-400 align-middle shadow-[0_0_8px_#34d399]"></span>
          )}
        </div>
        <div ref={terminalEndRef} />
      </div>

      {/* Quick Action Badges */}
      <div className="px-3 py-1 bg-zinc-950 border-t border-zinc-900 flex items-center gap-1.5 overflow-x-auto text-[10px] font-mono text-zinc-400">
        <span className="text-zinc-600 uppercase text-[9px] font-bold tracking-wider">Quick:</span>
        <button
          onClick={() => handleSendCommand('Generate full component implementation')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
        >
          ⚡ Implement
        </button>
        <button
          onClick={() => handleSendCommand('Run tests & verify correctness')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
        >
          🧪 Test
        </button>
        <button
          onClick={() => handleSendCommand('Review code and generate Git diff')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
        >
          🔍 Review Diff
        </button>
        <button
          onClick={() => handleSendCommand('Optimize performance and refactor')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
        >
          🚀 Optimize
        </button>
      </div>

      {/* Interactive Command Input Prompt */}
      <div className="p-2 border-t border-zinc-800/80 bg-zinc-950 flex items-center gap-2">
        <span className="text-emerald-400 font-mono font-bold text-xs pl-1">$</span>
        <input
          type="text"
          value={commandInput}
          onChange={(e) => setCommandInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSendCommand()}
          placeholder={`Command ${agent.displayName || agent.role}...`}
          disabled={isRunning}
          className="flex-1 bg-zinc-900/80 border border-zinc-800 rounded px-2.5 py-1 text-xs font-mono text-zinc-200 placeholder:text-zinc-600 outline-none focus:border-emerald-500/60 transition-colors"
        />
        <button
          onClick={() => handleSendCommand()}
          disabled={isRunning || !commandInput.trim()}
          className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded text-xs font-medium flex items-center gap-1 transition-colors"
        >
          {isRunning ? (
            <Loader2 className="w-3 h-3 animate-spin" />
          ) : (
            <Send className="w-3 h-3" />
          )}
        </button>
      </div>
    </div>
  );
}

function EmptyAgentPane({ index }: { index: number }) {
  return (
    <div 
      data-testid="agent-pane"
      className="flex flex-col bg-zinc-950/60 border border-zinc-900 rounded-lg overflow-hidden opacity-60 backdrop-blur-sm"
    >
      <div className="flex items-center gap-2 px-3 py-2 border-b border-zinc-900 bg-zinc-900/30 text-xs font-mono text-zinc-500 uppercase tracking-wider">
        <span className="w-2 h-2 rounded-full bg-zinc-700" />
        <span className="font-bold">TERMINAL SLOT 0{index + 1}</span>
      </div>
      <div className="flex-1 flex flex-col items-center justify-center p-6 text-center">
        <Bot className="w-8 h-8 text-zinc-700 mb-2" />
        <div className="text-zinc-500 text-xs font-mono tracking-wider uppercase">
          STANDBY — AWAITING AGENT ALLOCATION
        </div>
        <p className="text-zinc-600 text-[11px] font-mono mt-1">
          Launch a mission to activate this terminal slot.
        </p>
      </div>
    </div>
  );
}

export function AgentGrid() {
  const activeMissionId = useWorkspaceStore((state) => state.activeMissionId);
  const { data: agents = [], isLoading } = useAgents(activeMissionId);

  if (!activeMissionId) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center bg-zinc-950 font-mono p-8 text-center">
        <div className="w-16 h-16 rounded-2xl bg-zinc-900 border border-zinc-800 flex items-center justify-center mb-4 shadow-inner">
          <Terminal className="w-8 h-8 text-emerald-400" />
        </div>
        <h3 className="text-zinc-200 font-bold text-base tracking-wider uppercase mb-1">
          Mission Control Terminal System
        </h3>
        <p className="text-zinc-500 text-xs max-w-md mb-6 leading-relaxed">
          Type your project prompt in the top command bar and select your execution mode (<span className="text-emerald-400">⚡ Isolated</span> for raw speed or <span className="text-sky-400">🤝 Collaborative</span> for shared memory).
        </p>
      </div>
    );
  }

  if (isLoading && agents.length === 0) {
    return (
      <div className="flex-1 flex items-center justify-center bg-zinc-950 font-mono text-emerald-400 text-sm tracking-widest uppercase">
        <Loader2 className="w-5 h-5 animate-spin mr-2" />
        <span>INITIALIZING INTERACTIVE TERMINAL MULTIPLEXER...</span>
      </div>
    );
  }

  const panes = [];
  for (let i = 0; i < 4; i++) {
    if (agents[i]) {
      panes.push(<AgentCard key={agents[i].id} agent={agents[i]} missionId={activeMissionId} />);
    } else {
      panes.push(<EmptyAgentPane key={`empty-${i}`} index={i} />);
    }
  }

  return (
    <div data-testid="agent-grid-container" className="flex-1 grid grid-cols-2 grid-rows-2 gap-3.5 p-3.5 bg-zinc-950 min-h-0 overflow-hidden">
      {panes}
    </div>
  );
}
