import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { WorkspaceLayout } from './WorkspaceLayout'

describe('WorkspaceLayout', () => {
  it('renders all structural areas of the Mission Control interface (strict dark mode linear style)', () => {
    render(<WorkspaceLayout />)
    
    expect(screen.getByRole('banner', { name: /mission command bar/i })).toBeInTheDocument()
    expect(screen.getByRole('navigation', { name: /primary sidebar/i })).toBeInTheDocument()
    expect(screen.getByTestId('agent-grid-container')).toBeInTheDocument()
    expect(screen.getByTestId('memory-inspector')).toBeInTheDocument()
  })
})
