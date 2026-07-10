import { Workspace, Mission, Agent, TimelineEntry, MissionStatus, AgentStatus } from '../types';

export const apiBaseUrl = 'http://localhost:8080/api';

export const fetchApi = async <T>(endpoint: string, options?: RequestInit): Promise<T> => {
    const response = await fetch(`${apiBaseUrl}${endpoint}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(options?.headers || {})
        }
    });
    if (!response.ok) throw new Error('API Error: ' + response.statusText);
    return response.json();
};

export const createWorkspace = (name: string) => fetchApi<Workspace>('/workspaces', { method: 'POST', body: JSON.stringify({ name }) });
export const fetchWorkspaces = () => fetchApi<Workspace[]>('/workspaces');
export const fetchMissions = (workspaceId: string) => fetchApi<Mission[]>(`/workspaces/${workspaceId}/missions`);
export const createMission = (workspaceId: string, title: string) => fetchApi<Mission>(`/workspaces/${workspaceId}/missions`, { method: 'POST', body: JSON.stringify({ title }) });
export const updateMissionStatus = (missionId: string, status: MissionStatus) => fetchApi<Mission>(`/workspaces/missions/${missionId}/status`, { method: 'PATCH', body: JSON.stringify({ status }) });
export const fetchAgents = (missionId: string) => fetchApi<Agent[]>(`/missions/${missionId}/agents`);
export const spawnAgent = (missionId: string, data: any) => fetchApi<Agent>(`/missions/${missionId}/agents`, { method: 'POST', body: JSON.stringify(data) });
export const fetchTimeline = (workspaceId: string) => fetchApi<TimelineEntry[]>(`/workspaces/${workspaceId}/timeline`);
