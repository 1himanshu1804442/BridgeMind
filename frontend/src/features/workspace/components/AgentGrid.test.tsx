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

  it('renders agent data for the active mission', () => {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    queryClient.setQueryData(['agents', missionId], agents)

    render(
      <QueryClientProvider client={queryClient}>
        <AgentGrid />
      </QueryClientProvider>,
    )

    expect(screen.getAllByTestId('agent-pane')).toHaveLength(1)
    expect(screen.getByText('BACKEND_ENGINEER')).toBeInTheDocument()
    expect(screen.getByText('claude-3-5-sonnet')).toBeInTheDocument()
  })
})
