import { useQuery } from '@tanstack/react-query';
import { fetchTimeline } from '../services/api';

export const useTimeline = (workspaceId: string | null) => {
    return useQuery({
        queryKey: ['timeline', workspaceId],
        queryFn: () => workspaceId ? fetchTimeline(workspaceId) : Promise.resolve([]),
        enabled: !!workspaceId,
    });
};
