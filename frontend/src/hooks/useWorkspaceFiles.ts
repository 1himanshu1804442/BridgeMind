import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchWorkspaceFiles, fetchWorkspaceFileContent, saveWorkspaceFileContent } from '../services/api';
import type { WorkspaceFileNode } from '../types';

export function useWorkspaceFiles(workspaceId: string | null) {
  return useQuery<WorkspaceFileNode[]>({
    queryKey: ['workspace-files', workspaceId],
    queryFn: () => {
      if (!workspaceId) return Promise.resolve([]);
      return fetchWorkspaceFiles(workspaceId);
    },
    enabled: !!workspaceId,
    staleTime: 3000,
    refetchInterval: 5000,
  });
}

export function useWorkspaceFileContent(workspaceId: string | null, path: string | null) {
  return useQuery<{ path: string; content: string }>({
    queryKey: ['workspace-file-content', workspaceId, path],
    queryFn: () => {
      if (!workspaceId || !path) return Promise.resolve({ path: '', content: '' });
      return fetchWorkspaceFileContent(workspaceId, path);
    },
    enabled: !!workspaceId && !!path,
    staleTime: 5000,
  });
}

export function useSaveWorkspaceFileContent() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ workspaceId, path, content }: { workspaceId: string; path: string; content: string }) =>
      saveWorkspaceFileContent(workspaceId, path, content),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['workspace-files', variables.workspaceId] });
      queryClient.invalidateQueries({ queryKey: ['workspace-file-content', variables.workspaceId, variables.path] });
    },
  });
}
