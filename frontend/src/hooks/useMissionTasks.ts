import { useQuery } from '@tanstack/react-query';
import { fetchMissionTasks } from '../services/api';
import type { MissionTask } from '../types';

export function useMissionTasks(missionId: string | null) {
  return useQuery<MissionTask[]>({
    queryKey: ['mission-tasks', missionId],
    queryFn: () => {
      if (!missionId) return Promise.resolve([]);
      return fetchMissionTasks(missionId);
    },
    enabled: !!missionId,
    refetchInterval: 3000,
  });
}
