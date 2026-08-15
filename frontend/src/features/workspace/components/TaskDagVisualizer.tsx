import React from 'react';
import { 
  CheckCircle2, 
  CircleDot, 
  Clock, 
  AlertCircle, 
  ArrowRight,
  GitPullRequest,
  Cpu,
  Boxes,
  ShieldAlert,
  Code2,
  Loader2
} from 'lucide-react';
import { useMissionTasks } from '../../../hooks/useMissionTasks';
import type { Agent, TaskStatus, AgentRole } from '../../../types';

interface TaskDagVisualizerProps {
  missionId: string | null;
  agents: Agent[];
  selectedAgentId: string | null;
  onSelectAgent: (agentId: string) => void;
}

const getRoleIcon = (role: AgentRole) => {
  switch (role) {
    case 'COORDINATOR':
    case 'ARCHITECT':
    case 'PLANNER':
      return <Boxes className="w-3.5 h-3.5 text-indigo-400 shrink-0" />;
    case 'BUILDER':
    case 'BACKEND_ENGINEER':
    case 'FRONTEND_ENGINEER':
      return <Code2 className="w-3.5 h-3.5 text-emerald-400 shrink-0" />;
    case 'SCOUT':
    case 'QA_ENGINEER':
      return <Cpu className="w-3.5 h-3.5 text-sky-400 shrink-0" />;
    case 'REVIEWER':
    case 'SECURITY_AUDITOR':
      return <ShieldAlert className="w-3.5 h-3.5 text-amber-400 shrink-0" />;
    default:
      return <Boxes className="w-3.5 h-3.5 text-zinc-400 shrink-0" />;
  }
};

const getStatusBadge = (status: TaskStatus) => {
  switch (status) {
    case 'COMPLETED':
      return (
        <span className="flex items-center gap-1 text-[10px] text-emerald-400 font-mono font-medium">
          <CheckCircle2 className="w-3 h-3" /> Done
        </span>
      );
    case 'IN_PROGRESS':
      return (
        <span className="flex items-center gap-1 text-[10px] text-sky-400 font-mono font-medium">
          <Loader2 className="w-3 h-3 animate-spin text-sky-400" /> Active
        </span>
      );
    case 'FAILED':
      return (
        <span className="flex items-center gap-1 text-[10px] text-rose-400 font-mono font-medium">
          <AlertCircle className="w-3 h-3" /> Failed
        </span>
      );
    default:
      return (
        <span className="flex items-center gap-1 text-[10px] text-zinc-500 font-mono">
          <Clock className="w-3 h-3" /> Queued
        </span>
      );
  }
};

export function TaskDagVisualizer({
  missionId,
  agents,
  selectedAgentId,
  onSelectAgent
}: TaskDagVisualizerProps) {
  const { data: tasks = [] } = useMissionTasks(missionId);

  // If no backend tasks exist yet, derive visual nodes from active agents
  const displayNodes: Array<{
    id: string;
    title: string;
    role: AgentRole;
    status: TaskStatus;
    agentId?: string;
  }> = tasks.length > 0
    ? tasks.map(t => {
        const matchingAgent = agents.find(a => a.role === t.assignedRole);
        return {
          id: t.id,
          title: t.title,
          role: t.assignedRole,
          status: t.status,
          agentId: matchingAgent?.id
        };
      })
    : agents.map(a => ({
        id: a.id,
        title: a.displayName,
        role: a.role,
        status: a.status === 'COMPLETED' ? 'COMPLETED' : (a.status === 'FAILED' ? 'FAILED' : 'IN_PROGRESS'),
        agentId: a.id
      }));

  if (displayNodes.length === 0) {
    return null;
  }

  return (
    <div className="border-b border-zinc-800/80 bg-zinc-950/70 px-4 py-2 flex items-center gap-2 overflow-x-auto select-none shrink-0 scrollbar-none">
      <div className="flex items-center gap-1.5 text-zinc-500 text-[11px] font-mono font-semibold uppercase tracking-wider mr-2 shrink-0">
        <GitPullRequest className="w-3.5 h-3.5 text-emerald-400" />
        <span>Swarm Topology</span>
      </div>

      <div className="flex items-center gap-2 min-w-max">
        {displayNodes.map((node, index) => {
          const isSelected = node.agentId && selectedAgentId === node.agentId;
          const isLast = index === displayNodes.length - 1;

          return (
            <React.Fragment key={node.id}>
              <button
                onClick={() => node.agentId && onSelectAgent(node.agentId)}
                className={`flex items-center gap-2 px-3 py-1.5 rounded-lg border transition-all text-left ${
                  isSelected
                    ? 'bg-zinc-900 border-emerald-500/70 shadow-[0_0_12px_rgba(16,185,129,0.15)] ring-1 ring-emerald-500/30'
                    : 'bg-zinc-900/60 border-zinc-800/90 hover:border-zinc-700 hover:bg-zinc-900'
                }`}
              >
                {getRoleIcon(node.role)}
                <div className="flex flex-col min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-medium text-zinc-200 truncate max-w-[130px] font-mono">
                      {node.role.replace('_', ' ')}
                    </span>
                    {getStatusBadge(node.status)}
                  </div>
                  <span className="text-[10px] text-zinc-500 truncate max-w-[140px]">
                    {node.title}
                  </span>
                </div>
              </button>

              {!isLast && (
                <ArrowRight className="w-3.5 h-3.5 text-zinc-600 shrink-0" />
              )}
            </React.Fragment>
          );
        })}
      </div>
    </div>
  );
}
