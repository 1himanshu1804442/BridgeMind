import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect, beforeEach } from 'vitest'
import { LivePreview } from './LivePreview'
import { useWorkspaceStore } from '../../../store/workspaceStore'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

describe('LivePreview', () => {
  let queryClient: QueryClient

  beforeEach(() => {
    queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    useWorkspaceStore.setState({ activeWorkspaceId: 'ws-101', activeMissionId: 'mission-101' })
  })

  it('renders LivePreview panel with viewport switcher and tabs', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <LivePreview />
      </QueryClientProvider>
    )

    // Verify main preview panel exists
    expect(screen.getByTestId('live-preview-panel')).toBeInTheDocument()

    // Verify tabs
    expect(screen.getByRole('button', { name: /Live Preview/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Code Inspector/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Git Staged/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Unit Tests/i })).toBeInTheDocument()
  })

  it('switches to Code Inspector tab and displays code details', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <LivePreview />
      </QueryClientProvider>
    )

    const codeTabBtn = screen.getByRole('button', { name: /Code Inspector/i })
    fireEvent.click(codeTabBtn)

    expect(screen.getAllByText(/game\.js/i).length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText(/index\.html/i).length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText(/style\.css/i).length).toBeGreaterThanOrEqual(1)
  })

  it('switches to Git Staged tab and displays staged files', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <LivePreview />
      </QueryClientProvider>
    )

    const gitTabBtn = screen.getByRole('button', { name: /Git Staged/i })
    fireEvent.click(gitTabBtn)

    expect(screen.getByText(/Git Working Tree/i)).toBeInTheDocument()
  })
})
