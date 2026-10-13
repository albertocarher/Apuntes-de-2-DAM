#!/usr/bin/env python3
"""Autocalificacion: 9 puntos funcionales + 1 de JavaDoc condicionado (>85 %)."""
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

WEIGHTS = {
    'PersonaConsultasTest': 1.5,
    'PersonaCsvTest': 1.5,
    'PersonaJsonTest': 1.5,
    'PersonaXmlTest': 2.5,
    'PropertiesConfigTest': 2.0,
}
SOURCE = Path('src/main/java/es/codelearnacademy/filelab/persona/IPersonaRepository.java')


def normalize(s):
    return s.lower()


def has_any(s, terms):
    return any(normalize(term) in s for term in terms)


def doc_before(text, signature):
    # Debe existir un JavaDoc real inmediatamente antes de la declaracion.
    pattern = r'/\*\*([\s\S]*?)\*/\s*' + signature
    matches = list(re.finditer(pattern, text))
    if not matches:
        return ''
    doc = matches[-1].group(1)
    doc = re.sub(r'^\s*\*\s?', '', doc, flags=re.M)
    if re.search(r'\b(todo|pendiente|rellenar|documentar)\b', normalize(doc)):
        return ''
    return normalize(doc.strip()) if len(doc.strip()) >= 35 else ''


def javadoc_points():
    print('\nDOCUMENTACION JAVADOC — IPersonaRepository (maximo 1,00 punto)')
    if not SOURCE.is_file():
        print('  No se encuentra IPersonaRepository.java: 0,00 puntos')
        return 0.0
    src = SOURCE.read_text(encoding='utf-8')
    interface = doc_before(src, r'public\s+interface\s+IPersonaRepository\b')
    edad = doc_before(src, r'default\s+List\s*<\s*Persona\s*>\s+findByEdadMinima\s*\(')
    activo = doc_before(src, r'default\s+List\s*<\s*Persona\s*>\s+findByActivo\s*\(')

    interface_ok = bool(interface and has_any(interface, ('persona', 'personas')) and has_any(interface, ('repositorio', 'consultas', 'consulta')))
    edad_ok = bool(edad and has_any(edad, ('edad',)) and has_any(edad, ('minima', 'mayor o igual', 'al menos', '>=', 'igual o superior')) and '@param edad' in edad and '@return' in edad)
    activo_ok = bool(activo and has_any(activo, ('activo', 'activa', 'actividad', 'estado')) and has_any(activo, ('true', 'false', 'activo', 'inactivo')) and '@param activo' in activo and '@return' in activo)
    empty_ok = bool(edad and activo and all(has_any(doc, ('vacia', 'vacio', 'sin coincidencias', 'sin resultados', 'no hay coincidencias', 'no existen coincidencias')) for doc in (edad, activo)) and all('@return' in doc for doc in (edad, activo)))
    points = [(interface_ok, 0.2, 'Interfaz: personas + repositorio/consultas'),
              (edad_ok, 0.3, 'Edad: criterio inclusivo + @param edad + @return'),
              (activo_ok, 0.3, 'Estado: activo/inactivo + @param activo + @return'),
              (empty_ok, 0.2, 'Ambos metodos: listas vacias/sin coincidencias')]
    for ok, p, msg in points:
        print(f'  {"OK" if ok else "FALTA"}: {msg} — {p if ok else 0:.2f}/{p:.2f}')
    return round(sum(p for ok, p, _ in points if ok), 2)


def main():
    reports = Path('target/surefire-reports')
    total_pass = total_tests = 0
    points = 0.0
    all_reports = True
    print('AUTOEVALUACION FileLab Personas — 9 puntos funcionales')
    for name, weight in WEIGHTS.items():
        report = reports / f'TEST-es.codelearnacademy.filelab.{name}.xml'
        if not report.is_file():
            print(f'  {name}: SIN INFORME (0/{weight:.1f})')
            all_reports = False
            continue
        try:
            cases = ET.parse(report).getroot().findall('.//testcase')
        except ET.ParseError as exc:
            print(f'  {name}: XML de pruebas ilegible ({exc})')
            all_reports = False
            continue
        n = len(cases)
        if not n:
            all_reports = False
        passed = sum(not any(case.find(kind) is not None for kind in ('failure', 'error', 'skipped')) for case in cases)
        score = weight * passed / n if n else 0.0
        points += score
        total_pass += passed
        total_tests += n
        print(f'  {name}: {passed}/{n} pruebas; {score:.2f}/{weight:.1f} puntos')
    percent = 100 * total_pass / total_tests if total_tests else 0.0
    eligible = all_reports and total_tests > 0 and percent > 85
    print(f'Funcionalidad: {points:.2f}/9.00; pruebas superadas: {total_pass}/{total_tests} ({percent:.2f}%)')
    if eligible:
        print('JavaDoc: ELEGIBLE (mas del 85 % de pruebas funcionales).')
        docs = javadoc_points()
    else:
        print('JavaDoc: NO ELEGIBLE (requiere todos los informes y >85 % de pruebas superadas).')
        docs = 0.0
    print(f'JavaDoc: {docs:.2f}/1.00')
    print(f'NOTA FINAL AUTOMATICA: {points + docs:.2f}/10.00')
    if not all_reports:
        print('AVISO: faltan informes de pruebas. Comprueba los errores de compilacion/ejecucion.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
