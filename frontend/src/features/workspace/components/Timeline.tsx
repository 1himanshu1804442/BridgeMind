import { useWorkspaceStore } from '../../../store/workspaceStore';
import { useTimeline } from '../../../hooks/useTimeline';
import { useRef, useState } from 'react';
import { 
  Activity, 
  Terminal, 
  Bot, 
  Flag, 
  CheckCircle2, 
  AlertCircle, 
  Clock, 
  ChevronRight,
  ChevronLeft
} from 'lucide-react';
import { TimelineEntry } from '../../../types';

const getEventIcon = (eventType: string) => {
  const type = eventType.toUpperCase();
  if (type.includes('MISSION')) return <Flag className="w-4 h-4 text-emerald-400" />;
  if (type.includes('AGENT')) return <Bot className="w-4 h-4 text-indigo-400" />;
  if (type.includes('TOOL')) return <Terminal className="w-4 h-4 text-amber-400" />;
  if (type.includes('FAIL') || type.includes('ERROR')) return <AlertCircle className="w-4 h-4 text-rose-400" />;
  if (type.includes('COMPLET') || type.includes('SUCCESS')) return <CheckCircle2 className="w-4 h-4 text-emerald-500" />;
  return <Activity className="w-4 h-4 text-zinc-400" />;
};

export function Timeline() {
  const activeWorkspaceId = useWorkspaceStore((state) => state.activeWorkspaceId);
  const { data: timelineEntries, isLoading } = useTimeline(activeWorkspaceId);
  const [isOpen, setIsOpen] = useState(true);
  const scrollRef = useRef<HTMLDivElement>(null);

  if (!activeWorkspaceId) {
    return null;
  }

  return (
    <div 
      className={`
        border-l border-zinc-800/40 bg-zinc-950/50 flex flex-col transition-all duration-300 ease-in-out
        ${isOpen ? 'w-80' : 'w-12'}
      `}
    >
      <div className="h-12 border-b border-zinc-800/40 flex items-center justify-between px-3 bg-zinc-900/20 shrink-0">
        {isOpen && (
          <div className="flex items-center gap-2 text-xs font-medium text-zinc-400 tracking-wide uppercase">
            <Clock className="w-3.5 h-3.5" />
            Workspace Timeline
          </div>
        )}
        <button 
          onClick={() => setIsOpen(!isOpen)}
          className="p-1 hover:bg-zinc-800 rounded text-zinc-500 hover:text-zinc-300 transition-colors"
          title={isOpen ? "Collapse Timeline" : "Expand Timeline"}
        >
          {isOpen ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
        </button>
      </div>

      <div 
        ref={scrollRef}
        className={`flex-1 overflow-y-auto overflow-x-hidden p-4 ${!isOpen && 'hidden'}`}
      >
        {isLoading ? (
          <div className="flex items-center justify-center h-full text-sm text-zinc-500 animate-pulse">
            Loading timeline...
          </div>
        ) : timelineEntries && timelineEntries.length > 0 ? (
          <div className="relative border-l border-zinc-800/60 ml-2 space-y-6 pb-4">
            {timelineEntries.map((entry: TimelineEntry, idx: number) => (
              <div 
                key={entry.id} 
                className="relative pl-6 group animate-in slide-in-from-right-4 fade-in duration-300 ease-out fill-mode-both"
                style={{ animationDelay: `${Math.min(idx * 50, 500)}ms` }}
              >
                {/* Timeline Dot / Icon */}
                <div className="absolute -left-3 top-0.5 p-1 bg-zinc-950 border border-zinc-800 rounded-full group-hover:border-zinc-700 transition-colors group-hover:scale-110 duration-200">
                  {getEventIcon(entry.eventType)}
                </div>

                <div className="flex flex-col gap-1">
                  <div className="flex items-baseline justify-between gap-2">
                    <span className="text-sm font-medium text-zinc-200">
                      {entry.summary}
                    </span>
                    <span className="text-[10px] text-zinc-500 font-mono shrink-0">
                      {new Date(entry.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
                    </span>
                  </div>
                  
                  <div className="flex items-center gap-1.5 text-xs text-zinc-500">
                    <span className="font-medium text-zinc-400">{entry.actorName}</span>
                    <span className="text-[10px] px-1.5 py-0.5 rounded-full bg-zinc-900 border border-zinc-800/60 uppercase tracking-wider">
                      {entry.actorType}
                    </span>
                  </div>

                  {entry.details && (
                    <div className="mt-1.5 text-xs text-zinc-400 bg-zinc-900/50 p-2 rounded border border-zinc-800/40 font-mono break-all line-clamp-3 hover:line-clamp-none transition-all">
                      {entry.details}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-full text-center gap-2 text-zinc-500">
            <Clock className="w-8 h-8 text-zinc-800" />
            <p className="text-sm">No events recorded yet</p>
            <p className="text-xs text-zinc-600">Events will appear here as they happen.</p>
          </div>
        )}
      </div>
    </div>
  );
}
