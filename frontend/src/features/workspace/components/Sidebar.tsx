import { 
  LayoutDashboard, 
  Files, 
  Users, 
  Crosshair, 
  BrainCircuit, 
  GitBranch, 
  Clock, 
  Settings 
} from 'lucide-react';
import { useWorkspaceStore } from '../../../store/workspaceStore';

export function Sidebar() {
  const sidebarPanel = useWorkspaceStore((state) => state.sidebarPanel);
  const setSidebarPanel = useWorkspaceStore((state) => state.setSidebarPanel);

  return (
    <nav 
      aria-label="primary sidebar"
      className="w-14 flex flex-col items-center py-3 bg-zinc-950 border-r border-zinc-800/80 text-zinc-400 gap-4 shrink-0 z-20 select-none"
    >
      {/* Brand logo pill */}
      <div className="w-8 h-8 rounded-lg bg-emerald-950/80 border border-emerald-500/40 flex items-center justify-center text-emerald-400 font-bold text-sm shadow-[0_0_10px_rgba(16,185,129,0.2)] mb-1">
        BM
      </div>

      <button 
        aria-label="Workspace" 
        onClick={() => setSidebarPanel('explorer')}
        title="Workspace Overview"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'explorer' 
            ? 'bg-zinc-900 text-emerald-400 border border-emerald-500/40 shadow-sm' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <LayoutDashboard size={18} strokeWidth={1.75} />
      </button>

      <button 
        aria-label="Files" 
        onClick={() => setSidebarPanel('explorer')}
        title="File Tree Explorer"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'explorer' 
            ? 'bg-zinc-900 text-emerald-400 border border-emerald-500/40 shadow-sm' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <Files size={18} strokeWidth={1.75} />
      </button>

      <button 
        aria-label="Agents" 
        onClick={() => setSidebarPanel('agents')}
        title="Agent Swarm & Terminal Multiplexer"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'agents' 
            ? 'bg-zinc-900 text-sky-400 border border-sky-500/40 shadow-sm' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <Users size={18} strokeWidth={1.75} />
      </button>

      <button 
        aria-label="Missions" 
        onClick={() => setSidebarPanel('agents')}
        title="Mission Objectives"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'agents' 
            ? 'bg-zinc-900 text-indigo-400 border border-indigo-500/40 shadow-sm' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <Crosshair size={18} strokeWidth={1.75} />
      </button>

      <button 
        aria-label="Memory" 
        onClick={() => setSidebarPanel('memory')}
        title="Memory Inspector (Redis / Postgres Vectors)"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'memory' 
            ? 'bg-zinc-900 text-purple-400 border border-purple-500/40 shadow-sm' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <BrainCircuit size={18} strokeWidth={1.75} />
      </button>

      <button 
        aria-label="Git" 
        onClick={() => setSidebarPanel('git')}
        title="Git Diffs & Human-in-the-Loop Review"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'git' 
            ? 'bg-zinc-900 text-amber-400 border border-amber-500/40 shadow-sm' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <GitBranch size={18} strokeWidth={1.75} />
      </button>

      <button 
        aria-label="Timeline" 
        onClick={() => setSidebarPanel('timeline')}
        title="Workspace Event Bus Timeline"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'timeline' 
            ? 'bg-zinc-900 text-emerald-400 border border-emerald-500/40 shadow-sm' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <Clock size={18} strokeWidth={1.75} />
      </button>
      
      <div className="flex-1" />
      
      <button 
        aria-label="Settings" 
        onClick={() => setSidebarPanel('settings')}
        title="Workspace Settings"
        className={`p-2.5 rounded-lg transition-all ${
          sidebarPanel === 'settings' 
            ? 'bg-zinc-900 text-zinc-100 border border-zinc-700' 
            : 'hover:text-zinc-100 hover:bg-zinc-900/60'
        }`}
      >
        <Settings size={18} strokeWidth={1.75} />
      </button>
    </nav>
  );
}
