/** Validacion y formato del lado del navegador. Las reglas son las mismas que aplica la API. */

export function validar(precio: number, porcentaje: number): string | null {
  if (!Number.isFinite(precio) || precio <= 0) {
    return 'El precio original debe ser mayor que cero'
  }
  if (!Number.isFinite(porcentaje) || porcentaje < 0 || porcentaje > 100) {
    return 'El porcentaje de descuento debe estar entre 0 y 100'
  }
  return null
}

/** 30 -> "Bs 30,00" (coma decimal, siempre dos decimales; sin depender del idioma del navegador). */
export function formatearBs(valor: number): string {
  return `Bs ${valor.toFixed(2).replace('.', ',')}`
}
