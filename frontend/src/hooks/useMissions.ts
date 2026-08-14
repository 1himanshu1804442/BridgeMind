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
        mutationFn: ({ workspaceId, title, collaborationMode }: { workspaceId: string, title: string, collaborationMode?: 'ISOLATED' | 'COLLABORATIVE' }) => 
            createMission(workspaceId, title, collaborationMode),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['missions', variables.workspaceId] });
        },
    });
};
