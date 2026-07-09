import { 
  LayoutDashboard, 
  Files, 
  Users, 
  Crosshair, 
  BrainCircuit, 
  GitBranch, 
  Clock, 
  Settings 
} from 'lucide-react'

export function Sidebar() {
  return (
    <nav 
      aria-label="primary sidebar"
      className="w-14 flex flex-col items-center py-4 bg-zinc-950 border-r border-zinc-800/40 text-zinc-400 gap-6"
    >
      <button aria-label="Workspace" className="hover:text-zinc-100 transition-colors">
        <LayoutDashboard size={20} strokeWidth={1.5} />
      </button>
      <button aria-label="Files" className="hover:text-zinc-100 transition-colors">
        <Files size={20} strokeWidth={1.5} />
      </button>
      <button aria-label="Agents" className="hover:text-zinc-100 transition-colors">
        <Users size={20} strokeWidth={1.5} />
      </button>
      <button aria-label="Missions" className="hover:text-zinc-100 transition-colors">
        <Crosshair size={20} strokeWidth={1.5} />
      </button>
      <button aria-label="Memory" className="hover:text-zinc-100 transition-colors">
        <BrainCircuit size={20} strokeWidth={1.5} />
      </button>
      <button aria-label="Git" className="hover:text-zinc-100 transition-colors">
        <GitBranch size={20} strokeWidth={1.5} />
      </button>
      <button aria-label="Timeline" className="hover:text-zinc-100 transition-colors">
        <Clock size={20} strokeWidth={1.5} />
      </button>
      
      <div className="flex-1" />
      
      <button aria-label="Settings" className="hover:text-zinc-100 transition-colors">
        <Settings size={20} strokeWidth={1.5} />
      </button>
    </nav>
  )
}
