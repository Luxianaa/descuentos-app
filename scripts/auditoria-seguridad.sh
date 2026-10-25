#!/usr/bin/env bash
# Auditoria de seguridad de la API (y opcionalmente del front-end): comprobaciones rapidas con curl.
# Uso:  scripts/auditoria-seguridad.sh API_URL [WEB_URL]
# Cada comprobacion imprime OK o FALLO y el script termina con codigo 1 si alguna falla.
# No sustituye a ZAP: es la red de seguridad rapida y legible que corre antes del escaneo completo.
set -u
API="${1:?Uso: auditoria-seguridad.sh API_URL [WEB_URL]}"; API="${API%/}"
WEB="${2:-}"; WEB="${WEB%/}"
ORIGEN_MALO="https://sitio-malicioso.example"
fallos=0
RESUMEN="${GITHUB_STEP_SUMMARY:-/dev/null}"
{ echo "### Auditoria de seguridad"; echo "| Control | Resultado |"; echo "|---|---|"; } >> "$RESUMEN"

ok()    { echo "OK     $1"; echo "| $1 | ✅ |" >> "$RESUMEN"; }
fallo() { echo "FALLO  $1"; echo "::error title=Seguridad::$1"; echo "| $1 | ❌ |" >> "$RESUMEN"; fallos=1; }
# minusculas de las cabeceras de una respuesta (sin CR)
cabeceras() { curl -sS --max-time 20 -D - -o /dev/null "$@" | tr -d '\r' | tr '[:upper:]' '[:lower:]'; }
cuerpo()    { curl -sS --max-time 20 "$@"; }
codigo()    { curl -sS --max-time 20 -o /dev/null -w '%{http_code}' "$@"; }

# 1. Cabeceras de seguridad de la API (OWASP Secure Headers)
H="$(cabeceras "$API/api/info")"
for par in "x-content-type-options: nosniff" "x-frame-options: deny" "content-security-policy:" "referrer-policy:" "cache-control: no-store"; do
  if grep -q "^$par" <<<"$H"; then ok "API: cabecera '${par%%:*}' presente"; else fallo "API: falta la cabecera '${par%%:*}'"; fi
done
if grep -q '^strict-transport-security:' <<<"$H"; then ok "API: cabecera 'strict-transport-security' presente"; else fallo "API: falta la cabecera 'strict-transport-security'"; fi

# 2. No anunciar tecnologia ni version
if grep -Eq '^x-powered-by:|^server:.*[0-9]' <<<"$H"; then fallo "API: la respuesta anuncia tecnologia o version (Server / X-Powered-By)"; else ok "API: no anuncia tecnologia ni version"; fi

# 3. CORS: un origen desconocido no debe recibir permiso
HC="$(cabeceras -X OPTIONS "$API/api/descuento" -H "Origin: $ORIGEN_MALO" -H 'Access-Control-Request-Method: POST')"
if grep -Eq "^access-control-allow-origin: (\*|$ORIGEN_MALO)" <<<"$HC"; then fallo "CORS: la API acepta el origen $ORIGEN_MALO"; else ok "CORS: un origen desconocido no recibe permiso"; fi
if grep -q '^access-control-allow-credentials: true' <<<"$HC"; then fallo "CORS: permite credenciales a un origen desconocido"; fi

# 4. Los errores no filtran informacion interna
PATRON='Exception|stackTrace|"trace"|org\.springframework|bo\.edu|\.java|Whitelabel|at [a-z]+\.[a-z]+\.'
B1="$(cuerpo -X POST "$API/api/descuento" -H 'Content-Type: application/json' -d '{esto no es json')"
if grep -Eq "$PATRON" <<<"$B1"; then fallo "Errores: un cuerpo ilegible revela detalles internos"; else ok "Errores: un cuerpo ilegible no revela detalles internos"; fi
B2="$(cuerpo "$API/api/ruta-que-no-existe")"
if grep -Eq "$PATRON" <<<"$B2"; then fallo "Errores: una ruta inexistente revela detalles internos"; else ok "Errores: una ruta inexistente no revela detalles internos"; fi

# 5. Control de acceso: la ruta de administracion exige token
C1="$(codigo "$API/api/admin/estadisticas")"
if [ "$C1" = "200" ]; then fallo "Acceso: /api/admin/estadisticas responde 200 SIN token"; else ok "Acceso: /api/admin/estadisticas sin token responde $C1"; fi
C2="$(codigo "$API/api/admin/estadisticas" -H 'X-Admin-Token: token-incorrecto-0123456789')"
if [ "$C2" = "200" ]; then fallo "Acceso: /api/admin/estadisticas responde 200 con un token incorrecto"; else ok "Acceso: con un token incorrecto responde $C2"; fi

# 6. Entrada acotada: un precio absurdo se rechaza
C3="$(codigo -X POST "$API/api/descuento" -H 'Content-Type: application/json' -d '{"precio":1e300,"porcentaje":10}')"
if [ "$C3" = "400" ]; then ok "Entrada: un precio de 1e300 se rechaza con 400"; else fallo "Entrada: un precio de 1e300 responde $C3 (se esperaba 400)"; fi

# 7. Actuator: solo health y sin detalles
for ruta in env heapdump beans mappings; do
  C="$(codigo "$API/actuator/$ruta")"
  if [ "$C" = "200" ]; then fallo "Actuator: /actuator/$ruta es publico"; else ok "Actuator: /actuator/$ruta no es publico ($C)"; fi
done
if cuerpo "$API/actuator/health" | grep -q 'diskSpace'; then fallo "Actuator: /actuator/health muestra detalles internos"; else ok "Actuator: /actuator/health sin detalles"; fi

# 8. Front-end (si se indico)
if [ -n "$WEB" ]; then
  HW="$(cabeceras "$WEB/")"
  for par in "x-content-type-options: nosniff" "x-frame-options: deny" "content-security-policy:" "referrer-policy:"; do
    if grep -q "^$par" <<<"$HW"; then ok "Web: cabecera '${par%%:*}' presente"; else fallo "Web: falta la cabecera '${par%%:*}'"; fi
  done
  case "$WEB" in https://*) if grep -q '^strict-transport-security:' <<<"$HW"; then ok "Web: HSTS presente"; else fallo "Web: falta HSTS"; fi ;; esac
  if cuerpo "$WEB/" | grep -Eiq 'admin[_-]?token|api[_-]?key|secret'; then fallo "Web: el HTML publicado contiene algo parecido a un secreto"; else ok "Web: el HTML no contiene secretos evidentes"; fi
fi

[ $fallos -eq 0 ] && echo "Auditoria: todo en orden" || echo "Auditoria: HAY FALLOS"
exit $fallos
