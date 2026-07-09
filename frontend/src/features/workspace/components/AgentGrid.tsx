export function AgentGrid() {
  const agents = [
    { id: 1, role: 'Backend Engineer', model: 'Claude Opus', status: 'Thinking...', cost: '$0.42', elapsed: '1m 12s', color: 'bg-emerald-500' },
    { id: 2, role: 'Frontend Engineer', model: 'GPT-4o', status: 'Writing code', cost: '$0.15', elapsed: '45s', color: 'bg-emerald-500' },
    { id: 3, role: 'QA Tester', model: 'Claude Haiku', status: 'Idle', cost: '$0.01', elapsed: '0s', color: 'bg-zinc-500' },
    { id: 4, role: 'Architect', model: 'Claude Opus', status: 'Reviewing', cost: '$0.89', elapsed: '3m 05s', color: 'bg-amber-500' },
  ]

  return (
    <div data-testid="agent-grid-container" className="flex-1 grid grid-cols-2 grid-rows-2 gap-4 p-4 bg-zinc-950">
      {agents.map((agent) => (
        <div 
          key={agent.id} 
          data-testid="agent-pane"
          className="flex flex-col bg-zinc-900/50 border border-zinc-800/50 rounded-lg overflow-hidden"
        >
          {/* Header */}
          <div className="flex items-center gap-2 px-3 py-2 border-b border-zinc-800/50 bg-zinc-900/80 text-xs font-mono text-zinc-400">
            <span className={`w-2 h-2 rounded-full ${agent.color}`} />
            <span className="font-semibold text-zinc-300">{agent.role}</span>
            <span className="text-zinc-600">|</span>
            <span>{agent.model}</span>
            <span className="text-zinc-600">|</span>
            <span>{agent.status}</span>
            <span className="flex-1" />
            <span>{agent.cost}</span>
            <span className="text-zinc-600">|</span>
            <span>{agent.elapsed}</span>
          </div>
          
          {/* Content Area */}
          <div className="flex-1 p-4 overflow-y-auto">
            <div className="text-zinc-500 text-sm font-mono leading-relaxed">
              {/* Terminal / Code output simulation */}
              <p>{`> Initializing environment for ${agent.role}...`}</p>
              {agent.status === 'Thinking...' && <p className="animate-pulse">{`> Processing context...`}</p>}
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}
