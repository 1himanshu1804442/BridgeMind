import type { Workspace, Mission, Agent, TimelineEntry, MissionStatus } from '../types';

export const apiBaseUrl = 'http://localhost:8080/api';

const TOKEN_KEY = 'hivepilot_jwt_token';

export const getAuthToken = (): string | null => {
    try {
        return localStorage.getItem(TOKEN_KEY);
    } catch {
        return null;
    }
};

export const setAuthToken = (token: string): void => {
    try {
        localStorage.setItem(TOKEN_KEY, token);
    } catch (e) {
        console.error('Failed to save auth token to localStorage:', e);
    }
};

export const ensureAuth = async (): Promise<string> => {
    const existing = getAuthToken();
    if (existing) return existing;

    try {
        // Try login first
        const loginRes = await fetch(`${apiBaseUrl}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: 'dev@hivepilot.local', password: 'password123' })
        });
        if (loginRes.ok) {
            const data = await loginRes.json();
            if (data.token) {
                setAuthToken(data.token);
                return data.token;
            }
        }

        // If login failed (e.g. user not created yet), register
        const regRes = await fetch(`${apiBaseUrl}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: 'dev@hivepilot.local', password: 'password123' })
        });
        if (regRes.ok) {
            const data = await regRes.json();
            if (data.token) {
                setAuthToken(data.token);
                return data.token;
            }
        }
    } catch (e) {
        console.error('Auto-authentication failed:', e);
    }
    return '';
};

export const fetchApi = async <T>(endpoint: string, options?: RequestInit): Promise<T> => {
    let token = getAuthToken();
    if (!token && !endpoint.startsWith('/auth/')) {
        token = await ensureAuth();
    }

    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...((options?.headers as Record<string, string>) || {})
    };

    let response = await fetch(`${apiBaseUrl}${endpoint}`, {
        ...options,
        headers
    });

    // If token expired or unauthorized, re-authenticate once
    if (response.status === 403 || response.status === 401) {
        try {
            localStorage.removeItem(TOKEN_KEY);
        } catch {
            // ignore
        }
        token = await ensureAuth();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
            response = await fetch(`${apiBaseUrl}${endpoint}`, {
                ...options,
                headers
            });
        }
    }

    if (!response.ok) {
        const errorText = await response.text();
        console.error(`API Request to ${endpoint} failed (${response.status}):`, errorText);
        throw new Error(`API Error ${response.status}: ${errorText || response.statusText}`);
    }
    return response.json();
};

export const createWorkspace = (name: string) => fetchApi<Workspace>('/workspaces', { method: 'POST', body: JSON.stringify({ name }) });
export const fetchWorkspaces = () => fetchApi<Workspace[]>('/workspaces');
export const fetchMissions = (workspaceId: string) => fetchApi<Mission[]>(`/workspaces/${workspaceId}/missions`);
export const createMission = (workspaceId: string, title: string, collaborationMode: 'ISOLATED' | 'COLLABORATIVE' = 'COLLABORATIVE') => 
    fetchApi<Mission>(`/workspaces/${workspaceId}/missions`, { method: 'POST', body: JSON.stringify({ title, collaborationMode }) });
export const updateMissionStatus = (workspaceId: string, missionId: string, status: MissionStatus) => 
    fetchApi<Mission>(`/workspaces/${workspaceId}/missions/${missionId}/status`, { method: 'PATCH', body: JSON.stringify({ status }) });
export const fetchAgents = (missionId: string) => fetchApi<Agent[]>(`/missions/${missionId}/agents`);
export const spawnAgent = (missionId: string, data: { displayName: string; role: Agent['role']; model: string }) => 
    fetchApi<Agent>(`/missions/${missionId}/agents`, { method: 'POST', body: JSON.stringify(data) });
export const sendAgentCommand = (missionId: string, agentId: string, command: string) =>
    fetchApi<Agent>(`/missions/${missionId}/agents/${agentId}/command`, { method: 'POST', body: JSON.stringify({ command }) });
export const updateAgentModel = (missionId: string, agentId: string, model: string) =>
    fetchApi<Agent>(`/missions/${missionId}/agents/${agentId}/model`, { method: 'PATCH', body: JSON.stringify({ model }) });

export const fetchTimeline = (workspaceId: string) => fetchApi<TimelineEntry[]>(`/workspaces/${workspaceId}/timeline`);


// Workspace Files API
export const fetchWorkspaceFiles = (workspaceId: string) => 
    fetchApi<import('../types').WorkspaceFileNode[]>(`/workspaces/${workspaceId}/files`);

export const fetchWorkspaceFileContent = (workspaceId: string, path: string) =>
    fetchApi<{ path: string; content: string }>(`/workspaces/${workspaceId}/files/content?path=${encodeURIComponent(path)}`);

export const saveWorkspaceFileContent = (workspaceId: string, path: string, content: string) =>
    fetchApi<{ status: string; path: string }>(`/workspaces/${workspaceId}/files/content`, {
        method: 'PUT',
        body: JSON.stringify({ path, content })
    });

// Mission Tasks (DAG) API
export const fetchMissionTasks = (missionId: string) =>
    fetchApi<import('../types').MissionTask[]>(`/missions/${missionId}/tasks`);

// Git Review API
export const fetchGitDiff = (workspaceId: string) =>
    fetchApi<{ diff: string }>(`/workspaces/${workspaceId}/review/diff`);

export const approveGitDiff = (workspaceId: string, executionId: string, message: string) =>
    fetchApi<void>(`/workspaces/${workspaceId}/review/approve?executionId=${encodeURIComponent(executionId)}&message=${encodeURIComponent(message)}`, {
        method: 'POST'
    });

// Sandbox Test Runner API
export const runWorkspaceTests = (workspaceId: string) =>
    fetchApi<import('../types').TestRunResult>(`/workspaces/${workspaceId}/tests/run`, {
        method: 'POST'
    });

export const fetchLatestTestResult = (workspaceId: string) =>
    fetchApi<import('../types').TestRunResult>(`/workspaces/${workspaceId}/tests/latest`);

// MCP Server API
export const fetchMcpInfo = () =>
    fetchApi<Record<string, any>>('/mcp/info');

