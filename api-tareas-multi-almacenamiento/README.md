# Práctica de API REST de tareas por almacenamiento

Completa una API de tareas con cinco recorridos de persistencia. Cada tecnología tiene un controlador, un servicio y un repositorio propios. Trabaja sobre el proyecto Maven y completa los métodos marcados con `TODO`.

## 1. Reconoce los datos de cada capa

| Capa | Tipo | Campos |
|---|---|---|
| HTTP | `TareaRequest`, `TareaResponse` | `id`, `titulo` |
| Dominio | `Tarea` | `identificador`, `nombre` |
| Persistencia | `TareaEntityCsv`, `TareaEntityJson`, `TareaEntityXml`, `TareaEntityJdbc`, `TareaEntityOrm` | `idTarea`, `nombreTarea` |

Completa los mappers estáticos. El mapper de servicio usa `toDomain` para convertir el mensaje de entrada. Cada mapper de persistencia usa `toEntity` y `toDomain`. El mapper de API usa `toResponse` para producir el mensaje de salida. Las entidades de fichero son clases Java con constructores, getters y setters.

## 2. Implementa los repositorios

| Tecnología | Objetivo |
|---|---|
| CSV | Lee el encabezado `id,titulo` de `data/tarea.csv` con Commons CSV. Trabaja con una lista y guarda los cambios con `saveAll`. |
| JSON | Usa Jackson para leer y escribir `data/tarea.json`. Conserva las tareas existentes y admite el fichero inicial vacío. |
| XML | Completa el CRUD en `RepositorioTareaFichero` y los métodos `readAll` y `writeAll` en `TareaRepositoryXml`. Usa Jackson `XmlMapper` y la clase contenedora del documento. |
| JDBC | Completa el CRUD con `JdbcTemplate` y consultas parametrizadas sobre `tareas_jdbc`. Recupera el ID generado al insertar. |
| ORM | Completa el CRUD sobre `tareas_orm` usando `ITareaRepositoryJpa`. Recupera la entidad guardada con su ID generado. |

Para los ficheros, asigna el identificador en el alta teniendo en cuenta los registros guardados. 

## 3. Completa un servicio por tecnología

Mantén los calificadores de los esqueletos. Cada servicio debe usar el repositorio de su tecnología. En el alta, construye una tarea con identificador nulo. Al actualizar, usa el identificador de la URL.

| Método | Retorno esperado |
|---|---|
| `listar()` | `List<Tarea>`, que puede estar vacía. |
| `buscar(id)` | `Optional<Tarea>`, vacío si el ID no existe. |
| `crear(request)` | `Tarea` guardada con su ID. |
| `actualizar(id, request)` | `Optional<Tarea>`, vacío si el ID no existe. |
| `eliminar(id)` | `boolean`, verdadero si se eliminó una tarea. |

## 4. Expón y documenta la API

Define las rutas base `/api/csv/tareas`, `/api/json/tareas`, `/api/xml/tareas`, `/api/jdbc/tareas` y `/api/orm/tareas`. Añade las anotaciones de método HTTP, valida los cuerpos de POST y PUT, convierte las tareas a `TareaResponse` y aplica este contrato.

| Operación respecto a la ruta base | Resultado esperado |
|---|---|
| `GET` | `200` y una lista JSON. |
| `GET /{id}` | `200` con la tarea o `404` si el ID no existe. |
| `POST` | `201`, tarea creada y cabecera `Location`. Devuelve `400` si el título es inválido. |
| `PUT /{id}` | `200` con la tarea actualizada, `400` para un título inválido o `404` para un ID inexistente. |
| `DELETE /{id}` | `204` sin cuerpo o `404` para un ID inexistente. |

Escribe la documentación de Swagger en cada `@RestController` con `@Tag`, `@Operation` y `@ApiResponse`. Añade Javadoc a `ITareaService`, `ITareaRepository` e `ITareaRepositoryJpa`. Explica los parámetros, los retornos y cómo se representa un ID inexistente donde corresponda.

## 5. Comprueba la entrega

1. Ejecuta `mvn clean package` y arranca la aplicación desde la carpeta del POM.
2. Comprueba en [Swagger UI](http://localhost:8080/swagger-ui/index.html) que aparecen las 25 operaciones y sus respuestas documentadas.
3. Para cada tecnología, crea una tarea, consulta la URL que devuelve `Location`, actualiza el título y elimina la tarea. Comprueba el `404` al consultarla después y el `400` al enviar un título en blanco.
4. Reinicia la aplicación con una tarea de prueba guardada y verifica que sigue disponible.

## 6. Arranca y detén el servicio REST

Desde la carpeta que contiene `pom.xml` y `data/`, comprueba que Maven usa el JDK 21 y arranca la aplicación:

```bash
mvn clean spring-boot:run
```

Para detener la aplicación iniciada en primer plano, pulsa **Ctrl+C** en su terminal. Si se inició desde el IDE, pulsa **Stop**.

### Si el proceso sigue activo o el puerto está ocupado

En Linux o macOS, abre otra terminal. Filtra por el proceso que **escucha** en el puerto y busca los procesos con el nombre de la clase principal o del JAR:

```bash
lsof -nP -iTCP:8080 -sTCP:LISTEN
ps -ax -o pid= -o command= | grep -E 'aula[.]tareas[.]TareasApplication|tareas-0[.]0[.]1[.]jar'
```

Si no tienes `lsof`, en Linux puedes usar `ss -ltnp 'sport = :8080'`. Si cambiaste el puerto de la aplicación, usa ese puerto en los filtros.

Toma el PID de la salida, sustituye `12345` por ese valor y confirma que la línea de comandos corresponde a esta aplicación:

```bash
PID=12345
ps -p "$PID" -o pid=,args=
```

Una vez identificado el proceso, envía primero la señal de terminación normal:

```bash
kill -TERM "$PID"
```

Espera unos segundos y repite `ps -p "$PID" -o pid=,args=`. Si el mismo proceso sigue activo, usa **`kill -9` como último recurso**. Esta señal finaliza el proceso de inmediato y puede interrumpir escrituras pendientes.

```bash
kill -9 "$PID"
lsof -nP -iTCP:8080 -sTCP:LISTEN
```

Si el último comando no muestra ningún proceso, el puerto ya no tiene un servicio escuchando.

## 7. Envía peticiones y comprueba las respuestas en Swagger

En Swagger UI, elige **CSV**, **JSON**, **XML**, **JDBC** u **ORM**. Abre una operación, pulsa **Try it out**, rellena el cuerpo o el parámetro `id` cuando corresponda y pulsa **Execute**. En **Server response**, comprueba el código, el cuerpo y las cabeceras. Los ejemplos siguientes usan CSV. Repite la misma secuencia en las demás tecnologías.

| Petición | Datos que introduces en Swagger | Respuesta esperada |
|---|---|---|
| `GET /api/csv/tareas` | Ninguno | `200 OK` y una lista JSON. Si el almacén está vacío, devuelve `[]`. |
| `POST /api/csv/tareas` | `{"titulo":"Preparar entrega"}` | `201 Created`, la tarea creada y la cabecera `Location`. |
| `GET /api/csv/tareas/{id}` | El ID que devolvió el POST | `200 OK` y la tarea. |
| `PUT /api/csv/tareas/{id}` | El ID en la ruta y `{"titulo":"Entregar práctica"}` en el cuerpo | `200 OK` y la tarea con el título actualizado. |
| `POST /api/csv/tareas` | `{"titulo":" "}` | `400 Bad Request` por título en blanco. |
| `DELETE /api/csv/tareas/{id}` | El ID de la tarea creada | `204 No Content`, con el cuerpo vacío. |
| `GET /api/csv/tareas/{id}` | El ID que acabas de borrar | `404 Not Found`. También se espera `404` al intentar modificarlo o borrarlo de nuevo. |

**Ejemplo de cuerpo devuelto por POST**

```json
{
  "id": 7,
  "titulo": "Preparar entrega"
}
```

Con ese ID, `Location` apunta a `/api/csv/tareas/7`. Usa el identificador que devuelva tu ejecución, porque depende del almacenamiento y de las tareas ya guardadas. En POST puedes omitir `id`. En PUT, el identificador de la ruta determina qué tarea se modifica.
