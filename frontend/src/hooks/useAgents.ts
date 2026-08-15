import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchAgents, sendAgentCommand, updateAgentModel } from '../services/api';

export const useAgents = (missionId: string | null) => {
    return useQuery({
        queryKey: ['agents', missionId],
        queryFn: () => missionId ? fetchAgents(missionId) : Promise.resolve([]),
        enabled: !!missionId,
        refetchInterval: 1000,
    });
};

export const useSendAgentCommand = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ missionId, agentId, command }: { missionId: string; agentId: string; command: string }) =>
            sendAgentCommand(missionId, agentId, command),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['agents', variables.missionId] });
        },
    });
};

export const useUpdateAgentModel = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ missionId, agentId, model }: { missionId: string; agentId: string; model: string }) =>
            updateAgentModel(missionId, agentId, model),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['agents', variables.missionId] });
        },
    });
};

export const useUpdateAgentStatus = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ missionId, agentId, status }: { missionId: string; agentId: string; status: any }) =>
            import('../services/api').then(m => m.updateAgentStatus(missionId, agentId, status)),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['agents', variables.missionId] });
        },
    });
};

