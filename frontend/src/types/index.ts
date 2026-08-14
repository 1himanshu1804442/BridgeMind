export interface Workspace { id: string; name: string; createdAt: string; }
export type CollaborationMode = 'ISOLATED' | 'COLLABORATIVE';
export type MissionStatus = 'CREATED' | 'PLANNING' | 'RUNNING' | 'REVIEW' | 'MERGING' | 'DONE';
export interface Mission { id: string; title: string; status: MissionStatus; collaborationMode?: CollaborationMode; workspace: Workspace; createdAt: string; }
export type AgentStatus = 'CREATED' | 'WAITING' | 'RUNNING' | 'THINKING' | 'CALLING_TOOL' | 'WAITING_FOR_TOOL' | 'GENERATING_DIFF' | 'REVIEW_PENDING' | 'COMPLETED' | 'FAILED';
export type AgentRole = 'BACKEND_ENGINEER' | 'FRONTEND_ENGINEER' | 'QA_ENGINEER' | 'SECURITY_AUDITOR' | 'REVIEWER' | 'PLANNER' | 'ARCHITECT' | 'DEVOPS_ENGINEER';
export interface Agent { id: string; displayName: string; role: AgentRole; model: string; status: AgentStatus; costCents: number; elapsedMs: number; lastOutput: string | null; createdAt: string; }
export interface TimelineEntry { id: string; workspaceId: string; eventType: string; actorName: string; actorType: string; summary: string; details: string | null; createdAt: string; }
export interface WebSocketMessage {
  type: string;
  payload: {
    missionId?: string;
    workspaceId?: string;
    agentId?: string;
    eventType?: string;
    oldStatus?: string | null;
    newStatus?: string;
    details?: string;
  };
  timestamp: string;
}
