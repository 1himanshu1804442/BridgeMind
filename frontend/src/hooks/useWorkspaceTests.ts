import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { runWorkspaceTests, fetchLatestTestResult } from '../services/api';
import type { TestRunResult } from '../types';

export function useWorkspaceTests(workspaceId: string | null) {
  return useQuery<TestRunResult>({
    queryKey: ['workspace-tests-latest', workspaceId],
    queryFn: () => {
      if (!workspaceId) return Promise.resolve({
        suiteType: 'None',
        status: 'NO_RUNS_YET',
        exitCode: 0,
        durationMs: 0,
        output: '',
        summary: 'No tests run yet.',
        executedAt: new Date().toISOString()
      });
      return fetchLatestTestResult(workspaceId);
    },
    enabled: !!workspaceId,
    staleTime: 5000,
  });
}

export function useRunWorkspaceTests() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (workspaceId: string) => runWorkspaceTests(workspaceId),
    onSuccess: (data, workspaceId) => {
      queryClient.setQueryData(['workspace-tests-latest', workspaceId], data);
      queryClient.invalidateQueries({ queryKey: ['timeline', workspaceId] });
    },
  });
}
