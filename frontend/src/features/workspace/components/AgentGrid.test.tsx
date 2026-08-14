import { render, screen } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { beforeEach, describe, it, expect } from 'vitest'
import { AgentGrid } from './AgentGrid'
import { useWorkspaceStore } from '../../../store/workspaceStore'
import type { Agent } from '../../../types'

const missionId = 'mission-1'
const agents: Agent[] = [
  {
    id: 'agent-1', 
    displayName: 'Backend Engineer', 
    role: 'BACKEND_ENGINEER',
    model: 'claude-code', 
    status: 'RUNNING', 
    costCents: 12,
    elapsedMs: 1_000, 
    lastOutput: 'Generating database schema and services...', 
    createdAt: '2026-01-01T00:00:00Z',
  },
]

describe('AgentGrid', () => {
  beforeEach(() => {
    useWorkspaceStore.setState({ activeWorkspaceId: 'workspace-1', activeMissionId: missionId })
  })

  it('renders a 4-pane grid with active agent and standby terminal slots', () => {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    queryClient.setQueryData(['agents', missionId], agents)

    render(
      <QueryClientProvider client={queryClient}>
        <AgentGrid />
      </QueryClientProvider>,
    )

    // The active agent display name
    expect(screen.getByText('Backend Engineer')).toBeInTheDocument()
    
    // There should be exactly 4 panes total in the 2x2 multi-agent matrix
    expect(screen.getAllByTestId('agent-pane')).toHaveLength(4)
    
    // 3 of them should be in standby mode awaiting allocation
    expect(screen.getAllByText(/STANDBY — AWAITING AGENT ALLOCATION/i)).toHaveLength(3)
  })

  it('renders available AI models including Claude Code, Codex, Antigravity AGY, DeepSeek V4, and Aider', () => {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    queryClient.setQueryData(['agents', missionId], agents)

    render(
      <QueryClientProvider client={queryClient}>
        <AgentGrid />
      </QueryClientProvider>,
    )

    // Check model options in select
    expect(screen.getByText(/Claude Code 3.5 Sonnet/i)).toBeInTheDocument()
    expect(screen.getByText(/OpenAI Codex/i)).toBeInTheDocument()
    expect(screen.getByText(/Antigravity AGY Engine/i)).toBeInTheDocument()
    expect(screen.getByText(/DeepSeek V4 Coder/i)).toBeInTheDocument()
    expect(screen.getByText(/Aider Multi-File Architect/i)).toBeInTheDocument()
  })
})
