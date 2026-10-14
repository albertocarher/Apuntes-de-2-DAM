# Practica con SQLite3 

Esta carpeta contiene una base de datos con **100 productos y 100 vehículos de ejemplo** (datos ficticios), 30 consultas resueltas y autocorrección local.

## Archivos

- `README.md`: 30 consultas resueltas, su objetivo, los conceptos SQL y las salidas reales.
- `codelearn_modelos.sqlite3`: base de datos de trabajo.
- `crear_base_de_datos.sql`: recrea las tablas y carga los datos desde cero.
- `respuestas_alumno.sql`: plantilla con las 30 respuestas pendientes.
- `corregir.py`: autocorrector sin dependencias adicionales (requiere Python 3).
- `resultados_esperados.json`: salidas de referencia para el autocorrector.

## Cómo trabajar

1. Abre la base `codelearn_modelos.sqlite3` en DB Browser for SQLite o utiliza la terminal SQLite.
2. Trabaja las actividades del `README.md`. Para practicar sin mirar las soluciones, empieza por los enunciados del índice.
3. Escribe una sentencia por ejercicio en `respuestas_alumno.sql`, sustituyendo `SELECT ...;`. Mantén la etiqueta `-- EJERCICIO 01`, etc.
4. Abre una terminal **dentro de esta carpeta** y ejecuta:

```bash
python corregir.py respuestas_alumno.sql
```

En algunos sistemas el comando es `python3` en lugar de `python`.

## Cómo funciona la autocorrección

Se compara el **resultado** de cada consulta con la salida esperada calculada previamente sobre esta base: número de columnas y filas, contenido, y orden de filas. Se toleran pequeñas diferencias en decimales redondeando a cinco cifras.

Se utilizan una conexión SQLite **de solo lectura** y restricciones para impedir que los ejercicios modifiquen los datos. Los errores SQL se notifican por ejercicio.

**Limitaciones:** se corrige el resultado sobre estos datos, no la lógica general de la sentencia. Dos consultas distintas pueden dar la misma salida por casualidad. Además, el corrector exige el orden de filas del ejemplo (incluso cuando SQL no lo garantiza sin `ORDER BY`) y no evalúa los nombres de columna. Es una herramienta de práctica y retroalimentación, **no un sistema de evaluación a prueba de trampas**: las soluciones y el JSON de referencia están incluidos y son visibles. Para evaluación oficial, el docente debería ejecutar pruebas adicionales con bases de datos alternativas y revisar el SQL.

## Nota sobre el modelo

Los modelos se adaptan a los campos `Productos(id, nombre, precio)` y `Vehiculos(matricula, marca, modelo, anio)`; no hay una relación entre las dos tablas. Los datos de ejemplo son ficticios, no un conjunto de datos oficiales descargado de CodeLearn Academy.

---

# 30 consultas SQL resueltas — CodeLearn Academy

> **Base de datos:** `codelearn_modelos.sqlite3` · **SQLite3** · Tablas independientes: `Productos(id, nombre, precio)` y `Vehiculos(matricula, marca, modelo, anio)`.

## Cómo utilizar este material

Cada ejercicio indica **qué se pretende aprender**, los **conceptos** implicados, la **consulta ejecutable** y la **salida real** de la base de datos. Las consultas solo leen datos. En las consultas con `LIMIT`, la tabla muestra únicamente el número de filas solicitado; por tanto, no necesariamente es el conjunto completo de coincidencias. Algunos análisis de familias usan la primera palabra del nombre como aproximación didáctica, no una categoría oficial.

## Índice

| N.º | Nivel | Objetivo | Conceptos |
|---:|---|---|---|
| 1 | Básico | Mostrar los cinco primeros productos por identificador. | `SELECT, ORDER BY, LIMIT` |
| 2 | Básico | Ver los cinco vehículos más recientes. | `ORDER BY DESC, LIMIT` |
| 3 | Básico | Filtrar productos cuyo precio sea mayor de 100. | `WHERE, comparación` |
| 4 | Básico | Localizar vehículos de la marca Toyota. | `WHERE, =` |
| 5 | Básico | Buscar productos cuyo nombre contenga Pro. | `LIKE, %` |
| 6 | Básico | Mostrar productos con precio comprendido entre 20 y 50. | `BETWEEN, AND` |
| 7 | Básico | Listar marcas diferentes sin repetir. | `DISTINCT, ORDER BY` |
| 8 | Básico | Seleccionar vehículos de dos marcas concretas. | `IN, WHERE` |
| 9 | Agregación | Contar cuántos productos existen. | `COUNT(*)` |
| 10 | Agregación | Contar cuántos vehículos están registrados. | `COUNT(*)` |
| 11 | Agregación | Obtener precio mínimo, máximo y medio. | `MIN, MAX, AVG, ROUND` |
| 12 | Agregación | Contar las marcas diferentes. | `COUNT(DISTINCT)` |
| 13 | Agregación | Calcular la suma de todos los precios del catálogo. | `SUM, ROUND` |
| 14 | Agregación | Contar productos con precio de al menos 50. | `COUNT, WHERE` |
| 15 | Agrupación | Contar vehículos de cada marca. | `GROUP BY, COUNT` |
| 16 | Agrupación | Encontrar los años de fabricación con más vehículos. | `GROUP BY, COUNT` |
| 17 | Agrupación | Calcular año más antiguo y reciente por marca. | `GROUP BY, MIN, MAX` |
| 18 | Agrupación | Contar productos por tramos de precio. | `CASE, GROUP BY` |
| 19 | Agrupación | Filtrar marcas con al menos cinco vehículos. | `GROUP BY, HAVING` |
| 20 | Agrupación | Mostrar años en los que hay al menos tres vehículos. | `GROUP BY, HAVING` |
| 21 | Intermedio | Mostrar el precio medio por familia, usando la primera palabra del nombre. | `substr, instr, CASE, GROUP BY, AVG` |
| 22 | Intermedio | Mostrar familias que tengan al menos cuatro productos y precio medio superior a 35. | `CTE, GROUP BY, HAVING` |
| 23 | Intermedio | Mostrar productos más caros que la media general. | `Subconsulta, AVG` |
| 24 | Intermedio | Listar los modelos de los vehículos posteriores al año 2020 agrupados por marca. | `WHERE, GROUP BY, COUNT` |
| 25 | Intermedio | Detectar los productos empatados con el precio máximo. | `Subconsulta, MAX` |
| 26 | Avanzado | Clasificar productos según el precio usando un ranking. | `RANK() OVER, ORDER BY` |
| 27 | Avanzado | Calcular un total acumulado de precios ordenados por identificador. | `SUM() OVER, ventana` |
| 28 | Avanzado | Comparar cada producto con el precio medio de su familia. | `CTE, AVG() OVER (PARTITION BY)` |
| 29 | Avanzado | Mostrar el vehículo más reciente de cada marca, incluyendo empates. | `CTE, RANK(), PARTITION BY` |
| 30 | Avanzado | Resumir por marca el total, año medio y antigüedad, conservando solo marcas con al menos tres vehículos. | `CTE, GROUP BY, HAVING, ROUND, MAX, MIN` |

## 01. Mostrar los cinco primeros productos por identificador.

**Nivel:** Básico  
**Qué se pretende:** Mostrar los cinco primeros productos por identificador.  
**Conceptos:** `SELECT`, `ORDER BY`, `LIMIT`

```sql
SELECT id, nombre, precio FROM Productos ORDER BY id LIMIT 5;
```

**Resultado de la ejecución:**

| id | nombre | precio |
| --- | --- | --- |
| 1 | Teclado Básico | 17.68 |
| 2 | Teclado Plus | 17.4 |
| 3 | Teclado Pro | 30.87 |
| 4 | Teclado Compacto | 37.69 |
| 5 | Teclado Premium | 41.44 |

**Clave para resolverla:** LIMIT reduce el número de filas; ORDER BY hace que la selección sea reproducible.

## 02. Ver los cinco vehículos más recientes.

**Nivel:** Básico  
**Qué se pretende:** Ver los cinco vehículos más recientes.  
**Conceptos:** `ORDER BY DESC`, `LIMIT`


**Resultado de la ejecución:**

| matricula | marca | modelo | anio |
| --- | --- | --- | --- |
| 1003BBE | Toyota | Prius | 2026 |
| 1025BBG | Renault | Mégane | 2026 |
| 1047BCI | Hyundai | Kona | 2026 |
| 1069BDK | Volkswagen | Polo | 2026 |
| 1091BEM | Honda | CR-V | 2026 |

**Clave para resolverla:** DESC ordena de mayor a menor.

## 03. Filtrar productos cuyo precio sea mayor de 100.

**Nivel:** Básico  
**Qué se pretende:** Filtrar productos cuyo precio sea mayor de 100.  
**Conceptos:** `WHERE`, `comparación`

**Resultado de la ejecución:**

| id | nombre | precio |
| --- | --- | --- |
| 100 | Tableta gráfica Premium | 251.59 |
| 99 | Tableta gráfica Compacto | 246.71 |
| 95 | Switch Premium | 246.0 |
| 98 | Tableta gráfica Pro | 243.88 |
| 94 | Switch Compacto | 232.78 |
| 93 | Switch Pro | 232.75 |
| 96 | Tableta gráfica Básico | 231.39 |
| 97 | Tableta gráfica Plus | 229.02 |

**Clave para resolverla:** WHERE filtra filas antes de mostrarlas.

## 04. Localizar vehículos de la marca Toyota.

**Nivel:** Básico  
**Qué se pretende:** Localizar vehículos de la marca Toyota.  
**Conceptos:** `WHERE`, `=`


**Resultado de la ejecución:**

| matricula | marca | modelo | anio |
| --- | --- | --- | --- |
| 1003BBE | Toyota | Prius | 2026 |
| 1006BBH | Toyota | RAV4 | 2025 |
| 1009BBK | Toyota | Yaris | 2024 |
| 1002BBD | Toyota | RAV4 | 2019 |
| 1005BBG | Toyota | Yaris | 2018 |
| 1008BBJ | Toyota | Corolla | 2017 |
| 1001BBC | Toyota | Yaris | 2012 |
| 1004BBF | Toyota | Corolla | 2011 |

**Clave para resolverla:** Los valores de texto se escriben entre comillas simples.

## 05. Buscar productos cuyo nombre contenga Pro.

**Nivel:** Básico  
**Qué se pretende:** Buscar productos cuyo nombre contenga Pro.  
**Conceptos:** `LIKE`, `%` o expreción regula

**Resultado de la ejecución:**

| id | nombre | precio |
| --- | --- | --- |
| 3 | Teclado Pro | 30.87 |
| 8 | Ratón Pro | 39.16 |
| 13 | Monitor Pro | 49.36 |
| 18 | Auriculares Pro | 66.95 |
| 23 | Webcam Pro | 69.61 |
| 28 | Micrófono Pro | 78.98 |
| 33 | Altavoz Pro | 100.0 |
| 38 | Impresora Pro | 101.77 |

**Clave para resolverla:** % significa cero o más caracteres.

## 06. Mostrar productos con precio comprendido entre 20 y 50.

**Nivel:** Básico  
**Qué se pretende:** Mostrar productos con precio comprendido entre 20 y 50.  
**Conceptos:** `BETWEEN`, `AND`

**Resultado de la ejecución:**

| id | nombre | precio |
| --- | --- | --- |
| 6 | Ratón Básico | 24.12 |
| 7 | Ratón Plus | 30.8 |
| 3 | Teclado Pro | 30.87 |
| 11 | Monitor Básico | 36.58 |
| 4 | Teclado Compacto | 37.69 |
| 8 | Ratón Pro | 39.16 |
| 12 | Monitor Plus | 41.43 |
| 5 | Teclado Premium | 41.44 |

**Clave para resolverla:** BETWEEN incluye ambos extremos.

## 07. Listar marcas diferentes sin repetir.

**Nivel:** Básico  
**Qué se pretende:** Listar marcas diferentes sin repetir.  
**Conceptos:** `DISTINCT`, `ORDER BY`

**Resultado de la ejecución:**

| marca |
| --- |
| Ford |
| Honda |
| Hyundai |
| Kia |
| Opel |
| Peugeot |
| Renault |
| SEAT |
| Toyota |
| Volkswagen |

**Clave para resolverla:** DISTINCT elimina valores duplicados de la salida.

## 08. Seleccionar vehículos de dos marcas concretas.

**Nivel:** Básico  
**Qué se pretende:** Seleccionar vehículos de dos marcas concretas.  
**Conceptos:** `IN`, `WHERE`

**Resultado de la ejecución:**

| matricula | marca | modelo | anio |
| --- | --- | --- | --- |
| 1031BCM | Ford | Puma | 2024 |
| 1034BCP | Ford | Kuga | 2023 |
| 1037BCS | Ford | Fiesta | 2022 |
| 1030BCL | Ford | Kuga | 2017 |
| 1033BCO | Ford | Fiesta | 2016 |
| 1036BCR | Ford | Focus | 2015 |
| 1039BCU | Ford | Puma | 2014 |
| 1032BCN | Ford | Focus | 2009 |

**Clave para resolverla:** IN evita escribir varios OR.

## 09. Contar cuántos productos existen.

**Nivel:** Agregación  
**Qué se pretende:** Contar cuántos productos existen.  
**Conceptos:** `COUNT(*)`

**Resultado de la ejecución:**

| total_productos |
| --- |
| 100 |

**Clave para resolverla:** COUNT(*) cuenta todas las filas.

## 10. Contar cuántos vehículos están registrados.

**Nivel:** Agregación  
**Qué se pretende:** Contar cuántos vehículos están registrados.  
**Conceptos:** `COUNT(*)`

**Resultado de la ejecución:**

| total_vehiculos |
| --- |
| 100 |

**Clave para resolverla:** Compara el resultado con el total esperado de registros.

## 11. Obtener precio mínimo, máximo y medio.

**Nivel:** Agregación  
**Qué se pretende:** Obtener precio mínimo, máximo y medio.  
**Conceptos:** `MIN`, `MAX`, `AVG`, `ROUND`

**Resultado de la ejecución:**

| minimo | maximo | media |
| --- | --- | --- |
| 17.4 | 251.59 | 133.86 |

**Clave para resolverla:** AVG calcula la media; ROUND redondea para mostrarla.

## 12. Contar las marcas diferentes.

**Nivel:** Agregación  
**Qué se pretende:** Contar las marcas diferentes.  
**Conceptos:** `COUNT(DISTINCT)`

**Resultado de la ejecución:**

| marcas_diferentes |
| --- |
| 10 |

**Clave para resolverla:** DISTINCT dentro de COUNT cuenta categorías únicas.

## 13. Calcular la suma de todos los precios del catálogo.

**Nivel:** Agregación  
**Qué se pretende:** Calcular la suma de todos los precios del catálogo.  
**Conceptos:** `SUM`, `ROUND`

**Resultado de la ejecución:**

| suma_precios |
| --- |
| 13385.67 |

**Clave para resolverla:** Esta suma no representa ventas reales.

## 14. Contar productos con precio de al menos 50.

**Nivel:** Agregación  
**Qué se pretende:** Contar productos con precio de al menos 50.  
**Conceptos:** `COUNT`, `WHERE`

**Resultado de la ejecución:**

| productos_caros |
| --- |
| 88 |

**Clave para resolverla:** WHERE restringe las filas que COUNT contará.

## 15. Contar vehículos de cada marca.

**Nivel:** Agrupación  
**Qué se pretende:** Contar vehículos de cada marca.  
**Conceptos:** `GROUP BY`, `COUNT`

**Resultado de la ejecución:**

| marca | cantidad |
| --- | --- |
| Ford | 10 |
| Honda | 10 |
| Hyundai | 10 |
| Kia | 10 |
| Opel | 10 |
| Peugeot | 10 |
| Renault | 10 |
| SEAT | 10 |
| Toyota | 10 |
| Volkswagen | 10 |

**Clave para resolverla:** GROUP BY forma un grupo por marca.

## 16. Encontrar los años de fabricación con más vehículos.

**Nivel:** Agrupación  
**Qué se pretende:** Encontrar los años de fabricación con más vehículos.  
**Conceptos:** `GROUP BY`, `COUNT`

**Resultado de la ejecución:**

| anio | cantidad |
| --- | --- |
| 2026 | 5 |
| 2025 | 5 |
| 2024 | 5 |
| 2019 | 5 |
| 2018 | 5 |
| 2017 | 5 |
| 2016 | 5 |
| 2012 | 5 |
| 2011 | 5 |
| 2010 | 5 |

**Clave para resolverla:** La agregación se hace para cada año.

## 17. Calcular año más antiguo y reciente por marca.

**Nivel:** Agrupación  
**Qué se pretende:** Calcular año más antiguo y reciente por marca.  
**Conceptos:** `GROUP BY`, `MIN`, `MAX`

**Resultado de la ejecución:**

| marca | anio_min | anio_max |
| --- | --- | --- |
| Ford | 2007 | 2024 |
| Honda | 2009 | 2026 |
| Hyundai | 2005 | 2026 |
| Kia | 2008 | 2025 |
| Opel | 2005 | 2022 |
| Peugeot | 2008 | 2025 |
| Renault | 2005 | 2026 |
| SEAT | 2006 | 2023 |
| Toyota | 2005 | 2026 |
| Volkswagen | 2005 | 2026 |

**Clave para resolverla:** MIN y MAX se calculan dentro de cada grupo.

## 18. Contar productos por tramos de precio.

**Nivel:** Agrupación  
**Qué se pretende:** Contar productos por tramos de precio.  
**Conceptos:** `CASE`, `GROUP BY`

**Resultado de la ejecución:**

| tramo | cantidad |
| --- | --- |
| Menos de 25 | 3 |
| 25 a menos de 75 | 20 |
| 75 o más | 77 |

**Clave para resolverla:** CASE crea categorías calculadas antes de agrupar.

## 19. Filtrar marcas con al menos cinco vehículos.

**Nivel:** Agrupación  
**Qué se pretende:** Filtrar marcas con al menos cinco vehículos.  
**Conceptos:** `GROUP BY`, `HAVING`

**Resultado de la ejecución:**

| marca | cantidad |
| --- | --- |
| Ford | 10 |
| Honda | 10 |
| Hyundai | 10 |
| Kia | 10 |
| Opel | 10 |
| Peugeot | 10 |
| Renault | 10 |
| SEAT | 10 |
| Toyota | 10 |
| Volkswagen | 10 |

**Clave para resolverla:** HAVING filtra grupos; WHERE filtra filas.

## 20. Mostrar años en los que hay al menos tres vehículos.

**Nivel:** Agrupación  
**Qué se pretende:** Mostrar años en los que hay al menos tres vehículos.  
**Conceptos:** `GROUP BY`, `HAVING`

**Resultado de la ejecución:**

| anio | cantidad |
| --- | --- |
| 2026 | 5 |
| 2025 | 5 |
| 2024 | 5 |
| 2019 | 5 |
| 2018 | 5 |
| 2017 | 5 |
| 2016 | 5 |
| 2012 | 5 |
| 2011 | 5 |
| 2010 | 5 |

**Clave para resolverla:** No se puede reemplazar HAVING por WHERE COUNT(*).

## 21. Mostrar el precio medio por familia, usando la primera palabra del nombre.

**Nivel:** Intermedio  
**Qué se pretende:** Mostrar el precio medio por familia, usando la primera palabra del nombre.  
**Conceptos:** `substr`, `instr`, `CASE`, `GROUP BY`, `AVG`

**Resultado de la ejecución:**

| familia | productos | media |
| --- | --- | --- |
| Disco | 10 | 137.41 |
| Adaptador | 5 | 182.92 |
| Alfombrilla | 5 | 205.81 |
| Altavoz | 5 | 96.34 |
| Auriculares | 5 | 64.01 |
| Cable | 5 | 172.13 |
| Escáner | 5 | 118.39 |
| Hub | 5 | 161.57 |
| Impresora | 5 | 107.2 |
| Memoria | 5 | 140.04 |
| Micrófono | 5 | 81.18 |
| Monitor | 5 | 47.7 |

**Clave para resolverla:** La familia es una clasificación didáctica derivada del nombre, no una columna real.

## 22. Mostrar familias que tengan al menos cuatro productos y precio medio superior a 35.

**Nivel:** Intermedio  
**Qué se pretende:** Mostrar familias que tengan al menos cuatro productos y precio medio superior a 35.  
**Conceptos:** `CTE`, `GROUP BY`, `HAVING`

**Resultado de la ejecución:**

| familia | productos | media |
| --- | --- | --- |
| Tableta | 5 | 240.52 |
| Switch | 5 | 232.28 |
| Router | 5 | 217.95 |
| Alfombrilla | 5 | 205.81 |
| Soporte | 5 | 194.5 |
| Adaptador | 5 | 182.92 |
| Cable | 5 | 172.13 |
| Hub | 5 | 161.57 |
| Memoria | 5 | 140.04 |
| Disco | 10 | 137.41 |
| Escáner | 5 | 118.39 |
| Impresora | 5 | 107.2 |
| Altavoz | 5 | 96.34 |
| Micrófono | 5 | 81.18 |
| Webcam | 5 | 70.83 |
| Auriculares | 5 | 64.01 |
| Monitor | 5 | 47.7 |
| Ratón | 5 | 39.92 |

**Clave para resolverla:** La CTE permite reutilizar un cálculo; HAVING impone dos condiciones agregadas.

## 23. Mostrar productos más caros que la media general.

**Nivel:** Intermedio  
**Qué se pretende:** Mostrar productos más caros que la media general.  
**Conceptos:** `Subconsulta`, `AVG`

**Resultado de la ejecución:**

| id | nombre | precio |
| --- | --- | --- |
| 100 | Tableta gráfica Premium | 251.59 |
| 99 | Tableta gráfica Compacto | 246.71 |
| 95 | Switch Premium | 246.0 |
| 98 | Tableta gráfica Pro | 243.88 |
| 94 | Switch Compacto | 232.78 |
| 93 | Switch Pro | 232.75 |
| 96 | Tableta gráfica Básico | 231.39 |
| 97 | Tableta gráfica Plus | 229.02 |
| 92 | Switch Plus | 228.84 |
| 89 | Router Compacto | 226.81 |

**Clave para resolverla:** La subconsulta devuelve un único número empleado en el filtro.

## 24. Listar los modelos de los vehículos posteriores al año 2020 agrupados por marca.

**Nivel:** Intermedio  
**Qué se pretende:** Listar los modelos de los vehículos posteriores al año 2020 agrupados por marca.  
**Conceptos:** `WHERE`, `GROUP BY`, `COUNT`

**Resultado de la ejecución:**

| marca | vehiculos_recientes |
| --- | --- |
| Kia | 4 |
| Ford | 3 |
| Honda | 3 |
| Peugeot | 3 |
| SEAT | 3 |
| Toyota | 3 |
| Hyundai | 2 |
| Opel | 2 |
| Renault | 2 |
| Volkswagen | 2 |

**Clave para resolverla:** Primero WHERE filtra los años, después GROUP BY cuenta marcas.

## 25. Detectar los productos empatados con el precio máximo.

**Nivel:** Intermedio  
**Qué se pretende:** Detectar los productos empatados con el precio máximo.  
**Conceptos:** `Subconsulta`, `MAX`

**Resultado de la ejecución:**

| id | nombre | precio |
| --- | --- | --- |
| 100 | Tableta gráfica Premium | 251.59 |

**Clave para resolverla:** El signo = devuelve todos los empates, no solo una fila.

## 26. Clasificar productos según el precio usando un ranking.

**Nivel:** Avanzado  
**Qué se pretende:** Clasificar productos según el precio usando un ranking.  
**Conceptos:** `RANK() OVER`, `ORDER BY`

**Resultado de la ejecución:**

| id | nombre | precio | puesto |
| --- | --- | --- | --- |
| 100 | Tableta gráfica Premium | 251.59 | 1 |
| 99 | Tableta gráfica Compacto | 246.71 | 2 |
| 95 | Switch Premium | 246.0 | 3 |
| 98 | Tableta gráfica Pro | 243.88 | 4 |
| 94 | Switch Compacto | 232.78 | 5 |
| 93 | Switch Pro | 232.75 | 6 |
| 96 | Tableta gráfica Básico | 231.39 | 7 |
| 97 | Tableta gráfica Plus | 229.02 | 8 |
| 92 | Switch Plus | 228.84 | 9 |
| 89 | Router Compacto | 226.81 | 10 |

**Clave para resolverla:** RANK deja huecos después de empates.

## 27. Calcular un total acumulado de precios ordenados por identificador.

**Nivel:** Avanzado  
**Qué se pretende:** Calcular un total acumulado de precios ordenados por identificador.  
**Conceptos:** `SUM() OVER`, `ventana`

**Resultado de la ejecución:**

| id | nombre | precio | acumulado |
| --- | --- | --- | --- |
| 1 | Teclado Básico | 17.68 | 17.68 |
| 2 | Teclado Plus | 17.4 | 35.08 |
| 3 | Teclado Pro | 30.87 | 65.95 |
| 4 | Teclado Compacto | 37.69 | 103.64 |
| 5 | Teclado Premium | 41.44 | 145.08 |
| 6 | Ratón Básico | 24.12 | 169.2 |
| 7 | Ratón Plus | 30.8 | 200.0 |
| 8 | Ratón Pro | 39.16 | 239.16 |
| 9 | Ratón Compacto | 48.76 | 287.92 |
| 10 | Ratón Premium | 56.76 | 344.68 |

**Clave para resolverla:** Una función de ventana conserva una fila por producto.

## 28. Comparar cada producto con el precio medio de su familia.

**Nivel:** Avanzado  
**Qué se pretende:** Comparar cada producto con el precio medio de su familia.  
**Conceptos:** `CTE`, `AVG() OVER (PARTITION BY)`

**Resultado de la ejecución:**

| id | nombre | familia | precio | media_familia |
| --- | --- | --- | --- | --- |
| 75 | Adaptador USB-C Premium | Adaptador | 194.04 | 182.92 |
| 74 | Adaptador USB-C Compacto | Adaptador | 191.83 | 182.92 |
| 73 | Adaptador USB-C Pro | Adaptador | 187.18 | 182.92 |
| 72 | Adaptador USB-C Plus | Adaptador | 173.25 | 182.92 |
| 71 | Adaptador USB-C Básico | Adaptador | 168.32 | 182.92 |
| 85 | Alfombrilla Premium | Alfombrilla | 220.61 | 205.81 |
| 84 | Alfombrilla Compacto | Alfombrilla | 212.66 | 205.81 |
| 83 | Alfombrilla Pro | Alfombrilla | 201.13 | 205.81 |
| 81 | Alfombrilla Básico | Alfombrilla | 197.55 | 205.81 |
| 82 | Alfombrilla Plus | Alfombrilla | 197.12 | 205.81 |
| 35 | Altavoz Premium | Altavoz | 113.2 | 96.34 |
| 34 | Altavoz Compacto | Altavoz | 105.97 | 96.34 |

**Clave para resolverla:** PARTITION BY calcula una media independiente por familia.

## 29. Mostrar el vehículo más reciente de cada marca, incluyendo empates.

**Nivel:** Avanzado  
**Qué se pretende:** Mostrar el vehículo más reciente de cada marca, incluyendo empates.  
**Conceptos:** `CTE`, `RANK()`, `PARTITION BY`

**Resultado de la ejecución:**

| matricula | marca | modelo | anio |
| --- | --- | --- | --- |
| 1031BCM | Ford | Puma | 2024 |
| 1091BEM | Honda | CR-V | 2026 |
| 1047BCI | Hyundai | Kona | 2026 |
| 1050BCL | Kia | Sportage | 2025 |
| 1081BEC | Opel | Astra | 2022 |
| 1072BDN | Peugeot | 208 | 2025 |
| 1025BBG | Renault | Mégane | 2026 |
| 1012BBN | SEAT | Ibiza | 2023 |
| 1003BBE | Toyota | Prius | 2026 |
| 1069BDK | Volkswagen | Polo | 2026 |

**Clave para resolverla:** El ranking se calcula dentro de cada marca antes de filtrar posicion = 1.

## 30. Resumir por marca el total, año medio y antigüedad, conservando solo marcas con al menos tres vehículos.

**Nivel:** Avanzado  
**Qué se pretende:** Resumir por marca el total, año medio y antigüedad, conservando solo marcas con al menos tres vehículos.  
**Conceptos:** `CTE`, `GROUP BY`, `HAVING`, `ROUND`, `MAX`, `MIN`

**Resultado de la ejecución:**

| marca | total | anio_medio | mas_antiguo | mas_reciente |
| --- | --- | --- | --- | --- |
| Ford | 10 | 2015.5 | 2007 | 2024 |
| Honda | 10 | 2017.5 | 2009 | 2026 |
| Hyundai | 10 | 2015.1 | 2005 | 2026 |
| Kia | 10 | 2016.9 | 2008 | 2025 |
| Opel | 10 | 2013.5 | 2005 | 2022 |
| Peugeot | 10 | 2016.1 | 2008 | 2025 |
| Renault | 10 | 2015.9 | 2005 | 2026 |
| SEAT | 10 | 2014.1 | 2006 | 2023 |
| Toyota | 10 | 2016.7 | 2005 | 2026 |
| Volkswagen | 10 | 2014.3 | 2005 | 2026 |

**Clave para resolverla:** Se encadenan agregación, filtro por grupos y ordenación.

## Recomendaciones

1. Identifica qué tabla contiene los datos solicitados.
2. Separa las columnas que quieres mostrar (`SELECT`) de las condiciones que deben cumplirse (`WHERE`).
3. Cuando aparezca «por marca», «por año» o «por familia», piensa en `GROUP BY`.
4. Cuando se pidan condiciones sobre agregados, utiliza `HAVING`.
5. Comprueba que el resultado es coherente y explica por qué devuelve esas filas.

**Nota:** Los importes usan valores numéricos de ejemplo; no son datos de ventas. SQLite3 permite `ROUND`, `CASE`, CTE y funciones de ventana en versiones modernas.
