export function WorkspaceLayout() {
  return (
    <div className="flex flex-col h-screen bg-background text-foreground overflow-hidden">
      <header role="banner" aria-label="Workspace Header" className="h-14 border-b flex items-center px-4 bg-card">
        <h1 className="text-lg font-semibold">BridgeMind Mission Control</h1>
      </header>

      <div className="flex-1 flex overflow-hidden">
        {/* Left Sidebar - Agents & Mission */}
        <aside className="w-80 border-r flex flex-col bg-muted/20">
          <div data-testid="mission-bar" className="p-4 border-b min-h-[150px]">
            <h2 className="text-sm font-medium mb-2">Current Mission</h2>
            {/* Mission status will go here */}
          </div>
          <div data-testid="agent-grid" className="p-4 flex-1 overflow-y-auto">
            <h2 className="text-sm font-medium mb-2">Agent Grid</h2>
            {/* Agent statuses will go here */}
          </div>
        </aside>

        {/* Main Content Area - Diff & Timeline */}
        <main className="flex-1 flex flex-col min-w-0">
          <div className="flex-1 flex flex-col lg:flex-row min-h-0">
            {/* Git Diff View */}
            <section data-testid="git-diff" className="flex-1 border-r flex flex-col">
              <div className="h-10 border-b flex items-center px-4 bg-muted/10">
                <h2 className="text-sm font-medium">Git Diff</h2>
              </div>
              <div className="flex-1 overflow-y-auto p-4">
                {/* Diff content will go here */}
              </div>
            </section>

            {/* Timeline View */}
            <section data-testid="timeline" className="w-full lg:w-96 flex flex-col border-b lg:border-b-0">
              <div className="h-10 border-b flex items-center px-4 bg-muted/10">
                <h2 className="text-sm font-medium">Timeline</h2>
              </div>
              <div className="flex-1 overflow-y-auto p-4">
                {/* Timeline events will go here */}
              </div>
            </section>
          </div>

          {/* Bottom Panel - Logs & Memory */}
          <div className="h-64 border-t flex">
            <section data-testid="memory-inspector" className="w-1/2 border-r flex flex-col">
              <div className="h-10 border-b flex items-center px-4 bg-muted/10">
                <h2 className="text-sm font-medium">Memory Inspector</h2>
              </div>
              <div className="flex-1 overflow-y-auto p-4">
                {/* Memory content will go here */}
              </div>
            </section>
            <section data-testid="logs-panel" className="w-1/2 flex flex-col">
              <div className="h-10 border-b flex items-center px-4 bg-muted/10">
                <h2 className="text-sm font-medium">Logs</h2>
              </div>
              <div className="flex-1 overflow-y-auto p-4">
                {/* Logs will go here */}
              </div>
            </section>
          </div>
        </main>
      </div>
    </div>
  )
}
