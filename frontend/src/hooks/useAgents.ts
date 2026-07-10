import { useQuery } from '@tanstack/react-query';
import { fetchAgents } from '../services/api';

export const useAgents = (missionId: string | null) => {
    return useQuery({
        queryKey: ['agents', missionId],
        queryFn: () => missionId ? fetchAgents(missionId) : Promise.resolve([]),
        enabled: !!missionId,
    });
};
