#!/usr/bin/env python3
"""Autocorrector offline para la práctica SQLite de CodeLearn Academy."""
import json, re, sqlite3, sys
from pathlib import Path

BASE = Path(__file__).resolve().parent


def normalizar(valor):
    if isinstance(valor, float):
        return round(valor, 5)
    return valor


def corregir(ruta):
    contenido = ruta.read_text(encoding='utf-8')
    bloques = re.split(r'^--\s*EJERCICIO\s+(\d{1,2})\s*$', contenido, flags=re.M | re.I)
    entregas = {}
    for i in range(1, len(bloques), 2):
        numero = int(bloques[i])
        cuerpo = '\n'.join(l for l in bloques[i+1].splitlines() if not l.lstrip().startswith('--')).strip()
        entregas[numero] = cuerpo
    esperados = json.loads((BASE/'resultados_esperados.json').read_text(encoding='utf-8'))
    correctas = 0
    for ref in esperados:
        num = ref['numero']
        sql = entregas.get(num, '').strip()
        encabezado = f"{num:02d} — {ref['titulo']}"
        if not sql or sql.upper() in ('SELECT ...;', 'SELECT ...'):
            print(f'❌ {encabezado}: sin responder')
            continue
        # Solo lecturas: no se permiten escrituras, cambios de configuración ni adjuntos.
        if not re.match(r'^\s*(SELECT|WITH)\b', sql, flags=re.I):
            print(f'❌ {encabezado}: solo se admiten consultas SELECT / WITH')
            continue
        db = sqlite3.connect(f'file:{(BASE / "codelearn_modelos.sqlite3").as_posix()}?mode=ro', uri=True)
        db.execute('PRAGMA query_only=ON')
        db.set_progress_handler(lambda: 1, 1500000)  # evita bucles y consultas costosas
        def autorizar(action, arg1, arg2, dbname, source):
            permitido = {sqlite3.SQLITE_SELECT, sqlite3.SQLITE_READ, sqlite3.SQLITE_FUNCTION,
                         sqlite3.SQLITE_RECURSIVE}
            return sqlite3.SQLITE_OK if action in permitido else sqlite3.SQLITE_DENY
        db.set_authorizer(autorizar)
        try:
            cur = db.execute(sql)
            cols = [x[0] for x in cur.description]
            filas = [list(map(normalizar, row)) for row in cur.fetchall()]
            objetivo = [list(map(normalizar, row)) for row in ref['filas']]
            if len(cols) != len(ref['columnas']):
                print(f'❌ {encabezado}: columnas incorrectas ({len(cols)} en vez de {len(ref["columnas"])})')
            elif filas == objetivo:
                correctas += 1
                print(f'✅ {encabezado}: correcto')
            else:
                print(f'❌ {encabezado}: el resultado no coincide (obtenidas {len(filas)} filas, esperadas {len(objetivo)})')
        except sqlite3.Error as e:
            print(f'❌ {encabezado}: error SQL: {e}')
        finally:
            db.close()
    print(f'\nRESULTADO: {correctas}/30 consultas correctas')
    return 0 if correctas == len(esperados) else 1


if __name__ == '__main__':
    archivo = Path(sys.argv[1]) if len(sys.argv)>1 else BASE/'respuestas_alumno.sql'
    if not archivo.exists():
        print(f'No existe: {archivo}')
        sys.exit(2)
    sys.exit(corregir(archivo))
