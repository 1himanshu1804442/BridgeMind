import { render, screen } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { beforeEach, describe, it, expect } from 'vitest'
import { AgentGrid } from './AgentGrid'
import { useWorkspaceStore } from '../../../store/workspaceStore'
import type { Agent } from '../../../types'

const missionId = 'mission-1'
const agents: Agent[] = [
  {
    id: 'agent-1', displayName: 'Backend Engineer', role: 'BACKEND_ENGINEER',
    model: 'claude-3-5-sonnet', status: 'RUNNING', costCents: 12,
    elapsedMs: 1_000, lastOutput: 'Working', createdAt: '2026-01-01T00:00:00Z',
  },
]

describe('AgentGrid', () => {
  beforeEach(() => {
    useWorkspaceStore.setState({ activeWorkspaceId: 'workspace-1', activeMissionId: missionId })
  })

  it('renders a 4-pane grid regardless of number of active agents', () => {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    queryClient.setQueryData(['agents', missionId], agents)

    render(
      <QueryClientProvider client={queryClient}>
        <AgentGrid />
      </QueryClientProvider>,
    )

    // The active agent
    expect(screen.getByText('BACKEND_ENGINEER')).toBeInTheDocument()
    
    // There should be exactly 4 panes total
    expect(screen.getAllByTestId('agent-pane')).toHaveLength(4)
    
    // 3 of them should be empty/idle
    expect(screen.getAllByText('IDLE - AWAITING DEPLOYMENT')).toHaveLength(3)
  })
})
