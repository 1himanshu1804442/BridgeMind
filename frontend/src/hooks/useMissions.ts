import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchMissions, createMission } from '../services/api';

export const useMissions = (workspaceId: string | null) => {
    return useQuery({
        queryKey: ['missions', workspaceId],
        queryFn: () => workspaceId ? fetchMissions(workspaceId) : Promise.resolve([]),
        enabled: !!workspaceId,
    });
};

export const useCreateMission = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ workspaceId, title }: { workspaceId: string, title: string }) => createMission(workspaceId, title),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['missions', variables.workspaceId] });
        },
    });
};
