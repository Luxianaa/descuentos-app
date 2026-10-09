export const API_URL: string = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export interface RespuestaDescuento {
  precioOriginal: number
  porcentaje: number
  precioFinal: number
}

export interface InfoApi {
  app: string
  version: string
  commit: string
}

export async function calcularDescuento(
  precio: number,
  porcentaje: number,
): Promise<RespuestaDescuento> {
  const respuesta = await fetch(`${API_URL}/api/descuento`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ precio, porcentaje }),
  })
  const cuerpo = await respuesta.json().catch(() => ({}))
  if (!respuesta.ok) {
    throw new Error(cuerpo.error ?? `Error ${respuesta.status} de la API`)
  }
  return cuerpo as RespuestaDescuento
}

export async function obtenerInfo(): Promise<InfoApi> {
  const respuesta = await fetch(`${API_URL}/api/info`)
  if (!respuesta.ok) throw new Error(`Error ${respuesta.status} de la API`)
  return (await respuesta.json()) as InfoApi
}
