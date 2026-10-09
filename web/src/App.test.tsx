import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'

const respuesta = (cuerpo: unknown, ok = true, status = 200) =>
  Promise.resolve({ ok, status, json: () => Promise.resolve(cuerpo) } as Response)

describe('App', () => {
  beforeEach(() => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) =>
        url.endsWith('/api/info')
          ? respuesta({ app: 'descuentos-api', version: '1.0.0', commit: 'abc1234def' })
          : respuesta({ precioOriginal: 100, porcentaje: 10, precioFinal: 90 }),
      ),
    )
  })
  afterEach(() => vi.unstubAllGlobals())

  it('muestra la version y el commit de la API', async () => {
    render(<App />)
    expect(await screen.findByText('API 1.0.0 · commit abc1234')).toBeInTheDocument()
  })

  it('calcula el precio final con la API', async () => {
    render(<App />)
    await userEvent.click(screen.getByRole('button', { name: 'Calcular' }))
    expect(await screen.findByText('Bs 90,00')).toBeInTheDocument()
  })

  it('no llama a la API si el dato es invalido y muestra el error', async () => {
    render(<App />)
    await userEvent.clear(screen.getByLabelText('Descuento (%)'))
    await userEvent.type(screen.getByLabelText('Descuento (%)'), '150')
    await userEvent.click(screen.getByRole('button', { name: 'Calcular' }))
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'El porcentaje de descuento debe estar entre 0 y 100',
    )
    expect(fetch).toHaveBeenCalledTimes(1) // solo la llamada a /api/info
  })

  it('muestra el mensaje de error que devuelve la API', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) =>
        url.endsWith('/api/info')
          ? respuesta({ app: 'x', version: '1', commit: 'abcdefg' })
          : respuesta({ error: 'Mensaje de la API' }, false, 400),
      ),
    )
    render(<App />)
    await userEvent.click(screen.getByRole('button', { name: 'Calcular' }))
    expect(await screen.findByRole('alert')).toHaveTextContent('Mensaje de la API')
  })

  it('indica que la API no responde', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(() => Promise.reject(new Error('red caida'))),
    )
    render(<App />)
    expect(await screen.findByText('API sin conexión')).toBeInTheDocument()
  })
})
