import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { AgentGrid } from './AgentGrid'

describe('AgentGrid', () => {
  it('renders a 4-pane multiplexer layout with provider badges', () => {
    render(<AgentGrid />)
    
    // We expect some agent panes
    expect(screen.getAllByTestId('agent-pane')).toHaveLength(4)
    
    // Based on the requirement header: '🟢 Backend Engineer | Claude Opus | Thinking... | Cost | Elapsed'
    expect(screen.getAllByText(/Backend Engineer/i)[0]).toBeInTheDocument()
    expect(screen.getAllByText(/Claude Opus/i)[0]).toBeInTheDocument()
  })
})
