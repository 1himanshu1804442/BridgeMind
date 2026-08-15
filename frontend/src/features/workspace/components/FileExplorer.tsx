import { useState } from 'react';
import { 
  Folder, 
  FolderOpen, 
  FileCode, 
  FileText, 
  FileJson, 
  ChevronRight, 
  ChevronDown, 
  RefreshCw, 
  Search
} from 'lucide-react';
import { useWorkspaceFiles } from '../../../hooks/useWorkspaceFiles';
import type { WorkspaceFileNode } from '../../../types';

interface FileExplorerProps {
  workspaceId: string | null;
  selectedFile: string | null;
  onSelectFile: (path: string) => void;
}

const getFileIcon = (_fileName: string, ext: string) => {
  if (['ts', 'tsx', 'js', 'jsx', 'java', 'py', 'go', 'rs', 'cpp', 'c'].includes(ext)) {
    return <FileCode className="w-3.5 h-3.5 text-sky-400 shrink-0" />;
  }
  if (['json', 'yml', 'yaml', 'toml', 'xml'].includes(ext)) {
    return <FileJson className="w-3.5 h-3.5 text-amber-400 shrink-0" />;
  }
  if (['html', 'css', 'scss', 'svg'].includes(ext)) {
    return <FileCode className="w-3.5 h-3.5 text-emerald-400 shrink-0" />;
  }
  return <FileText className="w-3.5 h-3.5 text-zinc-400 shrink-0" />;
};

const formatSize = (bytes: number) => {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
};

function FileTreeNode({
  node,
  selectedFile,
  onSelectFile,
  depth = 0
}: {
  node: WorkspaceFileNode;
  selectedFile: string | null;
  onSelectFile: (path: string) => void;
  depth?: number;
}) {
  const [isOpen, setIsOpen] = useState(depth === 0 || depth === 1);
  const isSelected = selectedFile === node.path;

  if (node.isDirectory) {
    return (
      <div className="select-none">
        <button
          onClick={() => setIsOpen(!isOpen)}
          style={{ paddingLeft: `${depth * 12 + 8}px` }}
          className="w-full flex items-center gap-1.5 py-1 text-xs text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60 rounded transition-colors text-left"
        >
          {isOpen ? (
            <ChevronDown className="w-3 h-3 text-zinc-500 shrink-0" />
          ) : (
            <ChevronRight className="w-3 h-3 text-zinc-500 shrink-0" />
          )}
          {isOpen ? (
            <FolderOpen className="w-3.5 h-3.5 text-amber-400/90 shrink-0" />
          ) : (
            <Folder className="w-3.5 h-3.5 text-amber-500/80 shrink-0" />
          )}
          <span className="font-mono truncate">{node.name}</span>
        </button>

        {isOpen && node.children && node.children.length > 0 && (
          <div className="flex flex-col">
            {node.children.map((child) => (
              <FileTreeNode
                key={child.path}
                node={child}
                selectedFile={selectedFile}
                onSelectFile={onSelectFile}
                depth={depth + 1}
              />
            ))}
          </div>
        )}
      </div>
    );
  }

  return (
    <button
      onClick={() => onSelectFile(node.path)}
      style={{ paddingLeft: `${depth * 12 + 20}px` }}
      className={`w-full flex items-center justify-between py-1 pr-2 text-xs rounded transition-colors text-left group ${
        isSelected
          ? 'bg-emerald-950/70 text-emerald-300 font-medium border-l-2 border-emerald-500'
          : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/50'
      }`}
    >
      <div className="flex items-center gap-1.5 min-w-0">
        {getFileIcon(node.name, node.extension)}
        <span className="font-mono truncate">{node.name}</span>
      </div>
      {node.size > 0 && (
        <span className="text-[10px] text-zinc-600 font-mono group-hover:text-zinc-500 shrink-0">
          {formatSize(node.size)}
        </span>
      )}
    </button>
  );
}

export function FileExplorer({ workspaceId, selectedFile, onSelectFile }: FileExplorerProps) {
  const { data: files = [], isLoading, refetch, isRefetching } = useWorkspaceFiles(workspaceId);
  const [searchTerm, setSearchTerm] = useState('');

  const filterNodes = (nodes: WorkspaceFileNode[]): WorkspaceFileNode[] => {
    if (!searchTerm.trim()) return nodes;
    return nodes
      .map(node => {
        if (node.isDirectory) {
          const matchingChildren = filterNodes(node.children || []);
          if (matchingChildren.length > 0) {
            return { ...node, children: matchingChildren };
          }
        }
        if (node.name.toLowerCase().includes(searchTerm.toLowerCase())) {
          return node;
        }
        return null;
      })
      .filter(Boolean) as WorkspaceFileNode[];
  };

  const filteredFiles = filterNodes(files);

  return (
    <div className="flex flex-col h-full bg-zinc-950/90 border-r border-zinc-800/80 text-zinc-300 font-sans">
      {/* Header */}
      <div className="h-10 px-3 border-b border-zinc-800/80 flex items-center justify-between bg-zinc-900/40 shrink-0">
        <span className="text-[11px] font-mono font-semibold tracking-wider text-zinc-400 uppercase flex items-center gap-1.5">
          <Folder className="w-3.5 h-3.5 text-emerald-400" />
          Workspace Files
        </span>
        <button
          onClick={() => refetch()}
          disabled={isRefetching}
          className="p-1 hover:bg-zinc-800 rounded text-zinc-500 hover:text-zinc-300 transition-colors"
          title="Refresh File Tree"
        >
          <RefreshCw className={`w-3 h-3 ${isRefetching ? 'animate-spin text-emerald-400' : ''}`} />
        </button>
      </div>

      {/* Filter Search */}
      <div className="p-2 border-b border-zinc-800/60 bg-zinc-950/50 shrink-0">
        <div className="flex items-center bg-zinc-900/80 border border-zinc-800/80 rounded px-2 py-1 text-xs">
          <Search className="w-3 h-3 text-zinc-500 mr-1.5 shrink-0" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search files..."
            className="bg-transparent border-none outline-none flex-1 text-zinc-200 placeholder:text-zinc-600 font-mono text-[11px]"
          />
        </div>
      </div>

      {/* Tree Content */}
      <div className="flex-1 overflow-y-auto p-1 py-2 font-mono">
        {isLoading ? (
          <div className="p-4 text-center text-xs text-zinc-600 font-mono flex items-center justify-center gap-2">
            <RefreshCw className="w-3.5 h-3.5 animate-spin text-emerald-500" />
            Loading project tree...
          </div>
        ) : filteredFiles.length === 0 ? (
          <div className="p-6 text-center text-xs text-zinc-600 font-mono">
            {searchTerm ? 'No matching files found' : 'No workspace files generated yet.'}
          </div>
        ) : (
          filteredFiles.map((node) => (
            <FileTreeNode
              key={node.path}
              node={node}
              selectedFile={selectedFile}
              onSelectFile={onSelectFile}
            />
          ))
        )}
      </div>
    </div>
  );
}
