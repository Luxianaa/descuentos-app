#!/usr/bin/env python3
"""Suma las pruebas de los informes JUnit XML, las escribe como tabla Markdown y
falla si no se ejecuto ninguna: un pipeline que no prueba nada no debe quedar en verde.

Uso: resumen-pruebas.py "Titulo" "ruta/o/patron/*.xml"
"""
import glob
import sys
import xml.etree.ElementTree as ET

titulo, patron = sys.argv[1], sys.argv[2]
archivos = sorted(glob.glob(patron))
total = fallos = errores = omitidas = 0
for ruta in archivos:
    for suite in ET.parse(ruta).getroot().iter("testsuite"):
        total += int(suite.get("tests", 0))
        fallos += int(suite.get("failures", 0))
        errores += int(suite.get("errors", 0))
        omitidas += int(suite.get("skipped", 0))

print(f"### {titulo}")
print("| Pruebas ejecutadas | Fallos | Errores | Omitidas | Informes |")
print("|---|---|---|---|---|")
print(f"| {total} | {fallos} | {errores} | {omitidas} | {len(archivos)} |")
print()
if total == 0:
    print("::error title=Cero pruebas ejecutadas::" + titulo + ": no se encontro ninguna prueba en " + patron, file=sys.stderr)
    sys.exit(1)
