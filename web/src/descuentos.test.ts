import { describe, expect, it } from 'vitest'
import { formatearBs, validar } from './descuentos'

describe('validar', () => {
  it('acepta un precio y un porcentaje validos', () => {
    expect(validar(100, 10)).toBeNull()
  })

  it.each([0, 100])('acepta los extremos %s %%', (porcentaje) => {
    expect(validar(50, porcentaje)).toBeNull()
  })

  it.each([0, -5, NaN])('rechaza el precio %s', (precio) => {
    expect(validar(precio, 10)).toBe('El precio original debe ser mayor que cero')
  })

  it.each([-1, 100.01, 150, NaN])('rechaza el porcentaje %s', (porcentaje) => {
    expect(validar(100, porcentaje)).toBe('El porcentaje de descuento debe estar entre 0 y 100')
  })
})

describe('formatearBs', () => {
  it.each([
    [30, 'Bs 30,00'],
    [16.99, 'Bs 16,99'],
    [0, 'Bs 0,00'],
    [1234.5, 'Bs 1234,50'],
  ])('formatea %s como %s', (valor, esperado) => {
    expect(formatearBs(valor)).toBe(esperado)
  })
})
