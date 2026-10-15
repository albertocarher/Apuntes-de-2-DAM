# Gestión básica: Java 17 + SQLite + JDBC + JUnit 5

Proyecto didáctico con dos tablas: `Productos` y `Vehiculos`.

## Bases de datos

- `datos/ejemplo.sqlite3`: **original**, 100 productos y 100 vehículos. Consultar, no modificar.
- `datos/gestion.sqlite3`: **ejecución del programa**, empieza como copia del ejemplo. Los cambios CRUD quedan guardados para verlos en DB Browser for SQLite.
- `datos/test.sqlite3`: plantilla **sin registros**, con las restricciones reales. JUnit crea una copia temporal para **cada** prueba; la plantilla y las otras bases no se modifican.

Para **restaurar la base de trabajo**, con la aplicación cerrada, copiar `ejemplo.sqlite3` sobre `gestion.sqlite3` (sobrescribe cambios).

## Ejecutar

Requiere JDK 17+ y Maven. Abrir una terminal en la carpeta de `pom.xml`:

```sh
mvn compile exec:java
```

El `Main` define `String url = "jdbc:sqlite:datos/gestion.sqlite3";` y se la pasa a `MenuConsola(url)`, que crea `ProductoDao(url)` y `VehiculoDao(url)`. Cada DAO crea `ConexionBD(url)` y abre/cierra una conexión por operación. No hay propiedades del sistema ni variables de entorno. Abrir `datos/gestion.sqlite3` con DB Browser for SQLite **después de cada operación** y actualizar la vista para comprobar el cambio.

## Tests independientes y ordenados

```sh
mvn test
mvn -Dtest=ProductoDaoTest test
mvn -Dtest=VehiculoDaoTest test
```

- 17 tests de producto ordenados con `@Order`.
- 19 tests de vehículo ordenados con `@Order`.
- 2 tests de conexión.
- `BaseDatosTest` copia `datos/test.sqlite3` a un archivo temporal distinto en `@BeforeEach` y lo borra en `@AfterEach`.
- Un fallo no debe alterar ni otros tests ni la base de trabajo. El orden es **didáctico**, no una dependencia entre tests.
- Los métodos de escritura devuelven `boolean`; los problemas SQL lanzan `AplicationException` (extends `RuntimeException`).

## Observaciones

Se trabaja desde la raíz del proyecto para resolver las rutas `datos/...`. Las consultas están en `sql/consultas_ejemplo.sql`. Una SQLite abierta en DB Browser debe actualizarse para mostrar los cambios externos. No debe borrarse ni sobrescribirse `gestion.sqlite3` mientras la aplicación Java la tenga abierta.
