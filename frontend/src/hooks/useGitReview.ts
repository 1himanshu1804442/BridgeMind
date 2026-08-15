import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchGitDiff, approveGitDiff } from '../services/api';

export function useGitDiff(workspaceId: string | null) {
  return useQuery<{ diff: string }>({
    queryKey: ['git-diff', workspaceId],
    queryFn: () => {
      if (!workspaceId) return Promise.resolve({ diff: '' });
      return fetchGitDiff(workspaceId);
    },
    enabled: !!workspaceId,
    refetchInterval: 5000,
  });
}

export function useApproveGitDiff() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ workspaceId, executionId, message }: { workspaceId: string; executionId: string; message: string }) =>
      approveGitDiff(workspaceId, executionId, message),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['git-diff', variables.workspaceId] });
      queryClient.invalidateQueries({ queryKey: ['timeline', variables.workspaceId] });
      queryClient.invalidateQueries({ queryKey: ['workspace-files', variables.workspaceId] });
    },
  });
}
