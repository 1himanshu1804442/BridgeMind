import { useState, useEffect } from 'react';
import { 
  FileCode, 
  Save, 
  Copy, 
  Check, 
  RefreshCw, 
  X,
  Code
} from 'lucide-react';
import { useWorkspaceFileContent, useSaveWorkspaceFileContent } from '../../../hooks/useWorkspaceFiles';

interface FileEditorProps {
  workspaceId: string | null;
  filePath: string | null;
  onClose?: () => void;
}

export function FileEditor({ workspaceId, filePath, onClose }: FileEditorProps) {
  const { data, isLoading } = useWorkspaceFileContent(workspaceId, filePath);
  const { mutate: saveFile, isPending: isSaving, isSuccess: isSaved } = useSaveWorkspaceFileContent();
  const [content, setContent] = useState('');
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    if (data?.content !== undefined) {
      setContent(data.content);
    }
  }, [data]);

  const handleCopy = () => {
    navigator.clipboard.writeText(content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleSave = () => {
    if (!workspaceId || !filePath) return;
    saveFile({ workspaceId, path: filePath, content });
  };

  if (!filePath) {
    return (
      <div className="flex flex-col items-center justify-center h-full p-8 text-center text-zinc-600 font-mono">
        <Code className="w-8 h-8 mb-2 opacity-60" />
        <span className="text-sm text-zinc-400">No file selected</span>
        <span className="text-xs text-zinc-600 mt-1">Select a file from the workspace tree to inspect or edit.</span>
      </div>
    );
  }

  const lines = content.split('\n');

  return (
    <div className="flex flex-col h-full bg-zinc-950 text-zinc-300 font-sans overflow-hidden">
      {/* File Tab Header */}
      <div className="h-10 px-3 border-b border-zinc-800/80 bg-zinc-900/40 flex items-center justify-between shrink-0">
        <div className="flex items-center gap-2 min-w-0">
          <FileCode className="w-4 h-4 text-emerald-400 shrink-0" />
          <span className="text-xs font-mono font-medium text-zinc-200 truncate">
            {filePath}
          </span>
        </div>

        <div className="flex items-center gap-1.5 shrink-0">
          <button
            onClick={handleCopy}
            className="p-1.5 hover:bg-zinc-800 rounded text-zinc-400 hover:text-zinc-200 transition-colors text-xs flex items-center gap-1 font-mono"
            title="Copy file contents"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            <span className="hidden sm:inline text-[11px]">{copied ? 'Copied' : 'Copy'}</span>
          </button>

          <button
            onClick={handleSave}
            disabled={isSaving}
            className="flex items-center gap-1 px-2.5 py-1 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded text-xs font-mono font-medium transition-colors ml-1 shadow-sm"
          >
            {isSaving ? (
              <>
                <RefreshCw className="w-3 h-3 animate-spin" /> Saving...
              </>
            ) : isSaved ? (
              <>
                <Check className="w-3 h-3" /> Saved
              </>
            ) : (
              <>
                <Save className="w-3 h-3" /> Save
              </>
            )}
          </button>

          {onClose && (
            <button
              onClick={onClose}
              className="p-1.5 hover:bg-zinc-800 rounded text-zinc-500 hover:text-zinc-300 transition-colors ml-1"
              title="Close editor"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>
      </div>

      {/* Editor Body */}
      <div className="flex-1 flex overflow-hidden">
        {isLoading ? (
          <div className="p-8 text-center text-xs text-zinc-500 font-mono flex items-center justify-center gap-2 w-full">
            <RefreshCw className="w-3.5 h-3.5 animate-spin text-emerald-400" />
            Loading file content...
          </div>
        ) : (
          <div className="flex-1 flex overflow-auto font-mono text-[11px] bg-zinc-950">
            {/* Line numbers column */}
            <div className="py-3 px-2 bg-zinc-950/80 border-r border-zinc-800/60 text-zinc-600 select-none text-right font-mono min-w-[40px]">
              {lines.map((_, i) => (
                <div key={i} className="leading-5 text-[10px]">
                  {i + 1}
                </div>
              ))}
            </div>

            {/* Editable code textarea */}
            <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              spellCheck={false}
              className="flex-1 p-3 bg-transparent text-zinc-200 outline-none resize-none font-mono leading-5 whitespace-pre overflow-auto selection:bg-emerald-950 selection:text-emerald-300"
            />
          </div>
        )}
      </div>
    </div>
  );
}
