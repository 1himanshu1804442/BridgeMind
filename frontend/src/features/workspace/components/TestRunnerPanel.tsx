import { useState } from 'react';
import { 
  Play, 
  CheckCircle2, 
  AlertCircle, 
  Clock, 
  RefreshCw, 
  Copy, 
  Check, 
  Terminal,
  ShieldCheck
} from 'lucide-react';
import { useWorkspaceTests, useRunWorkspaceTests } from '../../../hooks/useWorkspaceTests';

interface TestRunnerPanelProps {
  workspaceId: string | null;
}

export function TestRunnerPanel({ workspaceId }: TestRunnerPanelProps) {
  const { data, isLoading } = useWorkspaceTests(workspaceId);
  const { mutate: runTests, isPending: isRunning } = useRunWorkspaceTests();
  const [copied, setCopied] = useState(false);

  const handleRun = () => {
    if (!workspaceId || isRunning) return;
    runTests(workspaceId);
  };

  const handleCopy = () => {
    if (!data?.output) return;
    navigator.clipboard.writeText(data.output);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const isPassed = data?.status === 'PASSED';
  const isFailed = data?.status === 'FAILED';
  const isError = data?.status === 'ERROR';

  return (
    <div className="flex flex-col h-full bg-zinc-950 text-zinc-300 font-sans overflow-hidden">
      {/* Top Header Bar */}
      <div className="h-10 px-3 border-b border-zinc-800/80 bg-zinc-900/40 flex items-center justify-between shrink-0 font-mono text-xs">
        <div className="flex items-center gap-2 min-w-0">
          <ShieldCheck className="w-4 h-4 text-emerald-400 shrink-0" />
          <span className="font-semibold text-zinc-200 truncate">
            {data?.suiteType || 'Sandbox Test Suite'}
          </span>
          {data?.status && data.status !== 'NO_RUNS_YET' && (
            <span className={`px-2 py-0.5 rounded text-[10px] font-bold flex items-center gap-1 ${
              isPassed 
                ? 'bg-emerald-950 text-emerald-400 border border-emerald-500/40' 
                : isFailed 
                ? 'bg-rose-950 text-rose-400 border border-rose-500/40' 
                : 'bg-amber-950 text-amber-400 border border-amber-500/40'
            }`}>
              {isPassed && <CheckCircle2 className="w-3 h-3" />}
              {isFailed && <AlertCircle className="w-3 h-3" />}
              {isError && <AlertCircle className="w-3 h-3" />}
              {data.status}
            </span>
          )}
          {data?.durationMs !== undefined && data.durationMs > 0 && (
            <span className="text-[10px] text-zinc-500 flex items-center gap-1">
              <Clock className="w-3 h-3" />
              {(data.durationMs / 1000).toFixed(2)}s
            </span>
          )}
        </div>

        <div className="flex items-center gap-2 shrink-0">
          {data?.output && (
            <button
              onClick={handleCopy}
              className="p-1 hover:bg-zinc-800 rounded text-zinc-400 hover:text-zinc-200 transition-colors text-xs flex items-center gap-1"
              title="Copy test output"
            >
              {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span className="hidden sm:inline text-[10px]">{copied ? 'Copied' : 'Copy'}</span>
            </button>
          )}

          <button
            onClick={handleRun}
            disabled={isRunning || !workspaceId}
            className="flex items-center gap-1.5 px-3 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded text-xs font-mono font-medium transition-colors shadow-sm"
          >
            {isRunning ? (
              <>
                <RefreshCw className="w-3.5 h-3.5 animate-spin" /> Running Suite...
              </>
            ) : (
              <>
                <Play className="w-3.5 h-3.5 fill-current" /> Run Test Suite
              </>
            )}
          </button>
        </div>
      </div>

      {/* Output Console */}
      <div className="flex-1 overflow-auto p-3 font-mono text-[11px] leading-relaxed bg-black/90 text-emerald-300 select-text">
        {isLoading || isRunning ? (
          <div className="flex flex-col items-center justify-center h-full p-8 text-center text-zinc-500">
            <RefreshCw className="w-6 h-6 animate-spin text-emerald-400 mb-2" />
            <span>Executing automated test suite in workspace sandbox...</span>
          </div>
        ) : !data?.output ? (
          <div className="flex flex-col items-center justify-center h-full p-8 text-center text-zinc-600">
            <Terminal className="w-8 h-8 mb-2 opacity-50 text-zinc-500" />
            <span className="text-sm text-zinc-400">Ready to execute tests</span>
            <span className="text-xs text-zinc-600 mt-1">
              Click &quot;Run Test Suite&quot; to execute and stream unit/integration test validations.
            </span>
          </div>
        ) : (
          <pre className="whitespace-pre-wrap font-mono">
            {data.output}
          </pre>
        )}
      </div>
    </div>
  );
}
