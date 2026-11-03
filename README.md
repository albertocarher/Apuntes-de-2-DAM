# Taller mecánico — Guía de construcción paso a paso (JDBC → JPA / Hibernate)

>  Esta guía permite construir la solución de manera gradual y entender **por qué** se escribe cada clase, no solo copiar el resultado. Parte del proyecto `taller-mecanico-alumnado` y sigue la progresión de [Persistencia relacional y ORM con Java y SQLite](https://codelearnacademy.github.io/orm/): SQL → JDBC → mapeo manual → entidades JPA → `EntityManager` → repositorios → consultas → pruebas.

## 0. Qué vas a aprender y cómo utilizar esta guía

Al terminar podrás identificar tablas y claves, convertir tablas en entidades Java, configurar Hibernate, comprender las relaciones entre objetos y claves foráneas, gestionar el ciclo de vida de `EntityManager`, implementar CRUD con transacciones y escribir consultas JPQL con parámetros. También aprenderás a separar el **modelo de dominio** (`modelo`) de las **entidades de persistencia** (`orm/entity`).

**Método de trabajo:** en cada fase (1) lee el objetivo, (2) crea o modifica los archivos, (3) ejecuta la comprobación, (4) contesta las preguntas. No empieces por la capa ORM si no comprendes antes las consultas SQL y las claves foráneas.

**Requisitos:** JDK 17 o posterior, Maven 3.9 o posterior, IDE Java y SQLite (CLI o visor de bases de datos opcional). El proyecto incluye bases SQLite en `datos/`, y su esquema en `src/main/resources/schema.sql`.

> **Precaución:** trabaja siempre sobre una **copia** de `datos/taller.sqlite3`, por ejemplo `datos/taller-practicas.sqlite3`. La aplicación puede modificar los registros de esa copia. No uses la base de test original para pruebas manuales.

## 1. Reconoce los archivos del proyecto

```text
pom.xml
src/main/java/es/ejemplo/taller/
├── modelo/                   # Objetos de dominio existentes
│   ├── Cliente.java
│   ├── Vehiculo.java
│   ├── Mecanico.java
│   ├── Reparacion.java
│   └── PiezaReparacion.java
├── infra/ConexionBD.java    # Acceso JDBC existente
├── dao/                      # DAO JDBC existentes
└── orm/
    ├── config/OrmFactory.java
    ├── entity/               # 5 entidades JPA
    ├── mapper/               # 5 mappers
    ├── repository/           # IRepository, CRUD y consultas específicas
    └── DemoOrm.java
src/main/resources/
├── META-INF/persistence.xml
└── schema.sql
datos/taller.sqlite3
src/test/java/es/ejemplo/taller/  # Pruebas JDBC originales
```

**Propuesta para el aula:** si recibes el ZIP que ya incluye `orm/`, oculta temporalmente esa carpeta o utilízala solo al finalizar cada práctica para comparar tu implementación. La guía está pensada para escribir los archivos **en este orden**.

## 2. Analiza el modelo relacional antes de programar

Abre `schema.sql` y localiza las cinco tablas. Identifica para cada una su clave primaria (PK) y las claves foráneas (FK):

| Tabla | PK | FK | Relación lógica |
|---|---|---|---|
| `clientes` | `id_cliente` (entero autogenerado) | — | Un cliente puede tener muchos vehículos |
| `vehiculos` | `matricula` (texto asignado) | `id_cliente → clientes.id_cliente` | Cada vehículo pertenece a un cliente |
| `mecanicos` | `id_mecanico` (entero autogenerado) | — | Un mecánico interviene en muchas reparaciones |
| `reparaciones` | `id_reparacion` (entero autogenerado) | `matricula → vehiculos.matricula`, `id_mecanico → mecanicos.id_mecanico` | Cada reparación corresponde a un vehículo y un mecánico |
| `piezas_reparacion` | `id_pieza` (entero autogenerado) | `id_reparacion → reparaciones.id_reparacion` | Cada pieza registrada corresponde a una reparación |

**Esquema conceptual:** `Cliente 1—N Vehiculo 1—N Reparacion N—1 Mecanico`; `Reparacion 1—N PiezaReparacion`. Una FK reside en el extremo **N** de una relación 1:N.

Explora también las restricciones: `NOT NULL`, `UNIQUE` para el correo, valores positivos de cantidad, tarifas no negativas, y estado de reparación limitado a `PENDIENTE`, `EN_PROCESO`, `FINALIZADA`. JPA **no sustituye** las reglas de integridad de la base de datos.

**Comprueba:** ejecuta `SELECT * FROM clientes;` y luego un `JOIN`:

```sql
SELECT v.matricula, v.marca, c.nombre AS propietario
FROM vehiculos v
JOIN clientes c ON c.id_cliente = v.id_cliente;
```

**Preguntas:** ¿por qué la matrícula no usa `AUTOINCREMENT`? ¿Por qué no se puede insertar una reparación de un vehículo inexistente cuando SQLite comprueba las FK?

## 3. Detecta el problema que resuelve un ORM

Con JDBC, para recuperar un cliente debes preparar SQL, ejecutar la consulta, recorrer el `ResultSet` y copiar manualmente `id_cliente`, `nombre`, `email` y `telefono` a un `Cliente`. Para actualizarlo, debes escribir otra sentencia `UPDATE` y fijar cada parámetro. Esto hace que se repita el mapeo SQL ↔ Java.

Con JPA, una clase anotada como entidad describe la correspondencia. Hibernate (implementación de JPA) genera el SQL necesario para operaciones habituales; **no elimina** la necesidad de saber SQL, de diseñar las tablas ni de gestionar transacciones.

**Diferencia imprescindible:**

- `modelo/Cliente.java`: objeto del dominio que utiliza el resto de la aplicación.
- `orm/entity/ClienteEntity.java`: objeto que Hibernate conoce y asocia a `clientes`.
- `orm/mapper/ClienteMapper.java`: traduce entre ambos, evitando que todo el código de negocio dependa de JPA.
- `orm/repository/ClienteOrmRepository.java`: ofrece operaciones al código cliente y utiliza el `EntityManager`.

## 4. Preparación de Maven y librerías

Localiza el `pom.xml`. Estas son las dependencias que utiliza **esta solución**; puedes mantener las versiones ya incluidas en el proyecto (es importante que los dos módulos Hibernate tengan la misma versión).

```xml
<dependencies>
  <!-- Implementación JPA / motor ORM -->
  <dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>7.4.11.Final</version>
  </dependency>

  <!-- SQLite no es dialecto principal de Hibernate -->
  <dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
    <version>7.4.11.Final</version>
  </dependency>

  <!-- Driver JDBC real para acceder al fichero .sqlite3 -->
  <dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.46.1.0</version>
  </dependency>

  <!-- Tests -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

En `properties` conserva `<maven.compiler.release>17</maven.compiler.release>` y en `build/plugins` los plugins `maven-compiler-plugin` y `maven-surefire-plugin` del proyecto. `jakarta.persistence.*` llega transitivamente con Hibernate; las anotaciones **son JPA**, mientras que Hibernate es su motor de ejecución.

```bash
java -version
mvn -version
mvn dependency:resolve
mvn compile
```

**Comprueba:** Maven reconoce el proyecto y puede resolver las dependencias. Si falla antes de compilar, revisa el acceso a Maven Central, la versión de Java o si estás ejecutando el comando en la carpeta que contiene `pom.xml`.

## 5. Configura la unidad de persistencia

Crea `src/main/resources/META-INF/persistence.xml` (en el ZIP ya existe). Define una **unidad de persistencia**, es decir, el conjunto de entidades y propiedades que utilizará JPA:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.1">
  <persistence-unit name="tallerPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    <class>es.ejemplo.taller.orm.entity.ClienteEntity</class>
    <class>es.ejemplo.taller.orm.entity.VehiculoEntity</class>
    <class>es.ejemplo.taller.orm.entity.MecanicoEntity</class>
    <class>es.ejemplo.taller.orm.entity.ReparacionEntity</class>
    <class>es.ejemplo.taller.orm.entity.PiezaReparacionEntity</class>
    <properties>
      <property name="jakarta.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
      <property name="hibernate.dialect" value="org.hibernate.community.dialect.SQLiteDialect"/>
      <property name="hibernate.hbm2ddl.auto" value="none"/>
      <property name="hibernate.show_sql" value="true"/>
    </properties>
  </persistence-unit>
</persistence>
```

**Qué significa cada ajuste:** `tallerPU` es el nombre que utilizará Java; `RESOURCE_LOCAL` significa que la aplicación controla `begin/commit/rollback`; `provider` elige Hibernate; `class` registra las entidades; `driver` apunta a SQLite; `dialect` enseña a Hibernate las particularidades de SQL de SQLite; `hbm2ddl.auto=none` protege el esquema ya entregado; `show_sql=true` muestra las sentencias producidas (desactívalo cuando termines de estudiar).

La **URL de conexión no se fija aquí**: se pasa desde Java para poder elegir la copia de la base o una base de pruebas. Si SQLite abre un archivo nuevo vacío por error, Hibernate encontrará tablas inexistentes. Usa una ruta correcta, por ejemplo `jdbc:sqlite:datos/taller-practicas.sqlite3` ejecutando desde la raíz del proyecto.

## 6. Entiende EntityManagerFactory y EntityManager

Crea `orm/config/OrmFactory.java`:

```java
package es.ejemplo.taller.orm.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Map;

public final class OrmFactory {
    private OrmFactory() {}

    public static EntityManagerFactory crear(String jdbcUrl) {
        return Persistence.createEntityManagerFactory(
            "tallerPU",
            Map.of("jakarta.persistence.jdbc.url", jdbcUrl)
        );
    }
}
```

**Diferencia esencial:** `EntityManagerFactory` es costosa y se suele crear **una vez** durante la vida de la aplicación; `EntityManager` es una sesión o contexto de persistencia de **vida corta**, que se crea para una operación o unidad de trabajo. No compartas el mismo `EntityManager` entre hilos.

**Flujo de una operación:**

```text
Código cliente → Repositorio → EntityManagerFactory
                                  └─ createEntityManager()
                                        ├─ begin()  [si hay escritura]
                                        ├─ persist / find / query / merge / remove
                                        ├─ flush (cuando corresponda)
                                        ├─ commit() / rollback()
                                        └─ close()
```

Prueba de conexión (cuando hayas creado las entidades de las fases siguientes):

```java
try (var factory = OrmFactory.crear("jdbc:sqlite:datos/taller-practicas.sqlite3")) {
    try (var em = factory.createEntityManager()) {
        System.out.println(em.isOpen());
    }
}
```

**Comprueba:** `true` indica que el gestor está abierto; no demuestra todavía que todas las consultas funcionen. Cerrar el gestor y cerrar la fábrica son responsabilidades distintas.

## 7. Entidad 1 — Cliente: de una tabla a una clase JPA

Crea `orm/entity/ClienteEntity.java`:

```java
package es.ejemplo.taller.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    protected ClienteEntity() {} // Necesario para JPA

    public ClienteEntity(Integer idCliente, String nombre,
                         String email, String telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    // Genera getters y setters de los cuatro atributos con el IDE.
}
```

**Anotación por anotación:** `@Entity` indica que la clase es persistente; `@Table` especifica el nombre real de la tabla; `@Id` elige el campo que identifica cada fila; `@GeneratedValue(IDENTITY)` delega el ID autoincremental a la base; `@Column` mapea nombre y restricciones. El constructor sin parámetros permite que Hibernate cree instancias cuando recupera filas. El identificador usa `Integer` porque inicialmente puede ser `null` antes de `persist`.

**Actividad:** transforma `MecanicoEntity` del mismo modo; usa `id_mecanico`, `nombre`, `especialidad` y `tarifa_hora`. Para el dinero utiliza `BigDecimal`, no `double`, ya que hay problemas de precisión con los flotantes.

**Comprueba:** ambas clases compilan. Explica por qué en Cliente el ID se genera automáticamente y en Vehículo no.

## 8. Entidad 2 — Vehículo: clave primaria de texto y relación muchos-a-uno

La PK de `vehiculos` es `matricula`, que **proporciona la aplicación**: no lleva `@GeneratedValue`. Además, `id_cliente` es una FK a `clientes`.

En **esta solución de referencia** se utiliza una estrategia muy concreta: conservar el campo de FK `idCliente` como campo de escritura y añadir una asociación **solo de lectura de la FK**. Así se preservan los modelos existentes y los mappers solo necesitan números de ID.

```java
@Entity
@Table(name = "vehiculos")
public class VehiculoEntity {
    @Id
    @Column(name = "matricula")
    private String matricula;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private ClienteEntity cliente;

    protected VehiculoEntity() {}
    // Añade constructor, getters y setters como en el código entregado.
}
```

**Lee la relación de izquierda a derecha:** muchos vehículos pertenecen a un cliente (`@ManyToOne`). `@JoinColumn(name="id_cliente")` señala **la FK de la tabla vehiculos**, no la PK de clientes. `LAZY` intenta cargar el cliente cuando haga falta. `insertable=false, updatable=false` es importante: impide que JPA intente escribir la misma columna `id_cliente` dos veces (una por el entero y otra por el objeto); **para cambiar el propietario en esta solución se cambia `idCliente`, no `cliente`**.

**Opción alternativa, NO mezclarla con el ejemplo anterior:** puedes eliminar el entero `idCliente` y hacer que la asociación sea la fuente de escritura:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "id_cliente", nullable = false)
private ClienteEntity cliente;
```

En ese diseño necesitas asignar la referencia `ClienteEntity` al crear un vehículo (por ejemplo con `em.getReference(ClienteEntity.class, idCliente)`) y adaptar los mappers. **Elige una estrategia y manténla en toda la solución**; en el ZIP se implementa la primera.

**Comprueba:** ¿qué ocurre si la matrícula no existe al buscarla? ¿Qué columna cambia si reasignas un vehículo a otro cliente?

## 9. Entidades 3, 4 y 5 — completa todas las relaciones

### 9.1 `MecanicoEntity`

`@Entity`, `@Table(name="mecanicos")`, PK `id_mecanico` con `@GeneratedValue(IDENTITY)` y atributos `nombre`, `especialidad` y `tarifaHora` (`@Column(name="tarifa_hora")`, tipo `BigDecimal`). No contiene ninguna FK.

### 9.2 `ReparacionEntity`

La tabla `reparaciones` contiene dos FK: `matricula` y `id_mecanico`. Cada fila relaciona **un vehículo** y **un mecánico**. Sus campos son `idReparacion`, `matricula`, `idMecanico`, `descripcion`, `fecha`, `horasTrabajo` y `estado`; en la solución existente `fecha` y `estado` son `String` (no inventes tipos distintos al hacer los mappers).

Añade ambas asociaciones siguiendo el patrón de FK escribible + asociación de lectura:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "matricula", insertable = false, updatable = false)
private VehiculoEntity vehiculo;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_mecanico", insertable = false, updatable = false)
private MecanicoEntity mecanico;
```

**Por qué son dos `@ManyToOne`:** la reparación tiene un solo vehículo y un solo mecánico, pero cada uno puede aparecer en numerosas reparaciones. No estás estableciendo una relación directa muchos-a-muchos entre vehículos y mecánicos: `reparaciones` tiene datos propios (`descripcion`, `fecha`, `horas_trabajo`, `estado`) y es una entidad por sí misma.

### 9.3 `PiezaReparacionEntity`

PK `id_pieza`; FK `id_reparacion`; demás columnas `nombre`, `cantidad`, `precio_unitario` (este último es `BigDecimal`). Añade:

```java
@Column(name = "id_reparacion")
private Integer idReparacion;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_reparacion", insertable = false, updatable = false)
private ReparacionEntity reparacion;
```

**Ejercicio de extensión opcional:** añade en `ClienteEntity` una lista `@OneToMany(mappedBy="cliente")` de vehículos. `mappedBy` apunta al **atributo de asociación** que existe en `VehiculoEntity`, no al nombre SQL. No añadas `CascadeType.REMOVE` sin analizar el `ON DELETE RESTRICT` del esquema. Las colecciones bidireccionales requieren cuidar la sincronización entre ambos lados y la carga diferida.

**Cuestión clave:** `@ManyToOne` no genera una nueva columna si la FK `@JoinColumn` apunta a una existente. `@OneToMany` describe la navegación inversa, que no es obligatoria para guardar o consultar los datos.

## 10. Aprende el ciclo de vida de una entidad

| Estado | Significado | Ejemplo |
|---|---|---|
| **Transient / nueva** | Es un objeto Java aún no gestionado | `new ClienteEntity(...)` |
| **Managed / administrada** | Está dentro del contexto de persistencia | Después de `em.find()` o `em.persist()` |
| **Detached / separada** | Era administrada, pero el `EntityManager` ya está cerrado | Una entidad devuelta tras `em.close()` |
| **Removed / eliminada** | Marcada para borrarse al sincronizar cambios | Después de `em.remove(entidadGestionada)` |

`persist` registra una nueva entidad; `find` busca por PK; `merge` copia el estado de una entidad separada a una instancia administrada **y devuelve esa instancia**; `remove` elimina una instancia administrada. `flush` envía operaciones pendientes a la base pero **no confirma** la transacción; `commit` confirma. El ORM puede enviar SQL antes de `commit` cuando sea necesario.

**Error típico:** hacer `em.remove(new ClienteEntity(...))` o intentar acceder a una asociación `LAZY` después de cerrar el `EntityManager`. Para borrar primero recupera la entidad administrada; para leer relaciones usa un `JOIN FETCH` dentro del gestor abierto, o convierte lo necesario a un DTO antes de cerrarlo.

## 11. Construye los mappers dominio ↔ JPA

¿Por qué no devolver directamente `ClienteEntity` desde el repositorio? Porque el resto de la aplicación ya trabaja con `modelo.Cliente`; además, así se evita exponer estados administrados y detalles de Hibernate a la capa de dominio.

Crea `orm/mapper/ClienteMapper.java`. Adapta el constructor de `Cliente` a la firma que figure en `modelo/Cliente.java`:

```java
package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.orm.entity.ClienteEntity;

public final class ClienteMapper {
    private ClienteMapper() {}

    public static ClienteEntity toEntity(Cliente cliente) {
        return new ClienteEntity(cliente.getIdCliente(), cliente.getNombre(),
            cliente.getEmail(), cliente.getTelefono());
    }

    public static Cliente toDomain(ClienteEntity entidad) {
        return new Cliente(entidad.getIdCliente(), entidad.getNombre(),
            entidad.getEmail(), entidad.getTelefono());
    }
}
```

En `VehiculoMapper`, lee y escribe `idCliente`, no `cliente.getNombre()`: así evitas inicializar relaciones `LAZY` cuando solo quieres un modelo de dominio plano. En `ReparacionMapper`, conserva las FK `matricula` e `idMecanico`; y en `PiezaReparacionMapper`, `idReparacion`. El mapper de Mecánico debe copiar `BigDecimal tarifaHora` sin redondeos innecesarios.

**Comprueba:** crea un objeto de dominio en memoria, conviértelo a entidad y de vuelta, y verifica que los valores son iguales. Aún no necesitas base de datos para esta prueba.

## 12. Define el contrato genérico de repositorio

Crea `orm/repository/IRepository.java`:

```java
package es.ejemplo.taller.orm.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    void create(T entity);
    boolean update(T entity);
    boolean delete(ID id);
}
```

`T` es el **tipo de dominio** que ve la aplicación; `ID` es la PK (`Integer` en clientes, mecánicos, reparaciones y piezas; `String` para la matrícula). `Optional` expresa que un registro podría no existir. `update` y `delete` devuelven `false` si el ID no se encuentra. Un contrato estable permite intercambiar JDBC por ORM sin reescribir el código que invoca esos métodos.

## 13. Implementa el CRUD con EntityManager, primero sin generalizar

Construye primero los métodos de `ClienteOrmRepository` a mano. Cuando comprendas cómo funcionan, podrás extraer la repetición a `AbstractOrmRepository`.

### 13.1 CREATE: `persist()` y transacción

```java
public void crearCliente(Cliente cliente) {
    try (var em = factory.createEntityManager()) {
        var tx = em.getTransaction();
        try {
            tx.begin();
            ClienteEntity entidad = ClienteMapper.toEntity(cliente);
            em.persist(entidad);
            em.flush(); // Necesario aquí para leer inmediatamente el ID generado
            cliente.setIdCliente(entidad.getIdCliente());
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }
}
```

**Explicación línea a línea:** (1) se crea un contexto, (2) se obtiene la transacción, (3) `begin` abre la unidad de trabajo, (4) el mapper construye la entidad, (5) `persist` la registra, (6) `flush` sincroniza el INSERT para disponer del ID generado, (7) lo copiamos al objeto de dominio, (8) `commit` confirma, (9) si hay excepción se revierte con `rollback`, y (10) el try-with-resources cierra el `EntityManager`. Si el `commit` falla, la escritura no debe considerarse exitosa.

### 13.2 READ por ID: `find()`

```java
public Optional<Cliente> buscarCliente(int id) {
    try (var em = factory.createEntityManager()) {
        return Optional.ofNullable(em.find(ClienteEntity.class, id))
                       .map(ClienteMapper::toDomain);
    }
}
```

`find` consulta **por la clave primaria**, no por cualquier atributo. Devuelve `null` si no encuentra la fila. Al convertir dentro del `EntityManager` abierto, no dependemos de una sesión posterior.

### 13.3 READ todos: JPQL

```java
public List<Cliente> listarClientes() {
    try (var em = factory.createEntityManager()) {
        return em.createQuery(
                "select c from ClienteEntity c order by c.nombre",
                ClienteEntity.class)
            .getResultStream()
            .map(ClienteMapper::toDomain)
            .toList();
    }
}
```

**JPQL no es SQL:** escribe el **nombre de la clase** `ClienteEntity` y sus **atributos Java** (`nombre`), no `clientes` ni `id_cliente`. Hibernate traduce la expresión a SQL para SQLite.

### 13.4 UPDATE: `find()` + cambios administrados

```java
public boolean actualizarEmail(int id, String nuevoEmail) {
    try (var em = factory.createEntityManager()) {
        var tx = em.getTransaction();
        try {
            tx.begin();
            ClienteEntity c = em.find(ClienteEntity.class, id);
            if (c == null) {
                tx.commit();
                return false;
            }
            c.setEmail(nuevoEmail); // dirty checking
            tx.commit();           // Hibernate detecta el cambio
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }
}
```

No necesitamos llamar a `merge` aquí porque `c` **ya es administrada**. En cambio, para actualizar un objeto de dominio convertido a nueva `ClienteEntity`, el repositorio genérico usa `em.merge(...)`, tras comprobar que la fila existe.

### 13.5 DELETE: `remove()`

```java
public boolean borrarCliente(int id) {
    try (var em = factory.createEntityManager()) {
        var tx = em.getTransaction();
        try {
            tx.begin();
            ClienteEntity c = em.find(ClienteEntity.class, id);
            if (c == null) {
                tx.commit();
                return false;
            }
            em.remove(c);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }
}
```

**Atención:** si existen vehículos asociados al cliente y las FK se hacen cumplir, la base puede rechazar el borrado por `ON DELETE RESTRICT`. Esto es comportamiento esperado, no un fallo del ORM. Si una escritura falla, observa el mensaje SQL y comprueba el `rollback`.

**Comprobación del paso:** crea un cliente, recupera el ID, consulta, modifica el correo, vuelve a consultar y borra **solo** un cliente recién creado que no tenga vehículos.

## 14. Refactoriza el código repetido a AbstractOrmRepository

Observa lo que se repite en los cinco repositorios: abrir y cerrar un gestor, abrir/cerrar transacciones, `find`, `persist`, `merge`, `remove` y manejar fallos. Crea `AbstractOrmRepository<T,E,ID>`:

- `T`: tipo de dominio (`Cliente`).
- `E`: entidad (`ClienteEntity`).
- `ID`: tipo de PK (`Integer`).
- `Function<T,E> toEntity`: conversión a persistencia.
- `Function<E,T> toDomain`: conversión a dominio.
- `Function<T,ID> domainId`: extracción del ID.
- `BiConsumer<T,E> onPersist`: copia del ID generado al modelo.

La clase completa está en `orm/repository/AbstractOrmRepository.java` del proyecto. **No empieces por copiarla**: primero identifica qué líneas se repiten en el CRUD anterior. A continuación examina su método de escritura, que es la pieza central:

```java
protected <R> R write(Function<EntityManager, R> action) {
    try (EntityManager em = factory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    } catch (RuntimeException ex) {
        throw new RepositoryException("Error de escritura ORM", ex);
    }
}
```

`action.apply(em)` permite pasar diferentes operaciones sin volver a copiar la gestión de transacciones. `read(...)` abre y cierra el gestor pero no abre una transacción de escritura. En la implementación entregada, el `create` genérico utiliza `persist` + `flush` y sincroniza el ID; `update` verifica primero si existe y después llama a `merge`; `delete` usa `find` + `remove`.

Un repositorio concreto queda muy pequeño:

```java
public final class ClienteOrmRepository
    extends AbstractOrmRepository<Cliente, ClienteEntity, Integer> {

    public ClienteOrmRepository(EntityManagerFactory factory) {
        super(factory, ClienteEntity.class,
              ClienteMapper::toEntity,
              ClienteMapper::toDomain,
              Cliente::getIdCliente,
              (dominio, entidad) -> dominio.setIdCliente(entidad.getIdCliente()));
    }
}
```

**Comprueba:** ¿por qué los vehículos usan `String` en `ID`? ¿Por qué para Vehículo `onPersist` no tiene que copiar ninguna PK generada? ¿Qué ocurre con una operación fallida dentro de `write`?

## 15. Construye los cinco repositorios específicos

Cada repositorio hereda CRUD y añade consultas del dominio:

| Clase | Métodos específicos que debes implementar |
|---|---|
| `ClienteOrmRepository` | CRUD genérico (sin específicos adicionales en la referencia) |
| `VehiculoOrmRepository` | `buscarPorCliente(Integer)`, `listarPorMarca(String)` |
| `MecanicoOrmRepository` | `buscarPorEspecialidad(String)` |
| `ReparacionOrmRepository` | `buscarPorVehiculo(String)`, `buscarPorMecanico(Integer)`, `listarPendientes()` |
| `PiezaReparacionOrmRepository` | `listarPorReparacion(Integer)`, `calcularCostePiezas(Integer)` |

**Orden recomendado:** completa Cliente → Mecánico → Vehículo → Reparación → Pieza. Este orden sigue las dependencias entre tablas y facilita crear datos de prueba válidos.

## 16. Aprende a escribir consultas específicas JPQL

### 16.1 Filtros por atributos

Ejemplo: vehículos de un cliente (`VehiculoOrmRepository`):

```java
public List<Vehiculo> buscarPorCliente(Integer idCliente) {
    return read(em -> em.createQuery(
            "select v from VehiculoEntity v where v.idCliente = :id",
            VehiculoEntity.class)
        .setParameter("id", idCliente)
        .getResultStream()
        .map(VehiculoMapper::toDomain)
        .toList());
}
```

**Desmenuzado:** `select v` selecciona objetos; `VehiculoEntity` es la entidad, `v` su alias; `where v.idCliente = :id` filtra usando el atributo Java; `:id` es un parámetro con nombre; `setParameter` lo fija sin concatenar entradas de usuario; `getResultStream()` obtiene resultados; `map` los convierte al modelo.

Repite el patrón:

```java
// Vehículos por marca
"select v from VehiculoEntity v where v.marca = :marca"

// Mecánicos por especialidad
"select m from MecanicoEntity m where m.especialidad = :valor"

// Reparaciones de un vehículo
"select r from ReparacionEntity r where r.matricula = :valor"

// Reparaciones de un mecánico
"select r from ReparacionEntity r where r.idMecanico = :valor"

// Reparaciones pendientes
"select r from ReparacionEntity r where r.estado = 'PENDIENTE'"
```

**Comprueba:** ¿qué cambia si el parámetro contiene una comilla simple? Al usar `setParameter`, no se convierte en parte del código de la consulta.

### 16.2 Navegar relaciones mediante objetos

Una ventaja del mapeo ORM es poder usar rutas entre asociaciones:

```java
// Se navega de vehículo → cliente
"select v from VehiculoEntity v where v.cliente.email = :email"

// Se navega de reparación → vehículo → cliente
"select r from ReparacionEntity r where r.vehiculo.cliente.idCliente = :id"
```

**Importante:** estas consultas usan atributos de entidad, no nombres de tabla. Hibernate traduce la navegación a las uniones necesarias; para filtrar por la FK simple `v.idCliente`, no necesitas recorrer `v.cliente`.

### 16.3 JOIN y JOIN FETCH (diferencia)

```java
// JOIN para filtrar reparaciones por especialidad del mecánico
"select r from ReparacionEntity r " +
"join r.mecanico m where m.especialidad = :esp"

// JOIN FETCH para cargar el vehículo en el mismo contexto
"select r from ReparacionEntity r join fetch r.vehiculo " +
"where r.idReparacion = :id"
```

Un `JOIN` permite combinar entidades para filtrar. Un `JOIN FETCH` solicita además la carga inmediata de la asociación indicada (útil antes de cerrar el gestor). Cuidado al utilizar varios `JOIN FETCH` de colecciones: puede multiplicar filas y ser ineficiente. En este taller las asociaciones mostradas son principalmente `ManyToOne`.

### 16.4 Ordenación, paginación y conteo

```java
// Ordenación
"select r from ReparacionEntity r order by r.fecha desc"

// Conteo de reparaciones por estado
Long total = em.createQuery(
        "select count(r) from ReparacionEntity r where r.estado = :estado",
        Long.class)
    .setParameter("estado", "PENDIENTE")
    .getSingleResult();

// Paginación
var pagina = em.createQuery(
        "select r from ReparacionEntity r order by r.idReparacion",
        ReparacionEntity.class)
    .setFirstResult(0)
    .setMaxResults(10)
    .getResultList();
```

`getSingleResult` es apropiado para `count`, que siempre produce una fila agregada. Para búsquedas de un único registro que puede faltar, es más cómodo `getResultStream().findFirst()` o `getResultList()`.

### 16.5 Agregaciones y coste de piezas

En el proyecto de referencia `calcularCostePiezas` recupera las piezas y suma en Java `precioUnitario × cantidad` usando `BigDecimal`:

```java
public BigDecimal calcularCostePiezas(Integer idReparacion) {
    return listarPorReparacion(idReparacion).stream()
        .map(p -> p.getPrecioUnitario()
            .multiply(BigDecimal.valueOf(p.getCantidad())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

**Ampliación:** propón una consulta JPQL con `sum(p.precioUnitario * p.cantidad)` y `coalesce` para devolver cero cuando no haya piezas. **Advertencia:** verifica el tipo numérico de la suma con el proveedor y el dialecto (SQLite/Hibernate) antes de convertirla a `BigDecimal`; para la solución base usa la suma en Java, más fácil de razonar.

**Actividad:** crea `buscarReparacionesPorEstado(String estado)` mediante un parámetro `:estado`, en lugar de incluir el literal `PENDIENTE`. Valida que el estado pertenezca al conjunto permitido.

## 17. Trabaja correctamente con relaciones y transacciones

**Caso práctico — registrar una reparación con piezas:** primero debe existir el `Vehiculo` y el `Mecanico`, luego se crea una reparación y finalmente las piezas con su `idReparacion`. Si necesitas que todas esas operaciones sean atómicas, **no llames por separado a varios `create()` que abren su propia transacción**. Crea un **servicio** que abra un solo `EntityManager`, inicie una transacción y registre todos los cambios antes de un único `commit`.

```java
try (var em = factory.createEntityManager()) {
    var tx = em.getTransaction();
    try {
        tx.begin();
        // Obtener referencias existentes (no crear vehículos/mecánicos ficticios).
        // persist(reparacion); flush(); persist(pieza1); persist(pieza2);
        // Usar las entidades y FK según la estrategia de mapeo adoptada.
        tx.commit();
    } catch (RuntimeException ex) {
        if (tx.isActive()) tx.rollback();
        throw ex;
    }
}
```

Este bloque es un **esqueleto didáctico**, no una operación lista para copiar: el alumnado debe implementar la creación y el enlace de entidades respetando la estrategia de FK elegida. Al usar `@JoinColumn(insertable=false, updatable=false)`, fija los atributos FK (`matricula`, `idMecanico`, `idReparacion`) que realmente se escriben.

**Comprobación:** provoca un fallo al insertar una pieza con cantidad `0` y verifica que **no se ha guardado ninguna parte** de la transacción. Para que SQLite valide referencias externas también en las conexiones que abre Hibernate, comprueba el estado de `PRAGMA foreign_keys` para esa conexión; no supongas que ejecutar el `PRAGMA` una sola vez sobre otra conexión lo activa globalmente.

## 18. Ejecuta una demostración de lectura

El proyecto incluye `orm/DemoOrm.java`. Antes de ejecutarlo, revisa la URL JDBC que emplea y confirma que abre tu copia de SQLite. Un ejemplo alternativo de uso es:

```java
try (var factory = OrmFactory.crear("jdbc:sqlite:datos/taller-practicas.sqlite3")) {
    var clientes = new ClienteOrmRepository(factory);
    var vehiculos = new VehiculoOrmRepository(factory);

    clientes.findAll().forEach(c -> System.out.println(c.getNombre()));
    vehiculos.buscarPorCliente(1)
             .forEach(v -> System.out.println(v.getMatricula()));
}
```

Añade los imports necesarios de `OrmFactory` y los repositorios. En la primera ejecución limita la demo a lecturas; así podrás comparar el resultado ORM con consultas SQL directas y comprobar que no se han modificado datos.

## 19. Escribe pruebas automáticas del ORM

Los tests que venían con el proyecto prueban principalmente los **DAO JDBC** y **no sustituyen** a las pruebas de la nueva capa ORM. Añade tests propios, sobre una **copia aislada** de la base de pruebas para no alterar datos compartidos.

Casos mínimos que hay que comprobar:

1. `findAll` recupera filas existentes y `findById` devuelve `Optional.empty()` cuando no existe el ID.
2. `create` persiste un cliente y copia su PK autogenerada al modelo de dominio.
3. `update` cambia un dato y retorna `false` para un ID inexistente.
4. `delete` borra un registro sin dependencias y retorna `false` para un ID inexistente.
5. `buscarPorCliente` devuelve únicamente vehículos de ese cliente.
6. `buscarPorMecanico`, `buscarPorVehiculo` y `listarPendientes` aplican sus filtros.
7. `calcularCostePiezas` suma correctamente cantidades y precios y retorna cero si no hay piezas.
8. Se produce un `rollback` completo al fallar una escritura múltiple.

Esqueleto ilustrativo de JUnit 5:

```java
@Test
void buscarClienteInexistenteDevuelveEmpty() {
    try (var factory = OrmFactory.crear(urlBaseTemporal)) {
        var repo = new ClienteOrmRepository(factory);
        assertTrue(repo.findById(-123456).isEmpty());
    }
}
```

El valor `urlBaseTemporal` debe configurarse en el test, por ejemplo copiando la SQLite de pruebas a un archivo temporal. Importa `@Test`, `assertTrue` y las clases del proyecto. No ejecutes escrituras sobre la SQLite de producción.

```bash
mvn clean test
```

**Criterio de evaluación:** distingue errores de **compilación** (clases, imports, tipos) de fallos de **ejecución** (base inexistente, tabla que falta, restricción FK, transacción, dialecto). Que `mvn compile` termine correctamente **no** prueba que Hibernate pueda leer o escribir la base.

## 20. Errores habituales y cómo diagnosticarlos

| Síntoma | Causa probable | Qué revisar |
|---|---|---|
| `no such table: clientes` | Ruta JDBC apunta a una SQLite nueva/vacía | Directorio de trabajo y archivo en `datos/` |
| `Unknown entity` / entidad no gestionada | Falta `@Entity` o el registro en `persistence.xml` | Paquete, nombre completo, unidad `tallerPU` |
| `Not an entity` en JPQL | Has escrito nombre SQL en vez de entidad | `ClienteEntity` en vez de `clientes` |
| Error de columna duplicada | Se escribe FK tanto como entero como objeto | `insertable=false, updatable=false` en la asociación de lectura |
| `LazyInitializationException` | Se accede a relación diferida tras cerrar el contexto | `JOIN FETCH` o transformación a DTO dentro del gestor |
| Error `UNIQUE constraint failed` | Correo repetido | Validar email, gestionar excepción y rollback |
| Error de FK / borrado rechazado | Registro referenciado o FK inexistente | Orden de inserciones, esquema, `ON DELETE RESTRICT` |
| Un cambio no se guarda | Falta transacción o `commit` | `begin`, `commit`, `rollback`; estado administrado |
| Cambio a `cliente` no se persiste | Asociación de solo lectura | Actualizar `idCliente` en esta variante de diseño |
| La consulta encuentra cero filas | Parámetro, mayúsculas, datos o ruta incorrectos | `setParameter`, DB usada, SQL generado |

## 21. Actividad final: construye un caso de uso completo

**Enunciado:** añade un cliente, registra uno de sus vehículos, asigna al vehículo una reparación realizada por un mecánico existente, agrega dos piezas y muestra un informe que incluya matrícula, nombre del cliente, mecánico, descripción, estado y coste total de piezas.

**Secuencia de trabajo:** define qué se crea y qué debe existir antes; diseña el servicio y su transacción; implementa las consultas; crea pruebas de éxito y de rollback; finalmente explica por qué el código cliente no necesita escribir SQL.

**Ampliaciones:** consulta reparaciones por email del cliente, paginación por fecha, coste de mano de obra (`horasTrabajo × tarifaHora`) y suma total de la reparación. Redacta en cada caso la versión SQL equivalente a tu JPQL y comprueba que ambas dan resultados coherentes.

## 22. Lista de comprobación para entregar

- [ ] Identifico PK, FK y restricciones de las cinco tablas.
- [ ] Entiendo el CRUD existente con JDBC y qué mapeo repite.
- [ ] `pom.xml` compila con las bibliotecas ORM y SQLite.
- [ ] `persistence.xml` registra las cinco entidades.
- [ ] Distingo la `EntityManagerFactory` del `EntityManager`.
- [ ] He creado cinco entidades con los nombres reales de tablas y columnas.
- [ ] Las cuatro FK están correctamente representadas como asociaciones `@ManyToOne`.
- [ ] Comprendo por qué las asociaciones son de solo lectura en esta implementación.
- [ ] He escrito y probado los cinco mappers.
- [ ] He implementado el CRUD con transacciones y `rollback`.
- [ ] Comprendo `persist`, `find`, `merge`, `remove`, `flush` y los estados de entidad.
- [ ] He implementado los cinco repositorios y todas sus consultas específicas.
- [ ] Sé explicar `JOIN`, `JOIN FETCH`, `setParameter` y `getResultStream`.
- [ ] He probado la solución con una base de datos aislada.
- [ ] Puedo explicar qué código ha dejado de ser necesario respecto a JDBC.

## 23. Entrega recomendada

Entrega el proyecto Maven, la guía completada con respuestas a las preguntas y una captura o salida de `mvn clean test`. Incluye una breve comparación de **una** operación JDBC y su equivalente JPA: SQL escrito manualmente, copia de columnas, control de transacciones, código del repositorio y resultado obtenido. Si alguna prueba no pasa, explica cuál es la causa y qué has comprobado.

---

**Nota para el profesorado:** el ZIP de referencia contiene la solución ORM, los DAO JDBC y los tests JDBC originales. La guía enseña a reproducir la capa ORM existente, cuya estrategia conserva las FK escalares como fuente de escritura; las consultas avanzadas y la transacción de caso de uso son propuestas de ampliación y requieren implementación. La ejecución real de tests Hibernate depende de un entorno Maven disponible y debe verificarse antes de utilizarla como criterio de corrección.


---

# ANEXO DOCENTE — Laboratorio explicado línea a línea

> Este anexo amplía las fases anteriores. Se recomienda **seguirlo con el editor abierto**, crear las clases en el orden indicado, ejecutar cada comprobación y anotar el resultado. Las variantes marcadas como «ampliación» no sustituyen al diseño original hasta que se modifiquen también los mappers y los repositorios.

## A. Inspección real del proyecto antes de tocar JPA

### A.1. Las cinco tablas y sus identificadores

| Tabla SQLite | Entidad Java | Identificador Java | Tipo | Claves que enlazan tablas |
|---|---|---|---|---|
| `clientes` | `ClienteEntity` | `idCliente` | `Integer` | — |
| `vehiculos` | `VehiculoEntity` | `matricula` | `String` | `id_cliente` → clientes |
| `mecanicos` | `MecanicoEntity` | `idMecanico` | `Integer` | — |
| `reparaciones` | `ReparacionEntity` | `idReparacion` | `Integer` | `matricula` → vehículos; `id_mecanico` → mecánicos |
| `piezas_reparacion` | `PiezaReparacionEntity` | `idPieza` | `Integer` | `id_reparacion` → reparaciones |

**Paso A1.** Abre `datos/taller.sqlite3` con un visor SQLite. Ejecuta:

```sql
SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name;
PRAGMA table_info(vehiculos);
PRAGMA foreign_key_list(vehiculos);
PRAGMA table_info(reparaciones);
PRAGMA foreign_key_list(reparaciones);
```

**Paso A2.** Dibuja las cardinalidades: un cliente puede tener varios vehículos; un vehículo puede tener varias reparaciones; un mecánico puede realizar varias reparaciones; una reparación puede contener varias piezas. **La clave foránea está siempre en el lado «muchos»** de estos ejemplos.

**Paso A3.** Localiza las clases `modelo/` y comprueba que son objetos de dominio, no entidades JPA. Deben poder emplearse aunque cambie el motor de persistencia. Escribe en el cuaderno qué campos de dominio corresponden a cada columna.

**Comprobación:** ¿puede haber dos reparaciones para una misma matrícula? Sí. ¿Puede haber dos vehículos con la misma matrícula? No, porque es clave primaria.

## B. Maven: cada dependencia, por qué y dónde actúa

En el `pom.xml` de esta solución se utiliza Java 17 como versión de compilación, Hibernate ORM y el dialecto comunitario de SQLite con la **misma versión**. **No mezcles versiones de las dos dependencias de Hibernate**. Antes de introducir una dependencia nueva, verifica su compatibilidad con la versión de Java y con el resto del proyecto.

```xml
<dependencies>
    <!-- Implementación de JPA y proveedor de EntityManager. -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>7.4.11.Final</version>
    </dependency>

    <!-- Traduce operaciones Hibernate al dialecto de SQLite. -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-community-dialects</artifactId>
        <version>7.4.11.Final</version>
    </dependency>

    <!-- Driver JDBC real: el ORM sigue comunicándose mediante JDBC. -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.46.1.0</version>
    </dependency>

    <!-- Pruebas; no forma parte de la aplicación en producción. -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.11.4</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Pregunta razonada:** ¿por qué Hibernate necesita `sqlite-jdbc`? Porque el ORM convierte acciones sobre objetos en SQL, pero el driver es el componente que establece la conexión y envía dicho SQL a la base de datos.

**Prueba de fase:** ejecuta `mvn -version` y `mvn -DskipTests compile`. Si falla la descarga de librerías, revisa la red, la configuración de Maven y las coordenadas del POM antes de alterar el código Java.

## C. `persistence.xml`: los ajustes que debes saber justificar

Archivo: `src/main/resources/META-INF/persistence.xml`. El nombre de la unidad debe coincidir **literalmente** con el usado en `Persistence.createEntityManagerFactory("tallerPU")`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.1">
  <persistence-unit name="tallerPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    <class>es.ejemplo.taller.orm.entity.ClienteEntity</class>
    <class>es.ejemplo.taller.orm.entity.VehiculoEntity</class>
    <class>es.ejemplo.taller.orm.entity.MecanicoEntity</class>
    <class>es.ejemplo.taller.orm.entity.ReparacionEntity</class>
    <class>es.ejemplo.taller.orm.entity.PiezaReparacionEntity</class>
    <properties>
      <property name="jakarta.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
      <property name="hibernate.dialect" value="org.hibernate.community.dialect.SQLiteDialect"/>
      <property name="hibernate.hbm2ddl.auto" value="none"/>
      <property name="hibernate.show_sql" value="false"/>
    </properties>
  </persistence-unit>
</persistence>
```

- `RESOURCE_LOCAL`: nuestra aplicación controla `begin`, `commit` y `rollback`; no hay gestor de transacciones de servidor.
- `provider`: indica qué implementación de JPA se utiliza.
- `<class>`: registra cada entidad persistente. Si una falta, ciertas consultas o relaciones fallarán.
- `hibernate.dialect`: adapta el SQL generado a SQLite.
- `hbm2ddl.auto=none`: **no crea ni actualiza las tablas**; conserva el esquema entregado. No usar `create`/`create-drop` sobre los datos de trabajo.
- `show_sql`: para estudiar SQL puede establecerse temporalmente a `true`, preferiblemente en una configuración local de desarrollo.

**Conexión JDBC:** inspecciona `orm/config/OrmFactory.java`. Esta solución construye las propiedades de conexión en Java; la ubicación de la BD depende de la URL suministrada a esa fábrica. No dupliques una segunda URL incompatible dentro de `persistence.xml`.

**Prueba de fase:** abre la fábrica, crea un `EntityManager`, comprueba `em.isOpen()`, cierra el gestor y finalmente la fábrica. Si aparece «Unknown entity», comprueba el paquete, `@Entity` y su registro en XML.

## D. Entidades: explicación completa de cada anotación y cada decisión

### D.1. Primera entidad: `ClienteEntity`

Crea `orm/entity/ClienteEntity.java` y comienza con este esqueleto (añade constructor vacío, getters y setters):

```java
package es.ejemplo.taller.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "telefono")
    private String telefono;

    public ClienteEntity() { }
    // Generar getters y setters con el IDE.
}
```

1. `@Entity` hace que JPA reconozca la clase como persistente. El nombre de entidad por defecto es `ClienteEntity` y es el utilizado en JPQL.
2. `@Table(name = "clientes")` relaciona la clase con la tabla real; el nombre de clase **no tiene por qué** coincidir con la tabla.
3. `@Id` señala la identidad de la entidad dentro de su tabla y del contexto de persistencia.
4. `@GeneratedValue(IDENTITY)` indica que la clave se obtiene del mecanismo de inserción de la base de datos; antes de crear una fila nueva, el ID suele ser `null`.
5. `@Column` relaciona el atributo Java con una columna SQL; las mayúsculas/minúsculas y convenciones de nombres importan según el proveedor.
6. El constructor sin argumentos permite que JPA instancie la entidad. Los setters son necesarios para los ejercicios y para facilitar el trabajo con los mappers.

**Evita** poner `@GeneratedValue` en `VehiculoEntity`: su matrícula es una clave natural proporcionada por la aplicación, no un entero autogenerado.

### D.2. Segunda entidad: `VehiculoEntity`

```java
@Entity
@Table(name = "vehiculos")
public class VehiculoEntity {
    @Id
    @Column(name = "matricula")
    private String matricula;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private ClienteEntity cliente;

    public VehiculoEntity() { }
    // Añadir getters y setters.
}
```

**Por qué hay dos representaciones de `id_cliente`:** `idCliente` representa directamente el valor de la columna y es quien lo escribe; `cliente` permite navegar hasta el objeto relacionado (`vehiculo.getCliente()`). Ambas apuntan a la **misma columna**. Si se permitiese escribir desde ambos campos, existirían dos rutas potencialmente contradictorias para actualizarla; por eso la asociación usa `insertable=false, updatable=false`.

**Consecuencia práctica:** `v.setCliente(otroCliente)` **no cambia por sí solo la FK** que se escribe en esta configuración; la operación de persistencia debe establecer `v.setIdCliente(otroCliente.getIdCliente())`. Además, si `cliente` ya estaba cargado en un contexto, actualizar la FK no sustituye automáticamente la referencia Java almacenada: conviene recargar la entidad o sincronizar el diseño conscientemente.

### D.3. Tercera entidad: `MecanicoEntity`

Tiene `idMecanico`, `nombre`, `especialidad`, `tarifaHora`. El ID es generado y el importe debe conservarse en `BigDecimal` (no en `double` para cálculos monetarios exactos). Ejemplo de campos:

```java
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_mecanico")
private Integer idMecanico;

@Column(name = "tarifa_hora")
private BigDecimal tarifaHora;
```

**Actividad:** completa `@Entity`, `@Table(name="mecanicos")`, constructor, restantes columnas y accesores; comprueba que el fichero importa `java.math.BigDecimal`.

### D.4. Cuarta entidad: `ReparacionEntity`

Una reparación se identifica por `idReparacion`. Contiene `matricula`, `idMecanico`, `descripcion`, `fecha`, `horasTrabajo` y `estado` y puede navegar a `VehiculoEntity` y `MecanicoEntity`:

```java
@Column(name = "matricula")
private String matricula;

@Column(name = "id_mecanico")
private Integer idMecanico;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "matricula", insertable = false, updatable = false)
private VehiculoEntity vehiculo;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_mecanico", insertable = false, updatable = false)
private MecanicoEntity mecanico;
```

**Comprende la cardinalidad:** muchas reparaciones pertenecen al mismo vehículo y muchas reparaciones pueden estar asignadas al mismo mecánico. Por eso **ambas** relaciones son `@ManyToOne`. No se utiliza `@ManyToMany`: no existe una tabla puente entre `reparaciones` y `mecanicos` para permitir varios mecánicos por reparación en este diseño.

La propiedad `fecha` es `String` en esta versión. En un diseño nuevo podría ser `LocalDate`, **pero no la cambies aislada**: antes revisa el formato de la columna, los valores existentes, los mappers y las pruebas.

### D.5. Quinta entidad: `PiezaReparacionEntity`

```java
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_pieza")
private Integer idPieza;

@Column(name = "id_reparacion")
private Integer idReparacion;

@Column(name = "cantidad")
private Integer cantidad;

@Column(name = "precio_unitario")
private BigDecimal precioUnitario;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_reparacion", insertable = false, updatable = false)
private ReparacionEntity reparacion;
```

Cada fila representa una pieza utilizada en una reparación; una reparación puede tener varias filas de piezas. El coste de una fila es `precioUnitario × cantidad`, **no** solo `precioUnitario`.

### D.6. Tabla de decisiones de mapeo

| Situación | Anotación / tipo | Motivo |
|---|---|---|
| Tabla existente con otro nombre | `@Table(name="...")` | Evita depender de convenciones de nombres |
| Clave primaria | `@Id` | Identidad de persistencia |
| Entero autogenerado | `@GeneratedValue(IDENTITY)` | La BD asigna el valor |
| Matrícula introducida por el usuario | Solo `@Id` | La aplicación conoce el ID |
| Nombre SQL diferente | `@Column(name="...")` | Evita columnas incorrectas |
| Relación N:1 | `@ManyToOne` | La FK está en esta tabla |
| Especificar columna FK | `@JoinColumn` | Enlaza la asociación con la FK |
| FK ya mapeada como campo | `insertable=false, updatable=false` en la asociación | Una única ruta de escritura |
| Asociación diferida | `fetch=LAZY` | Reduce cargas innecesarias, exige contexto abierto al acceder |

## E. Relaciones bidireccionales: ampliación opcional y riesgos

El proyecto **solo necesita** relaciones unidireccionales `@ManyToOne`. Si quieres acceder desde un cliente a su lista de vehículos, puedes añadir en `ClienteEntity`:

```java
@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
private List<VehiculoEntity> vehiculos = new ArrayList<>();
```

**Explicación:** `mappedBy="cliente"` apunta al **nombre del campo Java** `VehiculoEntity.cliente`, no a la columna `id_cliente`. El lado inverso no tiene su propia `@JoinColumn`; la FK sigue estando en `vehiculos`. De forma análoga se podrían añadir `reparaciones` a `VehiculoEntity` y `MecanicoEntity`, y `piezas` a `ReparacionEntity`.

**Precauciones:**

1. Esto añade colecciones diferidas. Leerlas después de cerrar el `EntityManager` puede lanzar `LazyInitializationException`.
2. Con el diseño de FK escalar de esta solución, cambiar solo la lista o la referencia inversa **no modifica** automáticamente la columna FK escrita. Hay que mantener coherente `idCliente`.
3. No añadas `CascadeType.ALL` sin analizar el efecto de borrar un cliente o una reparación: podrías borrar registros que no quieres eliminar.
4. No incluyas relaciones bidireccionales indiscriminadamente en `toString()`, `equals()` o serialización JSON: pueden producir ciclos y cargas no deseadas.

**Ejercicio:** representa qué lado guarda físicamente la FK en cada asociación bidireccional. Después implementa únicamente `ClienteEntity.vehiculos`, consulta la colección **dentro** del gestor y explica por qué funciona.

## F. `EntityManager` como máquina de estados: seguimiento de una sesión

```java
EntityManager em = factory.createEntityManager();
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    ClienteEntity cliente = new ClienteEntity();  // transient
    cliente.setNombre("Prueba ORM");
    cliente.setEmail("prueba@example.org");
    em.persist(cliente);                          // managed
    em.flush();                                   // sincroniza SQL; NO confirma
    Integer id = cliente.getIdCliente();
    ClienteEntity otraReferencia = em.find(ClienteEntity.class, id);
    System.out.println(cliente == otraReferencia); // true en el mismo contexto
    tx.rollback();                                // descarta cambios de BD
} finally {
    em.close();
}
```

**Analiza cada línea:**

- `createEntityManager`: abre un **contexto de persistencia**, no la fábrica. La fábrica es compartible, pero un `EntityManager` no debe compartirse entre hilos.
- `tx.begin()`: marca el comienzo de una unidad atómica de trabajo.
- `new`: objeto transitorio; no está asociado a la base de datos.
- `persist`: lo convierte en administrado; el SQL se ejecuta en el momento oportuno según proveedor y generación de ID.
- `flush`: intenta sincronizar operaciones pendientes con la base de datos; **no sustituye a `commit`**.
- `find`: busca por identificador; si el objeto ya está en el contexto devuelve la misma instancia administrada.
- `rollback`: anula la transacción en la base de datos; no garantiza restaurar todos los valores Java de las instancias ya modificadas. Es aconsejable desechar el gestor tras un error.
- `close`: las entidades supervivientes dejan de estar administradas por ese gestor.

**Cambios automáticos:**

```java
var tx = em.getTransaction();
tx.begin();
ClienteEntity c = em.find(ClienteEntity.class, idExistente);
if (c != null) c.setTelefono("600000000");
tx.commit(); // dirty checking: si cambió una entidad administrada, se sincroniza
```

**`merge` no significa “guardar cualquier cosa sin consecuencias”:** devuelve una **instancia administrada** distinta cuando se aplica sobre un objeto desconectado. Utiliza la instancia devuelta si vas a seguir modificándola; comprueba primero si existe cuando tu contrato de repositorio exija que `update()` devuelva `false` para IDs inexistentes.

## G. Crear el repositorio de manera incremental

### G.1. Primero, repositorio concreto y fácil de depurar

```java
public final class ClienteRepositorioInicial {
    private final EntityManagerFactory factory;

    public ClienteRepositorioInicial(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public Optional<ClienteEntity> buscar(Integer id) {
        try (EntityManager em = factory.createEntityManager()) {
            return Optional.ofNullable(em.find(ClienteEntity.class, id));
        }
    }
}
```

**Importante:** este ejemplo devuelve **entidades**, únicamente para comprender JPA. La solución final devuelve objetos del paquete `modelo` usando mappers y no expone clases JPA al resto de la aplicación.

### G.2. Extraer un helper de escritura transaccional

```java
private <R> R escribir(Function<EntityManager, R> accion) {
    try (EntityManager em = factory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = accion.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
```

Cada operación de escritura debe ejecutar `commit` cuando todo sale bien y `rollback` si falla. **No** hagas `commit` en `finally`: eso podría confirmar datos tras un error. Tampoco cierres la `EntityManagerFactory` por cada operación, solo cuando la aplicación termina.

### G.3. Pasar a `AbstractOrmRepository`

La clase real `orm/repository/AbstractOrmRepository.java` mantiene los tipos `<T,E,ID>`: `T` es **dominio**, `E` es **entidad JPA**, `ID` es el **tipo de clave**. Utiliza funciones que reciben y devuelven objetos de ambos mundos; así el CRUD es reutilizable.

```java
// En ClienteOrmRepository: la referencia a cada función define el mapeo.
super(factory,
      ClienteEntity.class,
      ClienteMapper::toEntity,
      ClienteMapper::toDomain,
      Cliente::getIdCliente,
      (dominio, entidad) -> dominio.setIdCliente(entidad.getIdCliente()));
```

**Interpretación del último argumento:** después de insertar, la base de datos puede generar el ID; hay que devolverlo al objeto `Cliente` del dominio. **En vehículos**, cuyo ID es `matricula`, esa generación no ocurre.

**Tareas de clase:** cambia el nombre de una función de mapper para ver el error de compilación, vuelve a dejarlo como estaba y explica por qué el compilador garantiza la compatibilidad entre `T` y `E`.

## H. Consultas específicas: receta completa para crear cada método

**Secuencia común para todas las consultas:**

1. Expresa la pregunta en lenguaje natural: «¿qué reparaciones hizo este mecánico?».
2. Identifica la entidad resultado: `ReparacionEntity`.
3. Elige el atributo para filtrar: `idMecanico` o la ruta `mecanico.idMecanico`.
4. Redacta el JPQL con un parámetro `:id` (no concatenes valores recibidos del usuario).
5. Ejecuta con `createQuery(jpql, ReparacionEntity.class)` y `setParameter`.
6. Obtén la lista y convierte cada entidad a `Reparacion` **antes** de cerrar el gestor.
7. Comprueba resultados para ID existente, ID sin coincidencias y entrada inválida.

### H.1. Buscar vehículos de un cliente

```java
public List<Vehiculo> buscarPorCliente(Integer idCliente) {
    return read(em -> em.createQuery(
            "select v from VehiculoEntity v where v.idCliente = :id " +
            "order by v.matricula", VehiculoEntity.class)
        .setParameter("id", idCliente)
        .getResultStream()
        .map(VehiculoMapper::toDomain)
        .toList());
}
```

**Traducción mental a SQL:** `SELECT ... FROM vehiculos WHERE id_cliente = ? ORDER BY matricula`. JPQL selecciona **entidades y sus propiedades Java**; Hibernate usa las anotaciones para construir el SQL real.

### H.2. Buscar reparaciones por estado parametrizado

```java
public List<Reparacion> buscarPorEstado(String estado) {
    return read(em -> em.createQuery(
            "select r from ReparacionEntity r where r.estado = :estado " +
            "order by r.idReparacion", ReparacionEntity.class)
        .setParameter("estado", estado)
        .getResultStream()
        .map(ReparacionMapper::toDomain)
        .toList());
}
```

¿Por qué mejor que una consulta fija para «PENDIENTE»? Un solo método resuelve varios estados. **Validar** el conjunto de estados permitidos corresponde a la capa de servicio o dominio, no a concatenar SQL.

### H.3. Consulta con `JOIN`: reparaciones de una especialidad

```java
public List<Reparacion> buscarPorEspecialidad(String especialidad) {
    return read(em -> em.createQuery(
            "select r from ReparacionEntity r " +
            "join r.mecanico m where m.especialidad = :esp " +
            "order by r.idReparacion", ReparacionEntity.class)
        .setParameter("esp", especialidad)
        .getResultStream()
        .map(ReparacionMapper::toDomain)
        .toList());
}
```

`join r.mecanico m` usa el **atributo de relación**, que ya sabe qué columna FK une ambas tablas. `join` sirve para filtrar; **no equivale** a cargar todos los datos asociados para usarlos después de cerrar el gestor.

### H.4. `JOIN FETCH` para evitar un acceso diferido fuera del gestor

```java
ReparacionEntity r = em.createQuery(
    "select r from ReparacionEntity r " +
    "join fetch r.vehiculo where r.idReparacion = :id",
    ReparacionEntity.class)
    .setParameter("id", id)
    .getResultStream().findFirst().orElse(null);
```

**Ejercicio:** intenta acceder a `r.getVehiculo().getMarca()` dentro y fuera del gestor, con y sin `fetch`. La conclusión debe describir el contexto de persistencia y el proxy diferido, no simplemente memorizar la excepción. Si existen reparaciones sin vehículo válido, un `join` interno las excluiría; analiza si necesitas `left join fetch`.

### H.5. Contar sin cargar filas enteras

```java
long total = em.createQuery(
    "select count(r) from ReparacionEntity r where r.idMecanico = :id",
    Long.class).setParameter("id", idMecanico).getSingleResult();
```

**Ventaja:** para contar, el motor de datos realiza la agregación; no se materializan todas las reparaciones en Java. El tipo del resultado de `count` es `Long`.

### H.6. Ordenación y paginación estables

```java
List<ReparacionEntity> pagina = em.createQuery(
        "select r from ReparacionEntity r order by r.idReparacion",
        ReparacionEntity.class)
    .setFirstResult(20) // saltar 20 registros: tercera página de tamaño 10
    .setMaxResults(10)
    .getResultList();
```

Nunca pagines sin `order by` estable: las filas no tienen orden implícito. `setFirstResult` empieza en cero; página 0: desplazamiento 0, página 1: 10 y página 2: 20.

### H.7. Calcular el coste de piezas con precisión

```java
BigDecimal total = piezas.stream()
    .map(p -> p.getPrecioUnitario()
        .multiply(BigDecimal.valueOf(p.getCantidad())))
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

`BigDecimal.ZERO` es el resultado correcto para una reparación sin piezas. Evita `new BigDecimal(0.1)` y prefiere `new BigDecimal("0.10")` cuando crees valores decimales literales. Una ampliación es obtener el total mediante JPQL con `sum`, pero debes ensayar qué tipo devuelve Hibernate con SQLite antes de asumir `BigDecimal`.

### H.8. Ejercicio de diagnóstico de consultas

Para cada consulta, escribe la entidad resultado, el parámetro, el posible SQL y el método Java esperado:

```jpql
select v from VehiculoEntity v where v.cliente.email = :email
select r from ReparacionEntity r where r.vehiculo.matricula = :matricula
select p from PiezaReparacionEntity p where p.idReparacion = :id
select count(r) from ReparacionEntity r where r.estado = :estado
```

**Cuestión:** ¿en qué casos basta filtrar por `idCliente`, `matricula` o `idReparacion` sin navegar asociaciones? Argumenta la diferencia entre filtrar por clave y filtrar por atributos de otra tabla.

## I. Caso completo: registrar reparación y piezas en UNA transacción

Los repositorios base abren un `EntityManager` y una transacción por método. Si se llama `repoReparacion.create(...)` y después falla `repoPieza.create(...)`, la reparación podría quedar guardada. La solución para una operación de negocio atómica es agrupar las inserciones dentro del mismo contexto.

```java
public Integer registrarReparacionConPiezas(
        EntityManagerFactory factory,
        Reparacion reparacion,
        List<PiezaReparacion> piezas) {

    try (EntityManager em = factory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Deben existir antes de la operación; no se crean aquí.
            if (em.find(VehiculoEntity.class, reparacion.getMatricula()) == null) {
                throw new IllegalArgumentException("Vehículo inexistente");
            }
            if (em.find(MecanicoEntity.class, reparacion.getIdMecanico()) == null) {
                throw new IllegalArgumentException("Mecánico inexistente");
            }

            ReparacionEntity entidad = ReparacionMapper.toEntity(reparacion);
            em.persist(entidad);
            em.flush(); // permite recuperar el ID generado
            Integer id = entidad.getIdReparacion();

            for (PiezaReparacion pieza : piezas) {
                PiezaReparacionEntity e = PiezaReparacionMapper.toEntity(pieza);
                e.setIdReparacion(id); // FK escalar: es la que escribe la BD
                em.persist(e);
            }

            tx.commit();
            reparacion.setIdReparacion(id);
            return id;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }
}
```

Este código requiere los imports de `modelo`, `orm.entity`, `orm.mapper`, `jakarta.persistence` y `java.util.List`. Debes ubicarlo en una **clase de servicio**, no como método suelto; el `EntityManagerFactory` se proporciona desde la composición de la aplicación.

**Traza esperada:** `begin → comprobar vehículo/mecánico → persist reparación → flush → persist piezas → commit`. Si una operación falla: `rollback` y ninguna inserción de esa transacción queda confirmada.

**Actividad experimental:** introduce una pieza que viole una restricción de la tabla y comprueba que no se guarda la reparación. Para confirmar atomicidad, consulta los datos después del fallo usando un **nuevo** `EntityManager`.

## J. Pruebas ORM independientes del JDBC heredado

Las pruebas JDBC existentes no prueban por sí solas los nuevos repositorios. Añade pruebas dedicadas a `orm/repository` que utilicen una base de datos de prueba **aislada**. No ejecutes operaciones destructivas sobre `datos/taller.sqlite3`.

```java
@Test
void debeEncontrarVehiculosDeUnCliente() {
    // Preparar un EntityManagerFactory apuntando a una BD temporal
    // con el mismo esquema que la BD de trabajo.
    // Crear un cliente y dos vehículos, todos en esa BD de prueba.
    // Invocar buscarPorCliente(idCliente).
    // Afirmar que devuelve exactamente esas dos matrículas.
}
```

Este pseudotest señala deliberadamente el **arranque necesario**: la configuración de la fábrica debe apuntar a un archivo temporal con esquema inicializado, no a la base de datos de producción o entregada. El `hbm2ddl.auto=none` significa que el alumno debe preparar las tablas antes del test.

Pruebas concretas para implementar:

| Prueba | Preparación | Resultado esperado |
|---|---|---|
| `createCliente` | Cliente sin ID | ID generado y fila consultable |
| `findByIdInexistente` | ID que no existe | `Optional.empty()` |
| `updateCliente` | Cliente previamente guardado | Cambios persistidos en otra sesión |
| `deleteCliente` | Cliente sin vehículos | La fila desaparece |
| `buscarVehiculosCliente` | 2 vehículos de A, 1 de B | 2 para A |
| `listarPendientes` | Varios estados | Solo `PENDIENTE` |
| `reparacionConPiezas` | Reparación + dos piezas | Todo confirmado o todo revertido |
| `costePiezas` | 2 × 10.50 y 3 × 4.00 | `33.00` numéricamente |

Para importes `BigDecimal`, considera `compareTo` o una comparación numérica apropiada, porque `equals` también distingue la escala decimal (`33.0` y `33.00`).

**Comandos de validación:**

```bash
mvn clean compile
mvn test
mvn -DskipTests package
```

Si Maven falla, adjunta el mensaje completo, distingue error de compilación, configuración ORM y restricción SQL, y localiza el archivo responsable antes de cambiar anotaciones al azar.

## K. Guía de resolución de incidencias con razonamiento

| Síntoma | Causa frecuente | Comprobación / solución |
|---|---|---|
| `No Persistence provider` | XML, dependencias o nombre incorrecto | Revisa `META-INF/persistence.xml` y `tallerPU` |
| `Unknown entity` | Clase sin `@Entity` o mal registrada | Verifica las cinco entidades y el paquete |
| `no such table` | URL apunta a otra SQLite o esquema no inicializado | Imprime/inspecciona la URL real de la fábrica |
| `no such column` | `@Column` no refleja el DDL | Consulta `PRAGMA table_info(...)` |
| `LazyInitializationException` | Se navega por una asociación después de cerrar `em` | Consulta/proyecta datos mientras el gestor esté abierto o usa `join fetch` pertinente |
| `TransactionRequiredException` | Escritura sin transacción | Usa `begin`, `commit`, `rollback` |
| `detached entity passed to persist` | Se intenta insertar algo que no es nuevo | Distingue `persist` de la actualización mediante entidad administrada/`merge` |
| FK sin actualizar | Se cambió solo una asociación de lectura | Actualiza el campo `idCliente`, `idMecanico`, etc. |
| Diferencias en dinero | `double`, escala `BigDecimal` | Usa `BigDecimal` y pruebas numéricas |
| Pruebas que alteran datos reales | Se reutilizó la BD entregada | Crea fichero SQLite temporal y prepara esquema independiente |

## L. Entrega por hitos y criterios de evaluación

- **Hito 1 — Base relacional:** esquema documentado con PK, FK y cardinalidades; consulta SQL de cada tabla.
- **Hito 2 — Entidades:** cinco clases anotadas, identificadores correctos y todas las columnas verificadas.
- **Hito 3 — Relaciones:** cuatro asociaciones `ManyToOne` explicadas; justificación de la FK escalar y la asociación de lectura.
- **Hito 4 — Configuración:** `pom.xml`, `persistence.xml` y `OrmFactory` explicados; conexión comprobada.
- **Hito 5 — Repositorio:** `IRepository`, mappers, CRUD y transacciones; ID generado de cliente devuelto correctamente.
- **Hito 6 — Consultas:** búsqueda por cliente, estado, especialidad; al menos un `join`, un `count` y una página estable.
- **Hito 7 — Servicio atómico:** reparación con dos piezas, rollback de una operación fallida y consulta posterior.
- **Hito 8 — Pruebas y memoria:** comandos, capturas o salidas de tests, explicaciones y decisiones justificadas.

**Preguntas para la defensa oral:** ¿qué diferencia hay entre clase de dominio y entidad? ¿Por qué `EntityManagerFactory` se comparte pero `EntityManager` no? ¿Qué diferencia hay entre `flush` y `commit`? ¿Por qué `merge` devuelve una instancia? ¿En qué tabla está cada FK? ¿Qué sucede si modificas solo una asociación con `updatable=false`? ¿Por qué `JOIN FETCH` no debería aplicarse indiscriminadamente? ¿Por qué una transacción por método de repositorio no basta para un caso de uso de varios pasos?

**Regla final del laboratorio:** entrega código comprensible y comprobable. El objetivo no es memorizar anotaciones: es poder explicar cómo un objeto Java acaba representado por una fila SQL y cómo proteger la integridad de sus relaciones.
