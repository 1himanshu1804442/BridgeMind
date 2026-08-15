import { useState } from 'react';
import { 
  GitCommit, 
  GitBranch, 
  CheckCircle2, 
  RefreshCw, 
  Check, 
  FileDiff
} from 'lucide-react';
import { useGitDiff, useApproveGitDiff } from '../../../hooks/useGitReview';

interface GitDiffReviewProps {
  workspaceId: string | null;
}

export function GitDiffReview({ workspaceId }: GitDiffReviewProps) {
  const { data, isLoading, refetch, isRefetching } = useGitDiff(workspaceId);
  const { mutate: approveDiff, isPending: isApproving, isSuccess } = useApproveGitDiff();
  const [commitMessage, setCommitMessage] = useState('feat: autonomous multi-agent swarm updates');

  const diffText = data?.diff || '';

  const handleApprove = () => {
    if (!workspaceId || !commitMessage.trim()) return;
    approveDiff({
      workspaceId,
      executionId: '00000000-0000-0000-0000-000000000000',
      message: commitMessage.trim()
    });
  };

  const renderDiffLines = (rawDiff: string) => {
    if (!rawDiff.trim()) {
      return (
        <div className="flex flex-col items-center justify-center p-12 text-center text-zinc-500 font-mono">
          <CheckCircle2 className="w-8 h-8 text-emerald-500 mb-2 opacity-80" />
          <span className="text-sm text-zinc-300 font-medium">Working tree is clean</span>
          <span className="text-xs text-zinc-600 mt-1">No uncommitted Git changes detected in workspace.</span>
        </div>
      );
    }

    const lines = rawDiff.split('\n');
    return (
      <div className="font-mono text-[11px] leading-relaxed select-text">
        {lines.map((line, idx) => {
          let lineStyle = 'text-zinc-400 hover:bg-zinc-900/40 px-3';
          let icon = null;

          if (line.startsWith('diff --git') || line.startsWith('index ')) {
            lineStyle = 'bg-zinc-900 text-zinc-300 font-semibold px-3 py-1 border-t border-zinc-800 mt-2';
            icon = <FileDiff className="w-3 h-3 text-sky-400 inline mr-1.5" />;
          } else if (line.startsWith('+++') || line.startsWith('---')) {
            lineStyle = 'text-zinc-500 font-bold px-3';
          } else if (line.startsWith('@@')) {
            lineStyle = 'bg-sky-950/40 text-sky-300 font-bold px-3 py-0.5 my-0.5 border-y border-sky-800/30';
          } else if (line.startsWith('+')) {
            lineStyle = 'bg-emerald-950/40 text-emerald-300 px-3 border-l-2 border-emerald-500';
          } else if (line.startsWith('-')) {
            lineStyle = 'bg-rose-950/40 text-rose-300 px-3 border-l-2 border-rose-500';
          }

          return (
            <div key={idx} className={`flex items-start font-mono ${lineStyle}`}>
              <span className="w-8 text-[10px] text-zinc-600 select-none shrink-0 text-right pr-3">
                {idx + 1}
              </span>
              <span className="flex-1 whitespace-pre-wrap break-all">
                {icon}
                {line}
              </span>
            </div>
          );
        })}
      </div>
    );
  };

  return (
    <div className="flex flex-col h-full bg-zinc-950 text-zinc-300 font-sans overflow-hidden">
      {/* Action Header */}
      <div className="p-3 border-b border-zinc-800/80 bg-zinc-900/40 flex flex-wrap items-center justify-between gap-3 shrink-0">
        <div className="flex items-center gap-2">
          <GitBranch className="w-4 h-4 text-emerald-400" />
          <span className="text-xs font-mono font-semibold text-zinc-200">
            Git Review & Human-in-the-Loop
          </span>
          <button
            onClick={() => refetch()}
            disabled={isRefetching}
            className="p-1 hover:bg-zinc-800 rounded text-zinc-500 hover:text-zinc-300 transition-colors ml-1"
            title="Refresh Git Diff"
          >
            <RefreshCw className={`w-3 h-3 ${isRefetching ? 'animate-spin text-emerald-400' : ''}`} />
          </button>
        </div>

        {diffText.trim() && (
          <div className="flex items-center gap-2">
            <input
              type="text"
              value={commitMessage}
              onChange={(e) => setCommitMessage(e.target.value)}
              placeholder="Commit message..."
              className="bg-zinc-900 border border-zinc-700/80 rounded px-2.5 py-1 text-xs text-zinc-200 placeholder:text-zinc-600 font-mono w-60 outline-none focus:border-emerald-500"
            />
            <button
              onClick={handleApprove}
              disabled={isApproving || !commitMessage.trim()}
              className="flex items-center gap-1.5 px-3 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded text-xs font-mono font-medium transition-colors shadow-sm"
            >
              {isApproving ? (
                <>
                  <RefreshCw className="w-3 h-3 animate-spin" /> Committing...
                </>
              ) : isSuccess ? (
                <>
                  <Check className="w-3 h-3" /> Committed!
                </>
              ) : (
                <>
                  <GitCommit className="w-3 h-3" /> Approve & Commit
                </>
              )}
            </button>
          </div>
        )}
      </div>

      {/* Diff Scroll Area */}
      <div className="flex-1 overflow-y-auto p-2 bg-zinc-950/80">
        {isLoading ? (
          <div className="p-8 text-center text-xs text-zinc-500 font-mono flex items-center justify-center gap-2">
            <RefreshCw className="w-3.5 h-3.5 animate-spin text-emerald-400" />
            Loading Git changes...
          </div>
        ) : (
          renderDiffLines(diffText)
        )}
      </div>
    </div>
  );
}
