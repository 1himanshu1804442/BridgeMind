import { Sidebar } from './Sidebar'
import { AgentGrid } from './AgentGrid'

export function WorkspaceLayout() {
  return (
    <div className="flex h-screen w-screen bg-zinc-950 text-zinc-300 font-sans overflow-hidden selection:bg-zinc-800">
      <Sidebar />

      <div className="flex-1 flex flex-col min-w-0">
        {/* Mission Bar */}
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
                placeholder="Build OAuth..." 
                className="bg-transparent border-none outline-none flex-1 text-zinc-200 placeholder:text-zinc-600"
              />
              <button className="px-3 py-0.5 bg-zinc-800 hover:bg-zinc-700 text-zinc-300 rounded text-xs transition-colors ml-2 font-medium">
                Launch
              </button>
            </div>
          </div>
        </header>

        {/* Main Content Area */}
        <main className="flex-1 flex flex-col min-h-0">
          {/* Agent Grid (70% height approx if we have a bottom pane, but let's make it flex-1) */}
          <AgentGrid />

          {/* Memory Inspector / Bottom Pane (30% height approx) */}
          <div className="h-1/3 min-h-[250px] border-t border-zinc-800/40 bg-zinc-950 flex flex-col">
            <div className="h-9 border-b border-zinc-800/40 flex items-center px-4 bg-zinc-900/20">
              <h2 className="text-xs font-medium text-zinc-400 tracking-wide uppercase">Memory Inspector</h2>
            </div>
            <div data-testid="memory-inspector" className="flex-1 overflow-y-auto p-4">
              <ul className="space-y-2 text-sm text-zinc-400 font-mono list-disc list-inside">
                <li>User requested OAuth integration with Google and GitHub.</li>
                <li>Backend team is currently provisioning the Supabase auth schema.</li>
                <li>Frontend team is awaiting client IDs to build the sign-in modal.</li>
                <li>Identified potential scope creep in social graph syncing (deferred to Phase 2).</li>
              </ul>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}
