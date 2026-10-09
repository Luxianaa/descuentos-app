import { useEffect, useState } from 'react'
import { calcularDescuento, obtenerInfo, type InfoApi, type RespuestaDescuento } from './api'
import { formatearBs, validar } from './descuentos'
import './App.css'

export default function App() {
  const [precio, setPrecio] = useState('100')
  const [porcentaje, setPorcentaje] = useState('10')
  const [resultado, setResultado] = useState<RespuestaDescuento | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [cargando, setCargando] = useState(false)
  const [info, setInfo] = useState<InfoApi | null>(null)

  useEffect(() => {
    obtenerInfo()
      .then(setInfo)
      .catch(() => setInfo(null))
  }, [])

  async function enviar(evento: React.FormEvent) {
    evento.preventDefault()
    setResultado(null)
    const problema = validar(Number(precio), Number(porcentaje))
    if (problema) {
      setError(problema)
      return
    }
    setError(null)
    setCargando(true)
    try {
      setResultado(await calcularDescuento(Number(precio), Number(porcentaje)))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error desconocido')
    } finally {
      setCargando(false)
    }
  }

  return (
    <main>
      <h1>Calculadora de Descuentos</h1>
      <p className="sub">COM450 · Calidad de Software · Práctica 8</p>

      <form onSubmit={enviar}>
        <label>
          Precio original (Bs)
          <input
            data-cy="precio"
            type="number"
            step="any"
            value={precio}
            onChange={(e) => setPrecio(e.target.value)}
          />
        </label>
        <label>
          Descuento (%)
          <input
            data-cy="porcentaje"
            type="number"
            step="any"
            value={porcentaje}
            onChange={(e) => setPorcentaje(e.target.value)}
          />
        </label>
        <button data-cy="calcular" type="submit" disabled={cargando}>
          {cargando ? 'Calculando…' : 'Calcular'}
        </button>
      </form>

      {resultado && (
        <p data-cy="resultado" className="ok">
          Precio final: <strong>{formatearBs(resultado.precioFinal)}</strong>
        </p>
      )}
      {error && (
        <p data-cy="error" role="alert" className="error">
          {error}
        </p>
      )}

      <footer data-cy="version">
        {info ? `API ${info.version} · commit ${info.commit.slice(0, 7)}` : 'API sin conexión'}
      </footer>
    </main>
  )
}
