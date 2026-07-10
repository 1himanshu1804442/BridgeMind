import { create } from 'zustand';

interface WorkspaceState {
    activeWorkspaceId: string | null;
    activeMissionId: string | null;
    sidebarPanel: 'explorer' | 'agents' | 'timeline' | 'memory' | 'git' | 'settings';
    setActiveWorkspace: (id: string | null) => void;
    setActiveMission: (id: string | null) => void;
    setSidebarPanel: (panel: 'explorer' | 'agents' | 'timeline' | 'memory' | 'git' | 'settings') => void;
}

export const useWorkspaceStore = create<WorkspaceState>((set) => ({
    activeWorkspaceId: null,
    activeMissionId: null,
    sidebarPanel: 'explorer',
    setActiveWorkspace: (id) => set({ activeWorkspaceId: id }),
    setActiveMission: (id) => set({ activeMissionId: id }),
    setSidebarPanel: (panel) => set({ sidebarPanel: panel }),
}));
