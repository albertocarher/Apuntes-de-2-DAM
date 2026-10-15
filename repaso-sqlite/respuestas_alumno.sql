-- Completa cada ejercicio con UNA consulta SELECT (o WITH ... SELECT).
-- No borres las líneas: -- EJERCICIO NN
-- Termina cada consulta con punto y coma.

-- EJERCICIO 01
-- Mostrar los cinco primeros productos por identificador.
SELECT id, nombre, precio FROM Productos ORDER BY id LIMIT 5;
-- SELECT id, nombre, precio FROM Productos ORDER BY id LIMIT 5;

-- EJERCICIO 02
-- Ver los cinco vehículos más recientes.
SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY anio DESC LIMIT 5;

-- EJERCICIO 03
-- Filtrar productos cuyo precio sea mayor de 100.
SELECT id, nombre, precio FROM Productos WHERE precio > 100 ORDER BY precio DESC LIMIT 8;

-- EJERCICIO 04
-- Localizar vehículos de la marca Toyota.
SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca = 'Toyota' ORDER BY anio DESC LIMIT 8;

-- EJERCICIO 05
-- Buscar productos cuyo nombre contenga Pro.
SELECT id, nombre, precio FROM Productos WHERE nombre LIKE '%Pro%' ORDER BY id LIMIT 8;

-- EJERCICIO 06
-- Mostrar productos con precio comprendido entre 20 y 50.
SELECT id, nombre, precio FROM Productos WHERE precio BETWEEN 20 AND 50 ORDER BY precio LIMIT 8;

-- EJERCICIO 07
-- Listar marcas diferentes sin repetir.
SELECT DISTINCT marca FROM Vehiculos ORDER BY marca;

-- EJERCICIO 08
-- Seleccionar vehículos de dos marcas concretas.
SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca IN ('Toyota', 'Ford') ORDER BY marca, anio DESC LIMIT 8;

-- EJERCICIO 09
-- Contar cuántos productos existen.
SELECT COUNT(*) FROM Productos;

-- EJERCICIO 10
-- Contar cuántos vehículos están registrados.
SELECT COUNT(*) FROM Vehiculos;

-- EJERCICIO 11
-- Obtener precio mínimo, máximo y medio.
SELECT MIN(precio),MAX(precio),ROUND(AVG(precio),2) FROM Productos ;

-- EJERCICIO 12
-- Contar las marcas diferentes.
SELECT COUNT(DISTINCT marca) FROM Vehiculos;

-- EJERCICIO 13
-- Calcular la suma de todos los precios del catálogo.
SELECT ROUND(SUM(precio),2) FROM Productos;

-- EJERCICIO 14
-- Contar productos con precio de al menos 50.
SELECT COUNT(*) FROM Productos WHERE precio >= 50;

-- EJERCICIO 15
-- Contar vehículos de cada marca.
SELECT marca,COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY marca;

-- EJERCICIO 16
-- Encontrar los años de fabricación con más vehículos.
SELECT anio,COUNT(*) AS total FROM Vehiculos GROUP BY anio ORDER BY total DESC, anio DESC LIMIT 10;

-- EJERCICIO 17
-- Calcular año más antiguo y reciente por marca.
SELECT marca, MIN(anio), MAX(anio) FROM Vehiculos GROUP BY marca ORDER BY marca;

-- EJERCICIO 18
-- Contar productos por tramos de precio.
SELECT
    CASE
        WHEN precio < 25 THEN 'Menos de 25'
        WHEN precio < 75 THEN '25 a menos de 75'
        ELSE '75 o más'
        END AS tramo,
        COUNT(*) AS cantidad FROM Productos GROUP BY tramo ORDER BY MIN(precio);

-- EJERCICIO 19
-- Filtrar marcas con al menos cinco vehículos.
SELECT marca, COUNT(*) AS cantidad FROM Vehiculos GROUP BY marca HAVING COUNT(*) >= 5 ORDER BY marca;

-- EJERCICIO 20
-- Mostrar años en los que hay al menos tres vehículos.
SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio HAVING COUNT(*) >= 3 ORDER BY cantidad DESC, anio DESC LIMIT 10;

-- EJERCICIO 21
-- Mostrar el precio medio por familia, usando la primera palabra del nombre.
SELECT AVG(precio) AS media, SUBSTRING(nombre,1,INSTR(nombre,' ') -1) AS familia
    CASE
        WHEN familia = 'Disco' THEN 'Disco'
        WHEN familia = 'Adaptador' THEN 'Adaptador'
        WHEN familia = 'Alfombrilla' THEN 'Alfombrilla'
        WHEN familia = 'Altavoz' THEN 'Altavoz'
        WHEN familia = 'Auriculares' THEN 'Auriculares'
        WHEN familia = 'Cable' THEN 'Cable'
        WHEN familia = 'Escáner' THEN 'Escáner'
        WHEN familia = 'Hub' THEN 'Hub'
        WHEN familia = 'Impresora' THEN 'Impresora'
        WHEN familia = 'Memoria' THEN 'Memoria'
        WHEN familia = 'Micrófono' THEN 'Micrófono'
        WHEN familia = 'Monitor' THEN 'Monitor'
        END AS productos,COUNT(*) AS cantidad FROM Productos GROUP BY productos ORDER BY productos DESC, cantidad DESC;

-- EJERCICIO 22
-- Mostrar familias que tengan al menos cuatro productos y precio medio superior a 35.
SELECT SUBSTRING(nombre,1,INSTR(nombre,' ') -1) AS familia, COUNT(*) as cantidad, ROUND(AVG(precio),2) AS media
FROM Productos GROUP BY familia HAVING cantidad >= 4 AND media > 35 ORDER BY media DESC;

-- EJERCICIO 23
-- Mostrar productos más caros que la media general.
SELECT ...;

-- EJERCICIO 24
-- Listar los modelos de los vehículos posteriores al año 2020 agrupados por marca.
SELECT ...;

-- EJERCICIO 25
-- Detectar los productos empatados con el precio máximo.
SELECT ...;

-- EJERCICIO 26
-- Clasificar productos según el precio usando un ranking.
SELECT ...;

-- EJERCICIO 27
-- Calcular un total acumulado de precios ordenados por identificador.
SELECT ...;

-- EJERCICIO 28
-- Comparar cada producto con el precio medio de su familia.
SELECT ...;

-- EJERCICIO 29
-- Mostrar el vehículo más reciente de cada marca, incluyendo empates.
SELECT ...;

-- EJERCICIO 30
-- Resumir por marca el total, año medio y antigüedad, conservando solo marcas con al menos tres vehículos.
SELECT ...;
