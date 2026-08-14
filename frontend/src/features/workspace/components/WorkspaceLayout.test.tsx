import { render, screen, fireEvent } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect } from 'vitest'
import { WorkspaceLayout } from './WorkspaceLayout'

describe('WorkspaceLayout', () => {
  it('renders structural areas of the BridgeMind Studio ADE interface with Split View', () => {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    render(
      <QueryClientProvider client={queryClient}>
        <WorkspaceLayout />
      </QueryClientProvider>,
    )
    
    // Command bar & sidebar
    expect(screen.getByRole('banner', { name: /mission command bar/i })).toBeInTheDocument()
    expect(screen.getByRole('navigation', { name: /primary sidebar/i })).toBeInTheDocument()

    // Mode switchers: Isolated vs Collaborative
    expect(screen.getByRole('button', { name: /Collaborative/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Isolated/i })).toBeInTheDocument()

    // Split view sections: Agent Matrix (60%) & Live Preview (40%)
    expect(screen.getByTestId('agent-matrix-section')).toBeInTheDocument()
    expect(screen.getByTestId('preview-section')).toBeInTheDocument()
  })

  it('toggles collaboration mode and split view buttons', () => {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    render(
      <QueryClientProvider client={queryClient}>
        <WorkspaceLayout />
      </QueryClientProvider>,
    )

    const isolatedBtn = screen.getByRole('button', { name: /Isolated/i })
    fireEvent.click(isolatedBtn)
    expect(isolatedBtn).toHaveClass('text-emerald-400')

    const terminalsViewBtn = screen.getByRole('button', { name: /Terminals/i })
    fireEvent.click(terminalsViewBtn)
    expect(screen.getByTestId('agent-matrix-section')).toBeInTheDocument()
    expect(screen.queryByTestId('preview-section')).not.toBeInTheDocument()
  })
})
