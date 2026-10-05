# FileLab — actividad de ficheros y formatos estructurados

Proyecto de prácticas con Java 21 para trabajar `Path`, `File`, `Files`, texto, `.properties`, CSV, JSON, XML, repositorios, genéricos y conversión entre formatos.

El proyecto se entrega **incompleto de forma intencionada**. Los métodos que debes implementar contienen:

```java
throw new UnsupportedOperationException("Función no implementada");
```

Debes sustituir esa excepción por la implementación correspondiente.

> No cambies las firmas públicas de los métodos ni modifiques los tests para conseguir que pasen. El objetivo es implementar el comportamiento indicado.

---

## Regla principal de las API

Las API públicas **no deben declarar `throws IOException`**.

Las excepciones de entrada/salida se gestionan dentro de las implementaciones. Cuando una operación no puede obtener información, devuelve un resultado vacío adecuado:

```text
Optional<T>     -> Optional.empty()
List<T>         -> List.of()
Map<K, V>       -> Map.of()
String          -> ""
OptionalLong    -> OptionalLong.empty()
boolean         -> false
```

Los métodos `protected` que realizan físicamente la lectura o escritura pueden utilizar `IOException` internamente. Esa excepción no debe llegar al código consumidor.

Las validaciones de dominio sí pueden utilizar `IllegalArgumentException`.

---

# Cómo trabajar

No intentes implementar todo el proyecto de una vez.

Completa un bloque, ejecuta sus tests y pasa al siguiente cuando todos estén en verde.

Todos los tests:

```bash
mvn test
```

Una clase concreta:

```bash
mvn -Dtest=PathServiceTest test
```

Un único test:

```bash
mvn -Dtest=ProductoServiceTest#encuentraMaximoStock test
```

Varias clases:

```bash
mvn -Dtest=PathServiceTest,FileServiceTest,FilesServiceTest test
```

Al principio es normal encontrar:

```text
java.lang.UnsupportedOperationException: Función no implementada
```

Ese mensaje identifica código pendiente.

---

# Bloque 1 · `Path`

Clase:

```text
src/main/java/es/codelearnacademy/filelab/io/PathService.java
```

Este bloque trabaja exclusivamente con `java.nio.file.Path`. En general, estas operaciones describen o transforman rutas; no tienen por qué acceder al sistema de ficheros.

## Funciones a implementar

### `crear(String primero, String... partes)`

Construye y devuelve un `Path` a partir del primer fragmento recibido y del resto de fragmentos.

Ejemplo:

```java
crear("data", "productos.csv")
```

debe representar:

```text
data/productos.csv
```

Debe utilizar la API `Path` y no concatenar manualmente separadores como `/` o `\`.

### `nombre(Path path)`

Devuelve el nombre del último elemento de la ruta.

Ejemplo:

```text
data/productos.csv -> productos.csv
```

### `padre(Path path)`

Devuelve la ruta padre.

Ejemplo:

```text
data/productos.csv -> data
```

### `absoluto(Path path)`

Convierte una ruta relativa en una ruta absoluta mediante la API de `Path`.

No debes construir manualmente la ruta absoluta.

### `normalizar(Path path)`

Devuelve una versión normalizada de la ruta eliminando elementos redundantes como `.` y resolviendo, cuando corresponda, elementos `..`.

Ejemplo conceptual:

```text
data/./temp/../productos.csv
```

debe normalizarse a:

```text
data/productos.csv
```

### `esAbsoluto(Path path)`

Indica mediante `true` o `false` si la ruta recibida es absoluta.

### `resolver(Path base, String otro)`

Combina la ruta `base` con el fragmento indicado en `otro` mediante `Path.resolve(...)`.

Ejemplo:

```text
base = data
otro = productos.csv
resultado = data/productos.csv
```

### `relativizar(Path base, Path destino)`

Calcula la ruta relativa necesaria para llegar desde `base` hasta `destino`.

Ejemplo:

```text
base    = data
destino = data/productos.csv
resultado = productos.csv
```

### `extension(Path path)`

Devuelve la extensión del fichero **sin el punto**.

Ejemplo:

```text
productos.csv -> csv
```

Si el nombre no tiene extensión, devuelve:

```java
""
```

## Verificación

```bash
mvn -Dtest=PathServiceTest test
```

Tests relevantes:

```text
creaRuta
obtieneNombre
obtienePadre
normalizaRuta
resuelveRuta
relativizaRuta
obtieneExtension
extensionVacia
```

---

# Bloque 2 · `File`

Clase:

```text
src/main/java/es/codelearnacademy/filelab/io/FileService.java
```

Este bloque permite conocer `java.io.File` y su relación con `Path`.

## Funciones a implementar

### `existe(File file)`

Devuelve `true` cuando el fichero o directorio representado por `file` existe en el sistema de ficheros.

### `esArchivo(File file)`

Devuelve `true` únicamente cuando `file` existe y representa un fichero normal.

### `esDirectorio(File file)`

Devuelve `true` únicamente cuando `file` existe y representa un directorio.

### `nombre(File file)`

Devuelve únicamente el nombre del fichero o directorio, sin incluir su ruta padre.

### `padre(File file)`

Devuelve el directorio padre como objeto `File`.

### `convertirAPath(File file)`

Convierte el objeto `File` recibido a `Path`.

### `convertirAFile(Path path)`

Convierte el objeto `Path` recibido a `File`.

## Verificación

```bash
mvn -Dtest=FileServiceTest test
```

---

# Bloque 3 · `Files`

Clase:

```text
src/main/java/es/codelearnacademy/filelab/io/FilesService.java
```

En este bloque se realizan operaciones reales sobre el sistema de ficheros mediante `java.nio.file.Files`.

## Funciones a implementar

### `existe(Path path)`

Devuelve `true` si la ruta existe.

### `crearDirectorio(Path path)`

Crea **un único directorio**.

Si la operación finaliza correctamente devuelve:

```java
Optional.of(path)
```

Si no puede crearse, devuelve:

```java
Optional.empty()
```

No propagues `IOException`.

### `crearDirectorios(Path path)`

Crea toda la jerarquía de directorios necesaria, equivalente al comportamiento de `Files.createDirectories(...)`.

Si se crea correctamente devuelve el `Path` dentro de un `Optional`.

### `crearArchivo(Path path)`

Crea un fichero vacío en la ruta indicada.

Devuelve el `Path` creado mediante `Optional`; ante un error devuelve `Optional.empty()`.

### `copiar(Path origen, Path destino)`

Copia el fichero indicado por `origen` en `destino`.

El fichero original debe continuar existiendo.

Devuelve el `Path` de destino dentro de un `Optional` cuando la copia se realiza correctamente.

### `mover(Path origen, Path destino)`

Mueve el fichero de `origen` a `destino`.

Tras una operación correcta:

```text
origen  -> ya no debe existir
destino -> debe existir
```

Devuelve el `Path` de destino mediante `Optional`.

### `eliminar(Path path)`

Elimina el fichero o directorio indicado.

Devuelve:

```text
true  -> la eliminación se ha realizado
false -> la operación no ha podido realizarse
```

No propagues `IOException`.

### `tamanio(Path path)`

Devuelve el tamaño del fichero en bytes mediante `OptionalLong`.

Si no puede obtenerse el tamaño:

```java
OptionalLong.empty()
```

## Verificación

```bash
mvn -Dtest=FilesServiceTest test
```

Los tests utilizan `@TempDir`, por lo que no debes usar rutas absolutas específicas de tu ordenador.

---

# Bloque 4 · Ficheros de texto UTF-8

Clase:

```text
src/main/java/es/codelearnacademy/filelab/io/TextFileService.java
```

Todas las operaciones de este bloque deben utilizar UTF-8.

## Funciones a implementar

### `escribir(Path path, String contenido)`

Escribe `contenido` en el fichero indicado.

Si el fichero ya existe, el comportamiento esperado es sustituir su contenido.

Devuelve `true` si la escritura se realiza correctamente y `false` si se produce un error.

### `leer(Path path)`

Lee todo el contenido del fichero como un único `String` utilizando UTF-8.

Si no puede leerse:

```java
""
```

### `escribirLineas(Path path, List<String> lineas)`

Escribe todas las líneas de la lista utilizando UTF-8.

Devuelve `true` si la operación termina correctamente.

### `leerLineas(Path path)`

Lee el fichero como una lista de líneas.

Si no puede leerse:

```java
List.of()
```

### `anexar(Path path, String contenido)`

Añade `contenido` al final del fichero sin eliminar el contenido existente.

Devuelve `true` si la operación se realiza correctamente.

## Verificación

```bash
mvn -Dtest=TextFileServiceTest test
```

---

# Bloque 5 · `Producto` y validación

Modelo:

```text
model/Producto.java
```

Validador:

```text
validation/ProductoValidator.java
```

El modelo `Producto` ya está definido. Debes completar la validación.

## Función a implementar

### `ProductoValidator.validar(Producto producto)`

Debe aceptar productos válidos y lanzar `IllegalArgumentException` cuando se incumpla alguna de estas reglas:

```text
producto != null
id > 0
nombre != null
nombre no puede estar vacío ni contener únicamente espacios
precio >= 0
stock >= 0
```

No conviertas los errores de validación en valores vacíos: en este caso la excepción forma parte de la validación de dominio.

## Verificación

```bash
mvn -Dtest=ProductoValidatorTest test
```

---

# Bloque 6 · Operaciones sobre productos

Clase:

```text
service/ProductoService.java
```

`ProductoService` contiene lógica de aplicación. No debe conocer cómo están almacenados los productos.

Los datos se obtienen mediante `IProductoRepository`.

## Funciones a implementar

### `maximoPrecio()`

Obtiene todos los productos del repositorio y devuelve el producto con el precio más alto.

Resultado:

```java
Optional<Producto>
```

Si no hay productos:

```java
Optional.empty()
```

### `minimoPrecio()`

Devuelve el producto con el precio más bajo.

Si no hay productos, devuelve `Optional.empty()`.

### `maximoStock()`

Devuelve el producto cuyo valor de `stock` sea mayor.

Si el catálogo está vacío, devuelve `Optional.empty()`.

### `minimoStock()`

Devuelve el producto cuyo valor de `stock` sea menor.

Un producto con stock `0` es válido y puede ser el resultado mínimo.

### `stockTotal()`

Suma el stock de todos los productos.

Ejemplo:

```text
10 + 4 + 25 + 0 = 39
```

Si no hay productos, devuelve `0`.

### `valorInventario()`

Calcula el valor económico total del inventario.

Para cada producto:

```text
precio × stock
```

El resultado final es la suma de esos importes.

Ejemplo conceptual:

```text
(teclado.precio × teclado.stock)
+ (monitor.precio × monitor.stock)
+ ...
```

### `sinStock()`

Devuelve una lista que contenga únicamente los productos cuyo stock sea exactamente `0`.

Si no existen productos sin stock:

```java
List.of()
```

o cualquier lista vacía equivalente.

### `buscar(String texto)`

Busca productos cuyo nombre contenga el texto recibido.

La búsqueda debe ignorar mayúsculas y minúsculas.

Ejemplo:

```text
"TECLA"
```

debe encontrar:

```text
"Teclado mecánico"
```

Si no hay coincidencias, devuelve una lista vacía.

## Verificación

```bash
mvn -Dtest=ProductoServiceTest test
```

Ejemplo para comprobar solamente stock máximo:

```bash
mvn -Dtest=ProductoServiceTest#encuentraMaximoStock test
```

---

# Bloque 7 · Repositorio genérico

Archivos:

```text
repository/IRepository.java
repository/IProductoRepository.java
repository/AbstractFileRepository.java
```

`IRepository<T, ID>` define el contrato CRUD común:

```java
List<T> findAll();
Optional<T> findById(ID id);
boolean create(T entity);
boolean update(T entity);
boolean delete(ID id);
```

`IProductoRepository` especializa ese contrato como:

```java
IRepository<Producto, Long>
```

## Funciones de `AbstractFileRepository<T, ID>` a implementar

### `findAll()`

Debe delegar la lectura en:

```java
readAll()
```

y devolver todas las entidades.

Si se produce un problema de entrada/salida:

```java
List.of()
```

La excepción no debe propagarse.

### `findById(ID id)`

Debe buscar en las entidades obtenidas por `readAll()` aquella cuyo identificador coincida con `id`.

Para conocer el identificador de cada entidad debes utilizar:

```java
getId(entity)
```

Si encuentra la entidad:

```java
Optional.of(entity)
```

Si no la encuentra o no puede leer los datos:

```java
Optional.empty()
```

### `create(T entity)`

Debe:

1. leer las entidades actuales;
2. comprobar que no exista otra entidad con el mismo identificador;
3. añadir la nueva entidad;
4. persistir la lista actualizada mediante `writeAll(...)`.

Debe devolver `true` únicamente cuando la creación y persistencia finalicen correctamente.

Si no puede realizarse la operación, devuelve `false`.

### `update(T entity)`

Debe localizar una entidad existente con el mismo identificador.

Si existe:

1. sustituye la entidad anterior por la nueva;
2. conserva el resto de elementos;
3. guarda la lista mediante `writeAll(...)`;
4. devuelve `true`.

Si no existe o no puede persistirse, devuelve `false`.

### `delete(ID id)`

Debe localizar la entidad cuyo identificador coincida con `id`.

Si existe:

1. elimina la entidad;
2. persiste el resto con `writeAll(...)`;
3. devuelve `true`.

Si no existe o no puede completarse la operación, devuelve `false`.

## Métodos protegidos que implementan las subclases

### `getId(T entity)`

Debe devolver el identificador de la entidad.

Ejemplos:

```text
Producto -> id
Vehiculo -> matricula
```

### `readAll()`

Debe leer el formato físico correspondiente y convertirlo a una `List<T>`.

Este método es `protected` y puede declarar `IOException`.

### `writeAll(List<T> entities)`

Debe sobrescribir el fichero correspondiente con todas las entidades recibidas.

Este método es `protected` y puede declarar `IOException`.

---

# Bloque 8 · CSV de productos

Clase:

```text
csv/ProductoCsvRepository.java
```

Utiliza Apache Commons CSV.

Cabecera esperada:

```text
id,nombre,precio,stock
```

## Funciones a implementar

### `getId(Producto producto)`

Devuelve:

```java
producto.id()
```

La clase base utiliza este método para buscar, actualizar y eliminar.

### `readAll()`

Debe:

1. abrir el fichero CSV indicado por `path`;
2. interpretar la primera fila como cabecera;
3. recorrer los registros;
4. convertir cada registro a `Producto`;
5. devolver la lista completa.

Conversión esperada:

```text
id     -> long
nombre -> String
precio -> double
stock  -> int
```

Si el método lanza `IOException`, será tratado posteriormente por la clase base.

### `writeAll(List<Producto> productos)`

Debe sobrescribir el CSV completo.

El fichero resultante debe:

1. contener la cabecera `id,nombre,precio,stock`;
2. escribir una fila por producto;
3. conservar correctamente caracteres UTF-8;
4. utilizar Apache Commons CSV.

## Verificación

```bash
mvn -Dtest=ProductoCsvRepositoryTest test
```

El test hereda el contrato CRUD común, por lo que CSV, JSON y XML deben comportarse de la misma forma.

---

# Bloque 9 · JSON de productos

Clase:

```text
json/ProductoJsonRepository.java
```

Utiliza Jackson Databind mediante `ObjectMapper`.

## Funciones a implementar

### `getId(Producto producto)`

Devuelve el `id` del producto.

### `readAll()`

Debe deserializar el contenido JSON del fichero indicado por `path` a una lista de objetos `Producto`.

El fichero contiene un array JSON de productos.

El método debe utilizar el `ObjectMapper` disponible en la clase.

### `writeAll(List<Producto> productos)`

Debe serializar la lista completa de productos al fichero JSON indicado.

El fichero final debe representar todos los productos de la lista recibida.

## Verificación

```bash
mvn -Dtest=ProductoJsonRepositoryTest test
```

---

# Bloque 10 · XML de productos

Clases:

```text
xml/DocumentoProductos.java
xml/ProductoXmlRepository.java
```

`DocumentoProductos` representa el elemento raíz `<productos>`.

## Funciones de `DocumentoProductos`

### `getProductos()`

Devuelve la lista almacenada en el atributo `productos`.

No debe devolver otra lista distinta ni lanzar la excepción de “función no implementada”.

### `setProductos(List<Producto> productos)`

Sustituye el contenido del atributo `productos` por la lista recibida.

Este método es necesario para que Jackson XML pueda trabajar correctamente con el documento.

## Funciones de `ProductoXmlRepository`

### `getId(Producto producto)`

Devuelve el identificador del producto.

### `readAll()`

Debe:

1. leer el XML mediante `XmlMapper`;
2. deserializar el documento a `DocumentoProductos`;
3. obtener la lista mediante `getProductos()`;
4. devolver esa lista.

### `writeAll(List<Producto> productos)`

Debe:

1. construir un `DocumentoProductos` con la lista recibida;
2. serializar el documento completo con `XmlMapper`;
3. escribirlo en `path`.

## Verificación

```bash
mvn -Dtest=ProductoXmlRepositoryTest test
```

---

# Bloque 11 · Generalización con `Vehiculo`

Modelo:

```text
model/Vehiculo.java
```

Contrato:

```text
repository/IVehiculoRepository.java
```

La matrícula es el identificador:

```java
IRepository<Vehiculo, String>
```

El objetivo es demostrar que `AbstractFileRepository<T, ID>` no está acoplado a `Producto` ni a `Long`.

---

## `VehiculoCsvRepository`

Clase:

```text
csv/VehiculoCsvRepository.java
```

### `getId(Vehiculo vehiculo)`

Devuelve la matrícula:

```java
vehiculo.matricula()
```

### `readAll()`

Lee el CSV de vehículos y devuelve una lista de `Vehiculo`.

Campos:

```text
matricula
marca
modelo
anio
```

`anio` debe convertirse a `int`.

### `writeAll(List<Vehiculo> vehiculos)`

Sobrescribe el CSV completo, incluyendo la cabecera correspondiente y una fila por vehículo.

---

## `VehiculoJsonRepository`

Clase:

```text
json/VehiculoJsonRepository.java
```

### `getId(Vehiculo vehiculo)`

Devuelve la matrícula.

### `readAll()`

Deserializa el array JSON de vehículos a `List<Vehiculo>` utilizando `ObjectMapper`.

### `writeAll(List<Vehiculo> vehiculos)`

Serializa la lista completa de vehículos al fichero JSON.

---

## `DocumentoVehiculos`

Clase:

```text
xml/DocumentoVehiculos.java
```

### `getVehiculos()`

Devuelve la lista interna de vehículos.

### `setVehiculos(List<Vehiculo> vehiculos)`

Sustituye la lista interna por la recibida.

---

## `VehiculoXmlRepository`

Clase:

```text
xml/VehiculoXmlRepository.java
```

### `getId(Vehiculo vehiculo)`

Devuelve la matrícula del vehículo.

### `readAll()`

Lee el documento XML con `XmlMapper`, lo convierte a `DocumentoVehiculos` y devuelve su lista.

### `writeAll(List<Vehiculo> vehiculos)`

Crea un `DocumentoVehiculos` y serializa todos los vehículos al fichero XML.

## Verificación

```bash
mvn -Dtest=VehiculoRepositoryTest test
```

Los tests suministrados verifican inicialmente el repositorio CSV. Las implementaciones JSON y XML deben seguir exactamente el mismo contrato.

---

# Bloque 12 · `.properties`

Clase:

```text
config/PropertiesConfig.java
```

Utiliza `java.util.Properties`.

## Funciones a implementar

### `get(String key)`

Lee las propiedades del fichero y busca la clave indicada.

Si la clave existe:

```java
Optional.of(valor)
```

Si la propiedad no existe o el fichero no puede leerse:

```java
Optional.empty()
```

### `getOrDefault(String key, String defaultValue)`

Devuelve el valor asociado a `key`.

Si no existe o no puede recuperarse, devuelve `defaultValue`.

### `findAll()`

Devuelve todas las propiedades como:

```java
Map<String, String>
```

Si no pueden leerse:

```java
Map.of()
```

### `put(String key, String value)`

Debe:

1. cargar las propiedades existentes, si las hay;
2. añadir o sustituir la propiedad indicada;
3. guardar el conjunto completo en el fichero;
4. devolver `true` si se persiste correctamente.

Ante un error devuelve `false`.

### `remove(String key)`

Debe eliminar la propiedad indicada y persistir el cambio.

Devuelve `true` cuando la operación se completa correctamente.

Si no puede realizarse, devuelve `false`.

## Verificación

```bash
mvn -Dtest=PropertiesConfigTest test
```

---

# Bloque 13 · Selección de repositorio

Clases:

```text
service/FileFormat.java
service/RepositoryFactory.java
```

## `FileFormat.from(String value)`

Convierte un texto a uno de los valores del enum:

```text
CSV
JSON
XML
```

Debe ignorar mayúsculas y minúsculas.

Ejemplos:

```text
csv  -> CSV
JSON -> JSON
Xml  -> XML
```

Un formato no reconocido, como `yaml`, debe producir `IllegalArgumentException`.

## `RepositoryFactory.create(FileFormat format, Path path)`

Debe devolver una implementación de `IProductoRepository` de acuerdo con el formato:

```text
CSV  -> ProductoCsvRepository
JSON -> ProductoJsonRepository
XML  -> ProductoXmlRepository
```

Todos los repositorios deben recibir el mismo `Path` pasado al método.

## Verificación

```bash
mvn -Dtest=FileFormatTest,RepositoryFactoryTest test
```

---

# Bloque 14 · DataBridge

Clases:

```text
service/DataBridgeService.java
service/ConfiguredDataBridge.java
```

La conversión no debe depender directamente de clases CSV, JSON o XML. Debe trabajar mediante `IProductoRepository`.

---

## `DataBridgeService.convert(...)`

Firma:

```java
convert(
    FileFormat origenFormato,
    Path origen,
    FileFormat destinoFormato,
    Path destino
)
```

Debe:

1. pedir a `RepositoryFactory` un repositorio para el formato y fichero de origen;
2. leer todos los productos del origen;
3. pedir otro repositorio para el formato y fichero de destino;
4. persistir esos productos en el destino;
5. devolver el número de productos convertidos cuando la operación finaliza correctamente.

Ejemplo conceptual:

```text
productos.csv
      ↓
ProductoCsvRepository
      ↓
List<Producto>
      ↓
ProductoJsonRepository
      ↓
productos.json
```

La lógica de `DataBridgeService` no debe contener código específico para parsear CSV, JSON o XML.

## `ConfiguredDataBridge.execute()`

Debe obtener de `PropertiesConfig` los datos necesarios para ejecutar una conversión configurada.

La configuración disponible utiliza claves como:

```properties
input.format=csv
input.file=data/productos.csv
output.format=json
output.file=data/productos.json
```

El método debe:

1. leer el formato de entrada;
2. leer la ruta de entrada;
3. leer el formato de salida;
4. leer la ruta de salida;
5. convertir los formatos de texto mediante `FileFormat.from(...)`;
6. convertir las rutas mediante `Path`;
7. llamar a `DataBridgeService.convert(...)`;
8. devolver el número de productos convertidos.

Si faltan datos imprescindibles de configuración o no puede ejecutarse la conversión, debe devolver un resultado coherente con la API sin propagar `IOException`.

## Verificación

```bash
mvn -Dtest=DataBridgeServiceTest test
```

---

# Resumen de todas las funciones pendientes

Antes de entregar, revisa que no quede ninguna aparición de:

```java
throw new UnsupportedOperationException("Función no implementada");
```

Las clases con trabajo pendiente son:

```text
PathService
FileService
FilesService
TextFileService
ProductoValidator
ProductoService
AbstractFileRepository
ProductoCsvRepository
ProductoJsonRepository
DocumentoProductos
ProductoXmlRepository
VehiculoCsvRepository
VehiculoJsonRepository
DocumentoVehiculos
VehiculoXmlRepository
PropertiesConfig
FileFormat
RepositoryFactory
DataBridgeService
ConfiguredDataBridge
```

---

# Documentación JavaDoc

Debes documentar la API genérica:

```text
repository/IRepository.java
```

Cada método debe contener una descripción clara y las etiquetas que correspondan.

## Patrón para métodos de consulta

```java
/**
 * Busca una entidad utilizando su identificador.
 *
 * @param id identificador de la entidad que se desea localizar
 * @return la entidad encontrada o {@link Optional#empty()} si no existe
 *         o no pueden recuperarse los datos
 */
Optional<T> findById(ID id);
```

## Patrón para métodos de modificación

```java
/**
 * Crea una nueva entidad.
 *
 * @param entity entidad que se desea crear
 * @return true si la entidad se ha persistido correctamente; false en caso contrario
 */
boolean create(T entity);
```

## Patrón para `findAll()`

```java
/**
 * Recupera todas las entidades almacenadas.
 *
 * @return lista con las entidades disponibles o una lista vacía si no existen
 *         datos o no pueden recuperarse
 */
List<T> findAll();
```

## Reglas de documentación

```text
No repitas únicamente el nombre del método.
Explica qué comportamiento ofrece la API.
Documenta todos los parámetros mediante @param.
Documenta siempre el resultado mediante @return.
Explica el significado de los valores vacíos o false.
No añadas @throws IOException a la API pública.
```

No es necesario documentar en `IRepository` cómo funciona internamente CSV, JSON o XML. La interfaz describe el contrato, no su implementación.

---

# Nombres de tests y variables

Los tests utilizan nombres breves y significativos:

```text
detectaArchivoExistente
creaDirectorio
leeTodosLosProductos
encuentraProductoPorId
encuentraMaximoPrecio
encuentraMinimoPrecio
encuentraMaximoStock
calculaStockTotal
rechazaPrecioNegativo
creaRepositorioCsv
convierteCsvAJson
```

Evita:

```text
existe_debeDetectarArchivoExistente
cuandoExisteArchivo_entoncesDevuelveTrue
test1
testMaximoStock
```

También evita variables sin significado:

```java
var r = ...
var p = ...
```

Utiliza nombres como:

```java
var repositorio = ...
var producto = ...
var archivo = ...
long idInexistente = 9999L;
```

---

# Secuencia recomendada de verificación

Trabaja en este orden:

```bash
mvn -Dtest=PathServiceTest test
mvn -Dtest=FileServiceTest test
mvn -Dtest=FilesServiceTest test
mvn -Dtest=TextFileServiceTest test
mvn -Dtest=ProductoValidatorTest test
mvn -Dtest=ProductoServiceTest test
mvn -Dtest=ProductoCsvRepositoryTest test
mvn -Dtest=ProductoJsonRepositoryTest test
mvn -Dtest=ProductoXmlRepositoryTest test
mvn -Dtest=VehiculoRepositoryTest test
mvn -Dtest=PropertiesConfigTest test
mvn -Dtest=FileFormatTest,RepositoryFactoryTest test
mvn -Dtest=DataBridgeServiceTest test
```

Cuando todos los bloques estén completados:

```bash
mvn test
```

---

# Cómo interpretar los fallos de los tests

Si aparece:

```text
UnsupportedOperationException: Función no implementada
```

todavía no has implementado el método alcanzado por el test.

Si aparece una comparación como:

```text
expected: <...>
but was: <...>
```

el método ya se ejecuta, pero su resultado no coincide con el contrato.

Si un test de repositorio falla tanto para `create`, `update` como `delete`, revisa primero:

```text
AbstractFileRepository
```

porque la lógica CRUD común se encuentra allí.

Si falla únicamente CSV, JSON o XML, revisa la implementación de:

```text
readAll()
writeAll()
getId()
```

del formato correspondiente.

---

# Calcular la nota

Linux/macOS:

```bash
mvn clean verify -Pnota
```

Windows, si Python se ejecuta como `python`:

```powershell
mvn clean verify -Pnota -Dpython.command=python
```

La nota orientativa se distribuye así:

```text
8 puntos -> tests
2 puntos -> JavaDoc de IRepository
```

Los tests fallidos, con error o saltados no cuentan como superados.

El informe se genera en:

```text
target/nota.txt
```

---

# Antes de entregar

Comprueba:

```text
[ ] No quedan UnsupportedOperationException("Función no implementada")
[ ] mvn test termina correctamente
[ ] IRepository está documentada con JavaDoc
[ ] Las API públicas no declaran throws IOException
[ ] No se han modificado los tests
[ ] Las variables tienen nombres significativos
[ ] CSV, JSON y XML representan los mismos productos
[ ] La lógica común CRUD está en AbstractFileRepository
[ ] ProductoService no contiene lógica específica de CSV, JSON o XML
[ ] DataBridgeService trabaja mediante IProductoRepository
[ ] El proyecto usa Java 21
```
