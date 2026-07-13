import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchWorkspaces, createWorkspace } from '../services/api';

export const useWorkspaces = () => {
    return useQuery({
        queryKey: ['workspaces'],
        queryFn: fetchWorkspaces
    });
};

export const useCreateWorkspace = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (name: string) => createWorkspace(name),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['workspaces'] });
        }
    });
};
