import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { Sidebar } from './Sidebar'

describe('Sidebar', () => {
  it('renders all required navigation icons', () => {
    render(<Sidebar />)
    expect(screen.getByRole('button', { name: /workspace/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /files/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /agents/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /missions/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /memory/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /git/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /timeline/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /settings/i })).toBeInTheDocument()
  })
})
