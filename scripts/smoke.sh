#!/usr/bin/env bash
# Smoke test post-despliegue: comprueba que lo desplegado funciona y es el commit esperado.
# shellcheck disable=SC2329
# Uso:  scripts/smoke.sh API_URL WEB_URL COMMIT_SHA
# Variable opcional: SMOKE_API_COMMIT=0 omite la comprobacion del commit de la API (en un preview la API es la de produccion).
set -u
API="${1%/}"; WEB="${2%/}"; SHA="$3"
VERIFICAR_API_COMMIT="${SMOKE_API_COMMIT:-1}"
fallos=0
RESUMEN="${GITHUB_STEP_SUMMARY:-/dev/null}"
{ echo "### Smoke test"; echo "| Comprobacion | Resultado |"; echo "|---|---|"; } >> "$RESUMEN"

# reintenta hasta 15 veces (cada 8 s): el plan gratuito de Render tarda en despertar
REINTENTOS="${SMOKE_REINTENTOS:-15}"; ESPERA="${SMOKE_ESPERA:-8}"
esperar() { # esperar "descripcion" comando...
  local desc="$1"; shift
  for _ in $(seq 1 "$REINTENTOS"); do
    if "$@"; then echo "OK  $desc"; echo "| $desc | ✅ |" >> "$RESUMEN"; return 0; fi
    sleep "$ESPERA"
  done
  echo "::error title=Smoke test::FALLO $desc"; echo "| $desc | ❌ |" >> "$RESUMEN"; fallos=1; return 1
}

api_arriba()   { curl -fsS --max-time 20 "$API/actuator/health" | grep -q '"status":"UP"'; }
api_commit()   { [ "$(curl -fsS --max-time 20 "$API/api/info" | jq -r .commit)" = "$SHA" ]; }
api_calcula()  { curl -fsS --max-time 20 -X POST "$API/api/descuento" -H 'Content-Type: application/json' -d '{"precio":200,"porcentaje":25}' | jq -e '.precioFinal == 150' > /dev/null; }
cors_permite() { curl -sS --max-time 20 -i -X OPTIONS "$API/api/descuento" -H "Origin: $WEB" -H 'Access-Control-Request-Method: POST' -H 'Access-Control-Request-Headers: content-type' | tr -d '\r' | grep -iq "^access-control-allow-origin: $WEB$"; }
web_responde() { curl -fsS --max-time 20 "$WEB/" | grep -q 'id="root"'; }
web_commit()   { curl -fsS --max-time 20 "$WEB/" | grep -q "name=\"commit\" content=\"$SHA\""; }

esperar "La API responde /actuator/health = UP" api_arriba
[ "$VERIFICAR_API_COMMIT" = "1" ] && esperar "La API corre el commit ${SHA:0:7}" api_commit
esperar "POST /api/descuento: 200 Bs con 25 % = 150" api_calcula
esperar "CORS: la API acepta el origen $WEB" cors_permite
esperar "El front-end responde en $WEB" web_responde
esperar "El front-end desplegado es el commit ${SHA:0:7}" web_commit

[ $fallos -eq 0 ] && echo "Smoke test: todo en orden" || echo "Smoke test: HAY FALLOS"
exit $fallos
