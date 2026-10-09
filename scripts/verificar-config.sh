#!/usr/bin/env bash
# Falla con un mensaje claro si falta algun secreto o variable.
# Uso: scripts/verificar-config.sh NOMBRE1 NOMBRE2 ...   (los valores deben estar en el entorno del paso)
faltan=0
for nombre in "$@"; do
  if [ -z "${!nombre:-}" ]; then
    echo "::error title=Falta configuracion::No hay valor para ${nombre}. Revisa Settings > Secrets and variables > Actions (y que el nombre coincida EXACTAMENTE con el del workflow)."
    faltan=1
  else
    echo "OK  ${nombre} esta definido"
  fi
done
exit $faltan
