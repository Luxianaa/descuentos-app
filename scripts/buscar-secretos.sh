#!/usr/bin/env bash
# Busca secretos evidentes en los archivos versionados (rapido, sin instalar nada).
# Uso: scripts/buscar-secretos.sh        (desde la raiz del repositorio)
# No sustituye a gitleaks: no revisa el historial ni usa reglas por proveedor. Sirve como primer filtro y para clase.
set -u
cd "$(git rev-parse --show-toplevel 2>/dev/null || pwd)" || exit 1
# nombre que sugiere un secreto  =  valor literal de 16+ caracteres (no una referencia ${...} ni un secreto de Actions)
REGEX='(token|secret|passw(or)?d|api[_-]?key|private[_-]?key)[A-Za-z0-9_.-]*[[:space:]]*[=:][[:space:]]*["'"'"']?[A-Za-z0-9+/_-]{16,}'
if git rev-parse --git-dir >/dev/null 2>&1; then
  archivos="$(git ls-files)"
else
  archivos="$(find . -type f -not -path '*/node_modules/*' -not -path '*/.git/*' -not -path '*/target/*' -not -path './web/dist/*' | sed 's|^\./||')"
fi
# shellcheck disable=SC2016
hallazgos="$(echo "$archivos" | grep -vE '(package-lock\.json|\.png|\.jpg|\.pdf)$' | grep -vE '^scripts/buscar-secretos\.sh$' | while read -r f; do
  [ -f "$f" ] && grep -InE -i "$REGEX" "$f" /dev/null | grep -vF '${' | grep -viE 'secrets\.|example|ejemplo|changeme|prueba|incorrecto'
done)"
if [ -n "$hallazgos" ]; then
  echo "POSIBLES SECRETOS (archivo:linea; el valor no se imprime):"; echo "$hallazgos" | cut -d: -f1,2
  echo "::error title=Secretos::Hay valores que parecen secretos en archivos versionados"
  exit 1
fi
echo "Sin secretos evidentes en los archivos versionados"
