from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent
REPORTS = ROOT / "target" / "surefire-reports"
TARGET = ROOT / "target"
SRC_MAIN = ROOT / "src" / "main" / "java" / "es" / "codelearnacademy" / "filelab"

BLOQUES = {
    "path": ["PathServiceTest"],
    "file": ["FileServiceTest"],
    "files": ["FilesServiceTest"],
    "texto": ["TextFileServiceTest"],
    "producto": ["ProductoValidatorTest", "ProductoServiceTest"],
    "properties": ["PropertiesConfigTest"],
    "csv": ["ProductoCsvRepositoryTest"],
    "json": ["ProductoJsonRepositoryTest"],
    "xml": ["ProductoXmlRepositoryTest"],
    "arquitectura": ["FileFormatTest", "RepositoryFactoryTest", "VehiculoRepositoryTest"],
    "databridge": ["DataBridgeServiceTest"],
}

PESO_TESTS = 8.0
PESO_DOCUMENTACION = 2.0
API_DOCUMENTADA = SRC_MAIN / "repository" / "IRepository.java"


def bloque_de_test(nombre: str):
    nombre_simple = nombre.split(".")[-1]
    for bloque, tests in BLOQUES.items():
        if nombre_simple in tests:
            return bloque
    return None


def leer_tests():
    resumen = {
        bloque: {"total": 0, "passed": 0, "failed": 0}
        for bloque in BLOQUES
    }

    for report in REPORTS.glob("TEST-*.xml"):
        root = ET.parse(report).getroot()
        bloque = bloque_de_test(root.attrib.get("name", report.stem))
        if bloque is None:
            continue

        total = int(root.attrib.get("tests", 0))
        failed = (
            int(root.attrib.get("failures", 0))
            + int(root.attrib.get("errors", 0))
            + int(root.attrib.get("skipped", 0))
        )
        passed = max(0, total - failed)

        resumen[bloque]["total"] += total
        resumen[bloque]["passed"] += passed
        resumen[bloque]["failed"] += failed

    return resumen


def puntuacion_documentacion():
    if not API_DOCUMENTADA.exists():
        return 0.0, ["No existe IRepository.java"]

    texto = API_DOCUMENTADA.read_text(encoding="utf-8")
    patron = re.compile(
        r"(?P<doc>/\*\*[\s\S]*?\*/)?\s*"
        r"(?:List<T>|Optional<T>|boolean)\s+"
        r"(?P<nombre>findAll|findById|create|update|delete)\s*"
        r"\((?P<params>[^)]*)\)\s*;"
    )

    encontrados = list(patron.finditer(texto))
    if len(encontrados) != 5:
        return 0.0, ["No se han detectado los cinco métodos de IRepository"]

    total = 0.0
    observaciones = []

    for match in encontrados:
        doc = match.group("doc") or ""
        metodo = match.group("nombre")
        params = match.group("params").strip()

        puntos = 0.0

        if len(re.sub(r"[/\*\s]", "", doc)) >= 20:
            puntos += 0.50
        else:
            observaciones.append(f"{metodo}: descripción insuficiente")

        if params:
            nombres = []
            for param in params.split(","):
                partes = param.strip().split()
                if partes:
                    nombres.append(partes[-1])

            if all(re.search(rf"@param\s+{re.escape(nombre)}\b", doc)
                   for nombre in nombres):
                puntos += 0.25
            else:
                observaciones.append(f"{metodo}: falta @param")
        else:
            puntos += 0.25

        if "@return" in doc:
            puntos += 0.25
        else:
            observaciones.append(f"{metodo}: falta @return")

        total += puntos

    return round((total / 5) * 10, 2), observaciones


def main():
    resumen = leer_tests()

    total_tests = sum(datos["total"] for datos in resumen.values())
    total_passed = sum(datos["passed"] for datos in resumen.values())

    nota_tests = 0.0 if total_tests == 0 else (total_passed / total_tests) * 10
    aportacion_tests = nota_tests / 10 * PESO_TESTS

    nota_doc, observaciones_doc = puntuacion_documentacion()
    aportacion_doc = nota_doc / 10 * PESO_DOCUMENTACION

    nota_final = aportacion_tests + aportacion_doc

    lineas = ["=== NOTA POR BLOQUES ===", ""]

    for bloque, datos in resumen.items():
        total = datos["total"]
        passed = datos["passed"]
        failed = datos["failed"]
        nota = 0.0 if total == 0 else passed / total * 10
        lineas.append(
            f"{bloque.upper():12} {nota:5.2f}/10  "
            f"({passed}/{total}, {failed} no superados)"
        )

    lineas += [
        "",
        "=== DOCUMENTACIÓN ===",
        f"JavaDoc IRepository: {nota_doc:.2f}/10",
    ]

    for observacion in observaciones_doc:
        lineas.append(f"- {observacion}")

    lineas += [
        "",
        "=== RESUMEN ===",
        f"Tests:          {nota_tests:.2f}/10 -> {aportacion_tests:.2f}/{PESO_TESTS:.2f}",
        f"Documentación:  {nota_doc:.2f}/10 -> {aportacion_doc:.2f}/{PESO_DOCUMENTACION:.2f}",
        f"NOTA FINAL:     {nota_final:.2f}/10",
        "",
    ]

    informe = "\n".join(lineas)
    TARGET.mkdir(parents=True, exist_ok=True)
    (TARGET / "nota.txt").write_text(informe, encoding="utf-8")
    print(informe)


if __name__ == "__main__":
    main()
