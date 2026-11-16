# Primera API de tareas con Spring Boot

Instala JDK 21 y Maven. Comprueba con `java -version` y `mvn -version` que Maven usa JDK 21.
El proyecto fija Spring Boot 4.1.2 en el POM.

## 1. Arrancar

Desde la carpeta del proyecto ejecuta `mvn spring-boot:run`. Deja esa terminal abierta.
Desde otra terminal, ejecuta los ejemplos de abajo. En PowerShell usa `curl.exe`.

## 2. Crear y comprobar una tarea

```sh
curl -i http://localhost:8080/api/tareas
curl -i -H 'Content-Type: application/json' --data '{"titulo":"Preparar entrega"}' http://localhost:8080/api/tareas
curl -i http://localhost:8080/api/tareas/1
curl -i -H 'Content-Type: application/json' --data '{"titulo":" "}' http://localhost:8080/api/tareas
curl -i http://localhost:8080/api/tareas/999
```

En el primer arranque los resultados esperados son, en orden, **200** con `[]`,
**201** con cabecera `Location: /api/tareas/1`, **200** con la tarea creada,
**400** por título en blanco y **404** para el identificador desconocido.
Usa la ruta devuelta en `Location` si ya creaste otras tareas.

## 3. Seguir el código

`TareaController` recibe HTTP y valida el título. Spring le proporciona
`TareaService` mediante el constructor. El servicio conserva las tareas en
memoria, así que cada reinicio comienza con una lista vacía. La persistencia
en PostgreSQL se trabaja más adelante en la ruta.

## Entrega breve

Crea una segunda tarea con otro título. Entrega el código de estado, la
cabecera `Location` y el resultado de consultar esa URL.

Para empaquetar, ejecuta `mvn package`. Después puedes iniciar el JAR con
`java -jar target/tareas-0.0.1.jar`.

## Probar la API con Swagger UI

1. Arranca el ejemplo con `mvn spring-boot:run`.
2. Abre [Swagger UI](http://localhost:8080/swagger-ui/index.html). La descripción OpenAPI está en [`/v3/api-docs`](http://localhost:8080/v3/api-docs).
3. Despliega `POST /api/tareas`, pulsa **Try it out**, escribe `{"titulo":"Preparar entrega"}` y pulsa **Execute**. Comprueba el **201** y copia el identificador que aparece en el cuerpo o en la cabecera `Location`.
4. Abre `GET /api/tareas/{id}`, escribe ese identificador y verifica el **200** y el título. Repite el POST con `{"titulo":" "}` y verifica el **400**.

Observa el campo **Request URL**. Es la URL HTTP que recibe la petición. Cuando cambies el almacenamiento a fichero, SQLite o PostgreSQL, podrás repetir la práctica desde la misma interfaz y comprobar el dato en el almacenamiento de ese perfil.

El ejemplo de partida ofrece creación y consulta. Cuando implementes actualización, borrado y búsquedas en el controlador, springdoc incorporará esas operaciones a Swagger UI.
