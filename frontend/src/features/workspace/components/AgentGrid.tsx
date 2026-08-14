import { useState, useEffect, useRef } from 'react';
import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useAgents, useSendAgentCommand, useUpdateAgentModel } from '../../../hooks/useAgents';
import type { Agent } from '../../../types';
import { Bot, Terminal, Send, Cpu, Loader2, Sparkles } from 'lucide-react';

// Curated modern SOTA AI models for the BridgeMind Multi-Agent Matrix
export const AVAILABLE_MODELS = [
  { id: 'antigravity-agy', label: 'Antigravity AGY 2.0 (Gemini 2.5 Pro)', icon: '🔵', badge: 'DeepMind', cli: 'agy' },
  { id: 'codex-pro', label: 'OpenAI Codex Pro (o3-mini / GPT-4o)', icon: '🟢', badge: 'OpenAI', cli: 'codex' },
  { id: 'claude-3-7-sonnet', label: 'Claude 3.7 Sonnet (Thinking)', icon: '🟣', badge: 'Anthropic', cli: 'claude' },
  { id: 'claude-3-5-sonnet', label: 'Claude 3.5 Sonnet v2', icon: '🟣', badge: 'Anthropic', cli: 'claude' },
  { id: 'deepseek-r1', label: 'DeepSeek R1 Reasoning Master', icon: '🟠', badge: 'DeepSeek', cli: 'deepseek' },
  { id: 'deepseek-v3', label: 'DeepSeek V3 / V4 Coder', icon: '🟠', badge: 'DeepSeek', cli: 'deepseek' },
  { id: 'openai-o1', label: 'OpenAI o1 Reasoning', icon: '🟢', badge: 'OpenAI', cli: 'codex' },
  { id: 'gpt-4o', label: 'OpenAI GPT-4o Omni', icon: '🟢', badge: 'OpenAI', cli: 'codex' },
  { id: 'aider-pro', label: 'Aider Architect Pro (Git-Pair)', icon: '⚡', badge: 'Git-Pair', cli: 'aider' },
  { id: 'cursor-composer', label: 'Cursor AI Composer', icon: '⌨️', badge: 'Anysphere', cli: 'cursor' },
  { id: 'gemini-2-flash', label: 'Gemini 2.0 Flash (Autonomous)', icon: '🔵', badge: 'Google', cli: 'agy' },
  { id: 'custom', label: '✨ Enter Custom Model / Ollama...', icon: '✨', badge: 'Custom', cli: 'use' },
];

function AgentCard({ agent, missionId }: { agent: Agent; missionId: string }) {
  const terminalEndRef = useRef<HTMLDivElement>(null);
  const [commandInput, setCommandInput] = useState('');
  const [showCustomInput, setShowCustomInput] = useState(false);
  const [customModelInput, setCustomModelInput] = useState('');
  
  const { mutate: sendCommand, isPending: isSendingCommand } = useSendAgentCommand();
  const { mutate: updateModel } = useUpdateAgentModel();

  // Keep terminal auto-scrolled to the bottom when new agent logs arrive
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
    if (newModel === 'custom') {
      setShowCustomInput(true);
      return;
    }
    setShowCustomInput(false);
    updateModel({
      missionId,
      agentId: agent.id,
      model: newModel,
    });
  };

  const handleCustomModelSubmit = () => {
    if (!customModelInput.trim()) return;
    const modelToSet = customModelInput.trim();
    updateModel({
      missionId,
      agentId: agent.id,
      model: modelToSet,
    });
    setShowCustomInput(false);
    setCustomModelInput('');
  };

  const isRunning = agent.status === 'RUNNING' || agent.status === 'THINKING' || isSendingCommand;
  const isDone = agent.status === 'COMPLETED';
  const isFailed = agent.status === 'FAILED';

  const currentModelId = agent.model || 'antigravity-agy';
  const isPredefinedModel = AVAILABLE_MODELS.some(m => m.id === currentModelId);

  return (
    <div 
      data-testid="agent-pane"
      className="flex flex-col bg-zinc-950/95 border border-zinc-800/80 hover:border-emerald-500/50 transition-all shadow-xl rounded-lg overflow-hidden backdrop-blur-md min-h-0"
    >
      {/* 1. Agent Pane Header */}
      <div className="flex items-center justify-between gap-2 px-3 py-2 border-b border-zinc-800/80 bg-zinc-900/70 text-xs font-mono shrink-0">
        <div className="flex items-center gap-2 min-w-0">
          <span className="relative flex h-2.5 w-2.5 shrink-0">
            {isRunning && (
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            )}
            <span className={`relative inline-flex rounded-full h-2.5 w-2.5 ${
              isRunning ? 'bg-emerald-500 shadow-[0_0_8px_#10b981]' : isDone ? 'bg-teal-400' : isFailed ? 'bg-rose-500' : 'bg-zinc-600'
            }`} />
          </span>
          
          <span className="font-bold text-zinc-100 tracking-wide truncate flex items-center gap-1.5">
            <Terminal className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
            <span className="truncate">{agent.displayName || agent.role}</span>
          </span>
        </div>

        {/* Model Switcher & Custom Model Input */}
        <div className="flex items-center gap-2 shrink-0">
          {showCustomInput ? (
            <div className="flex items-center gap-1 bg-zinc-900 border border-emerald-500/70 rounded px-1.5 py-0.5">
              <input
                type="text"
                placeholder="e.g. ollama/deepseek-r1:70b..."
                value={customModelInput}
                onChange={(e) => setCustomModelInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleCustomModelSubmit()}
                className="bg-transparent text-emerald-300 text-[11px] font-mono outline-none w-36 placeholder:text-zinc-600"
                autoFocus
              />
              <button 
                onClick={handleCustomModelSubmit} 
                className="text-emerald-400 hover:text-emerald-300 text-[10px] font-bold px-1"
                title="Apply Custom Model"
              >
                SET
              </button>
              <button 
                onClick={() => setShowCustomInput(false)} 
                className="text-zinc-500 hover:text-zinc-400 text-[10px]"
              >
                ✕
              </button>
            </div>
          ) : (
            <div className="relative flex items-center">
              <Cpu className="w-3 h-3 text-zinc-500 absolute left-2 pointer-events-none" />
              <select
                value={isPredefinedModel ? currentModelId : 'custom'}
                onChange={(e) => handleModelChange(e.target.value)}
                className="bg-zinc-900 border border-zinc-700/60 text-zinc-200 text-[11px] font-mono rounded pl-6 pr-2 py-0.5 outline-none hover:border-emerald-500/60 cursor-pointer transition-colors max-w-[190px] truncate"
              >
                {!isPredefinedModel && (
                  <option value="custom">✨ {currentModelId}</option>
                )}
                {AVAILABLE_MODELS.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.icon} {m.label}
                  </option>
                ))}
              </select>
            </div>
          )}

          <span className={`px-1.5 py-0.5 rounded text-[10px] font-semibold uppercase tracking-wider font-mono ${
            isRunning 
              ? 'bg-emerald-950/90 text-emerald-400 border border-emerald-800/60' 
              : isDone 
              ? 'bg-teal-950/80 text-teal-300 border border-teal-800/60' 
              : isFailed 
              ? 'bg-rose-950/80 text-rose-400 border border-rose-800/60'
              : 'bg-zinc-900 text-zinc-500 border border-zinc-800'
          }`}>
            {agent.status}
          </span>
        </div>
      </div>

      {/* 2. Glowing Terminal Viewport */}
      <div className="flex-1 p-3 overflow-y-auto bg-black/95 font-mono text-[11px] leading-relaxed select-text min-h-0">
        <div className="text-emerald-400/95 whitespace-pre-wrap break-words font-mono">
          {agent.lastOutput ? (
            agent.lastOutput
          ) : (
            <div className="text-zinc-600 flex items-center gap-2">
              <Sparkles className="w-3.5 h-3.5 text-zinc-600 shrink-0" />
              <span>Terminal ready. Type <span className="text-emerald-400 font-semibold">agy</span>, <span className="text-emerald-400 font-semibold">codex</span>, <span className="text-emerald-400 font-semibold">claude</span>, or any prompt below...</span>
            </div>
          )}
          {isRunning && (
            <span className="animate-pulse ml-1 inline-block w-1.5 h-3.5 bg-emerald-400 align-middle shadow-[0_0_10px_#10b981]"></span>
          )}
        </div>
        <div ref={terminalEndRef} />
      </div>

      {/* 3. Fast CLI Model & Action Chips */}
      <div className="px-3 py-1 bg-zinc-950 border-t border-zinc-900/90 flex items-center gap-1.5 overflow-x-auto text-[10px] font-mono text-zinc-400 shrink-0 select-none">
        <span className="text-zinc-600 uppercase text-[9px] font-bold tracking-wider shrink-0">CLI:</span>
        <button
          onClick={() => handleSendCommand('agy')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 hover:text-sky-300 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
          title="Switch to Google Antigravity AGY 2.0"
        >
          🔵 agy
        </button>
        <button
          onClick={() => handleSendCommand('codex')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 hover:text-emerald-300 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
          title="Switch to OpenAI Codex Pro"
        >
          🟢 codex
        </button>
        <button
          onClick={() => handleSendCommand('claude')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 hover:text-purple-300 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
          title="Switch to Claude 3.7 Sonnet"
        >
          🟣 claude
        </button>
        <button
          onClick={() => handleSendCommand('deepseek')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 hover:text-amber-300 disabled:opacity-40 border border-zinc-800 rounded text-zinc-300 transition-colors whitespace-nowrap"
          title="Switch to DeepSeek R1"
        >
          🟠 deepseek
        </button>
        <button
          onClick={() => handleSendCommand('clear')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 hover:text-zinc-200 disabled:opacity-40 border border-zinc-800 rounded text-zinc-400 transition-colors whitespace-nowrap"
          title="Clear Terminal Buffer"
        >
          🧹 clear
        </button>
        <button
          onClick={() => handleSendCommand('help')}
          disabled={isRunning}
          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 hover:text-zinc-200 disabled:opacity-40 border border-zinc-800 rounded text-zinc-400 transition-colors whitespace-nowrap"
          title="CLI Help & Commands"
        >
          ❓ help
        </button>
      </div>

      {/* 4. Interactive $ command Input Prompt */}
      <div className="p-2 border-t border-zinc-800/80 bg-zinc-950 flex items-center gap-2 shrink-0">
        <span className="text-emerald-400 font-mono font-bold text-xs pl-1">$</span>
        <input
          type="text"
          value={commandInput}
          onChange={(e) => setCommandInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSendCommand()}
          placeholder={`Type 'agy', 'codex', 'use <model>', or command for ${agent.displayName || agent.role}...`}
          disabled={isRunning}
          className="flex-1 bg-zinc-900/80 border border-zinc-800 rounded px-2.5 py-1 text-xs font-mono text-zinc-200 placeholder:text-zinc-600 outline-none focus:border-emerald-500/60 transition-colors"
        />
        <button
          onClick={() => handleSendCommand()}
          disabled={isRunning || !commandInput.trim()}
          className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded text-xs font-medium flex items-center gap-1 transition-colors shadow-sm"
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
  const slotLabels = [
    'Terminal Slot 01 • Lead Architect',
    'Terminal Slot 02 • Backend Engineer',
    'Terminal Slot 03 • Frontend UI Engineer',
    'Terminal Slot 04 • QA & Security Auditor',
  ];

  return (
    <div 
      data-testid="agent-pane"
      className="flex flex-col bg-zinc-950/60 border border-zinc-900 rounded-lg overflow-hidden opacity-75 backdrop-blur-sm min-h-0"
    >
      <div className="flex items-center gap-2 px-3 py-2 border-b border-zinc-900 bg-zinc-900/40 text-xs font-mono text-zinc-500 uppercase tracking-wider shrink-0">
        <span className="w-2 h-2 rounded-full bg-zinc-700" />
        <span className="font-bold">{slotLabels[index] || `TERMINAL SLOT 0${index + 1}`}</span>
      </div>
      <div className="flex-1 flex flex-col items-center justify-center p-6 text-center">
        <div className="w-10 h-10 rounded-xl bg-zinc-900/80 border border-zinc-800 flex items-center justify-center mb-2">
          <Bot className="w-5 h-5 text-zinc-600" />
        </div>
        <div className="text-zinc-400 text-xs font-mono tracking-wider uppercase font-semibold">
          STANDBY — AWAITING AGENT ALLOCATION
        </div>
        <p className="text-zinc-600 text-[11px] font-mono mt-1 max-w-xs">
          Launch a mission or send instructions to activate this multi-agent matrix slot.
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
      <div className="flex-1 flex flex-col items-center justify-center bg-zinc-950 font-mono p-8 text-center select-none">
        <div className="w-16 h-16 rounded-2xl bg-zinc-900 border border-zinc-800 flex items-center justify-center mb-4 shadow-inner">
          <Terminal className="w-8 h-8 text-emerald-400" />
        </div>
        <h3 className="text-zinc-200 font-bold text-base tracking-wider uppercase mb-1">
          BridgeMind Multi-Agent Matrix
        </h3>
        <p className="text-zinc-500 text-xs max-w-md mb-6 leading-relaxed">
          Type your project prompt in the top command bar and select your execution mode (<span className="text-emerald-400 font-semibold">⚡ Isolated</span> for raw speed or <span className="text-sky-400 font-semibold">🤝 Collaborative</span> for shared memory).
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

  // Guarantee exactly 4 panes in the 2x2 multi-agent matrix
  const panes = [];
  for (let i = 0; i < 4; i++) {
    if (agents[i]) {
      panes.push(<AgentCard key={agents[i].id} agent={agents[i]} missionId={activeMissionId} />);
    } else {
      panes.push(<EmptyAgentPane key={`empty-${i}`} index={i} />);
    }
  }

  return (
    <div 
      data-testid="agent-grid-container" 
      className="flex-1 grid grid-cols-2 grid-rows-2 gap-3 p-3 bg-zinc-950 min-h-0 overflow-hidden"
    >
      {panes}
    </div>
  );
}
