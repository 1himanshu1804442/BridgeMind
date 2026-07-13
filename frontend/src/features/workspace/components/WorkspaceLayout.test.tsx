import { render, screen } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect } from 'vitest'
import { WorkspaceLayout } from './WorkspaceLayout'

describe('WorkspaceLayout', () => {
  it('renders all structural areas of the Mission Control interface (strict dark mode linear style)', () => {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    render(
      <QueryClientProvider client={queryClient}>
        <WorkspaceLayout />
      </QueryClientProvider>,
    )
    
    expect(screen.getByRole('banner', { name: /mission command bar/i })).toBeInTheDocument()
    expect(screen.getByRole('navigation', { name: /primary sidebar/i })).toBeInTheDocument()
    expect(screen.getByText(/SYSTEM STANDBY - SELECT MISSION/i)).toBeInTheDocument()
    expect(screen.getByTestId('memory-inspector')).toBeInTheDocument()
  })
})
