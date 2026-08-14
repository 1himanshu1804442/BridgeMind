import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect, beforeEach } from 'vitest'
import { LivePreview } from './LivePreview'
import { useWorkspaceStore } from '../../../store/workspaceStore'

describe('LivePreview', () => {
  beforeEach(() => {
    useWorkspaceStore.setState({ activeWorkspaceId: 'ws-101', activeMissionId: 'mission-101' })
  })

  it('renders LivePreview panel with viewport switcher and tabs', () => {
    render(<LivePreview />)

    // Verify main preview panel exists
    expect(screen.getByTestId('live-preview-panel')).toBeInTheDocument()
    expect(screen.getByText(/BridgeSpace ADE Preview/i)).toBeInTheDocument()

    // Verify tabs
    expect(screen.getByRole('button', { name: /Live App \/ Game/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Workspace Files/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Git Diff Review/i })).toBeInTheDocument()
  })

  it('switches to Workspace Files tab and displays generated project files', () => {
    render(<LivePreview />)

    const filesTabBtn = screen.getByRole('button', { name: /Workspace Files/i })
    fireEvent.click(filesTabBtn)

    expect(screen.getByText(/Generated Files/i)).toBeInTheDocument()
    expect(screen.getAllByText('index.html').length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText('game.js').length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText('style.css').length).toBeGreaterThanOrEqual(1)
  })

  it('switches to Git Diff Review tab and displays diff details', () => {
    render(<LivePreview />)

    const gitTabBtn = screen.getByRole('button', { name: /Git Diff Review/i })
    fireEvent.click(gitTabBtn)

    expect(screen.getByText(/feat\/space-arcade/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Approve & Merge/i })).toBeInTheDocument()
  })
})
