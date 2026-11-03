# Apuntes: JDBC, SQLite, DAO y AbstractDao

## 1. Arquitectura general

```text
Main / UI
   ↓ usa
Interfaz DAO  (IClienteDao, IMascotaDao…)
   ↓ implementa
DAO concreto  (ClienteDao, MascotaDao…)
   ↓ hereda de
AbstractDao<T, ID>
   ↓ usa
ConexionBD
   ↓ abre
SQLite
```

**Responsabilidades:**

- `modelo`: clases Java normales con atributos, constructores, getters y setters.
- `dao`: interfaces y DAO; contienen SQL y convierten `ResultSet` en objetos.
- `infra`: `ConexionBD`; crea, valida y configura conexiones.
- `excepción`: `DaoException` envuelve errores SQL.
- `UI/Main`: no debe contener SQL.

## 2. ConexionBD

**Idea clave:** una única clase responsable de abrir y configurar conexiones. No significa una conexión global abierta siempre: cada operación DAO abre su conexión y la cierra con `try-with-resources`.

```java
public class ConexionBD {

    private final String url;

    protected ConexionBD(String url) {
        this.url = url;
    }

    public static Connection abrir(String url) throws SQLException {
        return new ConexionBD(url).obtenerConexion();
    }

    public Connection obtenerConexion() throws SQLException {
        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("URL SQLite no válida");
        }

        String ruta = url.substring("jdbc:sqlite:".length());

        if (!ruta.equals(":memory:")
                && !ruta.startsWith("file:")
                && !Files.isRegularFile(Path.of(ruta))) {
            throw new IllegalArgumentException("La base de datos no existe: " + ruta);
        }

        Connection cn = DriverManager.getConnection(url);

        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            cn.close();
            throw e;
        }

        return cn;
    }
}
```

**Qué hacer siempre:**

1. Validar que la URL empieza por `jdbc:sqlite:`.
2. Comprobar que el archivo existe antes de conectar.
3. Conectar con `DriverManager.getConnection(url)`.
4. Activar `PRAGMA foreign_keys = ON`.
5. Devolver la conexión abierta; quien la abre la cierra.

**Por qué `abrir(String url)` es estático:** los DAO no pueden usar `new ConexionBD(url)` si el constructor es `protected`; `abrir(url)` es el punto de acceso público.

## 3. Contratos DAO

| Método | Devuelve |
|---|---|
| `listar()` | `List<T>`, nunca `null`; vacía si no hay filas |
| `buscarPorId(ID id)` | `Optional<T>`; vacío si no existe o `id` es `null` |
| `insertar(T entidad)` | ID generado por la base de datos |
| `actualizar(T entidad)` | Número de filas afectadas |
| `eliminar(ID id)` | Número de filas afectadas |
| Filtros | `List<T>` |
| Cálculos | `int`, `double` o `BigDecimal` |

**Reglas importantes:**

- `buscarPorId(null)` devuelve `Optional.empty()`, no lanza excepción.
- `eliminar(null)` suele lanzar `NullPointerException`.
- `actualizar` con `id == null` lanza `IllegalArgumentException`.
- Los errores SQL se convierten en `DaoException`, conservando la causa.
- Nunca concatenar valores del usuario en el SQL: usar `?` y `PreparedStatement`.

## 4. DAO básico con ID autogenerado

Ejemplo reutilizable para una entidad con `id INTEGER PRIMARY KEY AUTOINCREMENT`.

```java
public class ClienteDao implements IClienteDao {

    private final String url;

    public ClienteDao(String url) {
        this.url = url;
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("telefono")
        );
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT id, nombre, email, telefono FROM clientes ORDER BY id";
        List<Cliente> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapCliente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar clientes", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, email, telefono FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCliente(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar cliente por id", e);
        }
    }

    @Override
    public int insertar(Cliente entidad) {
        String sql = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entidad.setId(id);
                    return id;
                }
            }

            throw new DaoException("No se pudo obtener el ID generado", null);

        } catch (SQLException e) {
            throw new DaoException("Error al insertar cliente", e);
        }
    }

    @Override
    public int actualizar(Cliente entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("El cliente debe tener id");
        }

        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.setInt(4, entidad.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar cliente", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El id no puede ser nulo");
        }

        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar cliente", e);
        }
    }
}
```

## 5. ID autogenerado vs. ID natural

| Tipo de clave | Ejemplo | En `insertar()` |
|---|---|---|
| Autogenerada | `id INTEGER PRIMARY KEY AUTOINCREMENT` | No se envía el `id`; se usa `RETURN_GENERATED_KEYS` y `getGeneratedKeys()` |
| Natural / proporcionada | `matricula VARCHAR PRIMARY KEY` | El ID ya viene en el objeto; se inserta normalmente y se devuelve `ps.executeUpdate()` |

**ID autogenerado:**

```java
PreparedStatement ps = cn.prepareStatement(
        sql,
        Statement.RETURN_GENERATED_KEYS
);

ps.executeUpdate();

try (ResultSet rs = ps.getGeneratedKeys()) {
    if (rs.next()) {
        int id = rs.getInt(1);
        entidad.setId(id);
        return id;
    }
}
```

**ID natural, por ejemplo matrícula:**

```java
String sql = "INSERT INTO vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)";

try (Connection cn = ConexionBD.abrir(url);
     PreparedStatement ps = cn.prepareStatement(sql)) {

    ps.setString(1, entidad.getMatricula());
    ps.setString(2, entidad.getMarca());
    ps.setString(3, entidad.getModelo());
    ps.setInt(4, entidad.getAnio());

    return ps.executeUpdate();
}
```

Aquí `executeUpdate()` devuelve `1` si se insertó y `0` si no; **no** se busca ID generado.

## 6. Filtros y cálculos

Patrón para filtros:

```java
public List<Mascota> buscarPorCliente(Integer idCliente) {
    String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente "
               + "FROM mascotas WHERE id_cliente = ? ORDER BY id";

    List<Mascota> lista = new ArrayList<>();

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapMascota(rs));
            }
        }

        return lista;

    } catch (SQLException e) {
        throw new DaoException("Error al filtrar mascotas por cliente", e);
    }
}
```

Patrón para costes o sumas:

```java
public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
    if (idConsulta == null) {
        return BigDecimal.ZERO;
    }

    String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) AS total "
               + "FROM tratamientos WHERE id_consulta = ?";

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idConsulta);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getBigDecimal("total") != null) {
                return rs.getBigDecimal("total");
            }
        }

        return BigDecimal.ZERO;

    } catch (SQLException e) {
        throw new DaoException("Error al calcular coste", e);
    }
}
```

**Recuerda:** `COALESCE(x, 0)` evita devolver `null` cuando no hay filas; `BigDecimal` se compara con `compareTo`, no con `equals`.

## 7. DaoException

```java
public class DaoException extends RuntimeException {

    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
```

Uso:

```java
catch (SQLException e) {
    throw new DaoException("Error al listar clientes", e);
}
```

**Nunca** hacer:

```java
catch (SQLException e) {
    e.printStackTrace();
}
```

Porque oculta el error y no respeta el contrato.

## 8. AbstractDao<T, ID>

La clase abstracta debe reutilizar **mecanismos comunes**, no el SQL concreto de cada tabla.

```java
public abstract class AbstractDao<T, ID> {

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> consultarLista(PreparedStatement ps) throws SQLException {
        List<T> lista = new ArrayList<>();

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }

        return lista;
    }

    protected Optional<T> consultarUno(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }

        return Optional.empty();
    }

    protected int ejecutarActualizacion(PreparedStatement ps) throws SQLException {
        return ps.executeUpdate();
    }
}
```

### DAO que hereda

```java
public class ClienteDao extends AbstractDao<Cliente, Integer>
        implements IClienteDao {

    private final String url;

    public ClienteDao(String url) {
        this.url = url;
    }

    @Override
    protected Cliente mapRow(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("telefono")
        );
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT id, nombre, email, telefono FROM clientes ORDER BY id";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultarLista(ps);

        } catch (SQLException e) {
            throw new DaoException("Error al listar clientes", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, email, telefono FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return consultarUno(ps);

        } catch (SQLException e) {
            throw new DaoException("Error al buscar cliente", e);
        }
    }
}
```

### Qué implementar en cada DAO

| En `AbstractDao<T, ID>` | En cada DAO concreto |
|---|---|
| `mapRow(ResultSet)` abstracto | Conversión real de fila a objeto |
| `consultarLista(ps)` | Usado por `listar()` y filtros |
| `consultarUno(ps)` | Usado por `buscarPorId()` |
| `ejecutarActualizacion(ps)` | Usado por `actualizar()` y `eliminar()` |
| Gestión común de errores, si se decide | `DaoException` con mensaje específico |

**No metas en la clase base:**

- El SQL de cada tabla.
- Los `ps.setXxx(...)`, porque cambian según la entidad.
- `insertar()`, porque cambia si el ID es autogenerado o natural.
- Validaciones muy específicas de una tabla.

## 9. Relaciones entre clases

### Herencia

```java
public abstract class AbstractDao<T, ID> { ... }

public class ClienteDao extends AbstractDao<Cliente, Integer>
        implements IClienteDao { ... }
```

- `T` = tipo de entidad: `Cliente`, `Mascota`, etc.
- `ID` = tipo de clave primaria: `Integer` o `String`.
- `implements` = cumple el contrato de la interfaz.
- `extends` = reutiliza código de la clase abstracta.

### Composición

```java
public class ClienteDao {

    private final String url;

    public ClienteDao(String url) {
        this.url = url;
    }
}
```

El DAO **tiene una** URL y usa `ConexionBD.abrir(url)`.

### Dependencia

```java
ClienteDao dao = new ClienteDao("jdbc:sqlite:datos/clinica.sqlite3");
List<Cliente> clientes = dao.listar();
```

La interfaz permite programar contra `IClienteDao`, no contra la clase concreta:

```java
IClienteDao dao = new ClienteDao(url);
```

## 10. Esquema para copiar en el examen

```text
ENTIDAD = clase Java con atributos, constructores, getters y setters.

DAO = objeto que accede a la tabla:
- listar() -> List<T>
- buscarPorId(id) -> Optional<T>
- insertar(entidad) -> ID generado
- actualizar(entidad) -> filas afectadas
- eliminar(id) -> filas afectadas

CONEXIONBD:
- validar URL jdbc:sqlite:
- comprobar que el archivo existe
- DriverManager.getConnection(url)
- PRAGMA foreign_keys = ON
- devolver Connection

REGLAS JDBC:
- SQL con ? y PreparedStatement
- try-with-resources para Connection, PreparedStatement y ResultSet
- ResultSet -> objeto con mapRow()
- SQLException -> DaoException(mensaje, e)
- INSERT autogenerado -> RETURN_GENERATED_KEYS + getGeneratedKeys()
- INSERT con clave natural -> executeUpdate()
- SUM sin filas -> COALESCE(SUM(...), 0)

ABSTRACTDAO<T, ID>:
- mapRow(ResultSet) abstracto
- consultarLista(ps) -> List<T>
- consultarUno(ps) -> Optional<T>
- ejecutarActualizacion(ps) -> int

HERENCIA:
ClienteDao extends AbstractDao<Cliente, Integer>
             implements IClienteDao

ID:
- AUTOINCREMENT -> Integer, getGeneratedKeys()
- natural -> String/Integer, executeUpdate()
```

## 11. Errores típicos que preguntan o testean

- Devolver `executeUpdate()` en un `INSERT` autogenerado: mal; hay que devolver el ID.
- Olvidar `id` en el `SELECT` y luego hacer `rs.getInt("id")`: error de mapeo.
- Llamar a `ps.setInt(1, id)` con `id == null`: `NullPointerException`.
- Usar `LIKE` cuando se pide coincidencia exacta: mejor `=`.
- Sumar solo `cantidad` en vez de `cantidad * precio_unitario`.
- Devolver `null` en un cálculo: debe ser `BigDecimal.ZERO`.
- No cerrar recursos: usar siempre `try-with-resources`.
- Concatenar valores en SQL: usar siempre `?`.
- Usar una conexión estática global: rompe el aislamiento entre pruebas.

## 12. Plantilla mental para cualquier DAO

```text
1. Constructor: guardar url.
2. mapRow(ResultSet): convertir columnas SQL a objeto.
3. listar(): SELECT + consultarLista.
4. buscarPorId(): si id null -> Optional.empty(); SELECT WHERE id = ? + consultarUno.
5. insertar():
   - ID autogenerado: RETURN_GENERATED_KEYS + getGeneratedKeys().
   - ID natural: executeUpdate().
6. actualizar(): UPDATE ... WHERE id = ?; devolver executeUpdate().
7. eliminar(): DELETE WHERE id = ?; devolver executeUpdate().
8. Filtros: SELECT + WHERE campo = ? + consultarLista.
9. Cálculos: SELECT COALESCE(SUM(...), 0).
```

Con esto tienes lo esencial: **ConexionBD centraliza conexión, DAO implementa contratos, AbstractDao reutiliza mecanismo común, y el tipo de ID determina cómo se hace el `insertar()`**.

# Ejemplos de DAO para el examen

## 1. DAO con ID autogenerado

```java
public class ClienteDao implements IClienteDao {

    private final String url;

    public ClienteDao(String url) {
        this.url = url;
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("telefono")
        );
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT id, nombre, email, telefono FROM clientes ORDER BY id";
        List<Cliente> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapCliente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar clientes", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, email, telefono FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCliente(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar cliente por id", e);
        }
    }

    @Override
    public int insertar(Cliente entidad) {
        String sql = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entidad.setId(id);
                    return id;
                }
            }

            throw new DaoException("No se pudo obtener el ID generado", null);

        } catch (SQLException e) {
            throw new DaoException("Error al insertar cliente", e);
        }
    }

    @Override
    public int actualizar(Cliente entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("El cliente debe tener id");
        }

        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.setInt(4, entidad.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar cliente", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El id no puede ser nulo");
        }

        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar cliente", e);
        }
    }
}
```

## 2. DAO con ID natural

Ejemplo con `matricula` como clave primaria. **No** se usa `RETURN_GENERATED_KEYS` porque el ID ya lo proporciona el usuario o el objeto.

```java
public class VehiculoDao implements IVehiculoDao {

    private final String url;

    public VehiculoDao(String url) {
        this.url = url;
    }

    private Vehiculo mapVehiculo(ResultSet rs) throws SQLException {
        return new Vehiculo(
                rs.getString("matricula"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getInt("anio")
        );
    }

    @Override
    public List<Vehiculo> listar() {
        String sql = "SELECT matricula, marca, modelo, anio FROM vehiculos ORDER BY matricula";
        List<Vehiculo> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapVehiculo(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar vehículos", e);
        }
    }

    @Override
    public Optional<Vehiculo> buscarPorId(String matricula) {
        if (matricula == null) {
            return Optional.empty();
        }

        String sql = "SELECT matricula, marca, modelo, anio FROM vehiculos WHERE matricula = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapVehiculo(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar vehículo", e);
        }
    }

    @Override
    public int insertar(Vehiculo entidad) {
        String sql = "INSERT INTO vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entidad.getMatricula());
            ps.setString(2, entidad.getMarca());
            ps.setString(3, entidad.getModelo());
            ps.setInt(4, entidad.getAnio());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al insertar vehículo", e);
        }
    }

    @Override
    public int actualizar(Vehiculo entidad) {
        if (entidad == null || entidad.getMatricula() == null) {
            throw new IllegalArgumentException("El vehículo debe tener matrícula");
        }

        String sql = "UPDATE vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entidad.getMarca());
            ps.setString(2, entidad.getModelo());
            ps.setInt(3, entidad.getAnio());
            ps.setString(4, entidad.getMatricula());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar vehículo", e);
        }
    }

    @Override
    public int eliminar(String matricula) {
        if (matricula == null) {
            throw new NullPointerException("La matrícula no puede ser nula");
        }

        String sql = "DELETE FROM vehiculos WHERE matricula = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, matricula);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar vehículo", e);
        }
    }
}
```

## 3. Filtro por clave foránea

Ejemplo: mascotas de un cliente.

```java
@Override
public List<Mascota> buscarPorCliente(Integer idCliente) {
    String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente "
               + "FROM mascotas WHERE id_cliente = ? ORDER BY id";

    List<Mascota> lista = new ArrayList<>();

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapMascota(rs));
            }
        }

        return lista;

    } catch (SQLException e) {
        throw new DaoException("Error al buscar mascotas por cliente", e);
    }
}
```

## 4. Filtro por texto exacto

Ejemplo: veterinarios por especialidad.

```java
@Override
public List<Veterinario> buscarPorEspecialidad(String especialidad) {
    String sql = "SELECT id, nombre, especialidad, tarifa_hora "
               + "FROM veterinarios WHERE especialidad = ? ORDER BY tarifa_hora, id";

    List<Veterinario> lista = new ArrayList<>();

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, especialidad);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapVeterinario(rs));
            }
        }

        return lista;

    } catch (SQLException e) {
        throw new DaoException("Error al buscar veterinarios por especialidad", e);
    }
}
```

## 5. Filtro por estado

Ejemplo: consultas programadas.

```java
@Override
public List<Consulta> listarProgramadas() {
    String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado "
               + "FROM consultas WHERE estado = 'PROGRAMADA' ORDER BY id";

    List<Consulta> lista = new ArrayList<>();

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(mapConsulta(rs));
        }

        return lista;

    } catch (SQLException e) {
        throw new DaoException("Error al listar consultas programadas", e);
    }
}
```

## 6. Cálculo con SUM y COALESCE

Ejemplo: coste total de tratamientos de una consulta.

```java
@Override
public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
    if (idConsulta == null) {
        return BigDecimal.ZERO;
    }

    String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) AS total "
               + "FROM tratamientos WHERE id_consulta = ?";

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idConsulta);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getBigDecimal("total") != null) {
                return rs.getBigDecimal("total");
            }
        }

        return BigDecimal.ZERO;

    } catch (SQLException e) {
        throw new DaoException("Error al calcular el coste", e);
    }
}
```

## 7. Contar registros

```java
@Override
public int contar() {
    String sql = "SELECT COUNT(*) AS total FROM clientes";

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        return rs.next() ? rs.getInt("total") : 0;

    } catch (SQLException e) {
        throw new DaoException("Error al contar clientes", e);
    }
}
```

## 8. Mapa de resultados agrupados

Ejemplo: número de mascotas por cliente.

```java
@Override
public Map<Integer, Integer> contarMascotasPorCliente() {
    String sql = "SELECT id_cliente, COUNT(*) AS total "
               + "FROM mascotas GROUP BY id_cliente";

    Map<Integer, Integer> mapa = new HashMap<>();

    try (Connection cn = ConexionBD.abrir(url);
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            mapa.put(rs.getInt("id_cliente"), rs.getInt("total"));
        }

        return mapa;

    } catch (SQLException e) {
        throw new DaoException("Error al contar mascotas por cliente", e);
    }
}
```

## 9. AbstractDao<T, ID>

```java
public abstract class AbstractDao<T, ID> {

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> consultarLista(PreparedStatement ps) throws SQLException {
        List<T> lista = new ArrayList<>();

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }

        return lista;
    }

    protected Optional<T> consultarUno(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }

        return Optional.empty();
    }

    protected int ejecutarActualizacion(PreparedStatement ps) throws SQLException {
        return ps.executeUpdate();
    }
}
```

### DAO que hereda

```java
public class ClienteDao extends AbstractDao<Cliente, Integer>
        implements IClienteDao {

    private final String url;

    public ClienteDao(String url) {
        this.url = url;
    }

    @Override
    protected Cliente mapRow(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("telefono")
        );
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT id, nombre, email, telefono FROM clientes ORDER BY id";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultarLista(ps);

        } catch (SQLException e) {
            throw new DaoException("Error al listar clientes", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, email, telefono FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return consultarUno(ps);

        } catch (SQLException e) {
            throw new DaoException("Error al buscar cliente", e);
        }
    }
}
```

## 10. ConexionBD mínima

```java
public class ConexionBD {

    private final String url;

    protected ConexionBD(String url) {
        this.url = url;
    }

    public static Connection abrir(String url) throws SQLException {
        return new ConexionBD(url).obtenerConexion();
    }

    public Connection obtenerConexion() throws SQLException {
        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("URL SQLite no válida");
        }

        String ruta = url.substring("jdbc:sqlite:".length());

        if (!ruta.equals(":memory:")
                && !ruta.startsWith("file:")
                && !Files.isRegularFile(Path.of(ruta))) {
            throw new IllegalArgumentException("La base de datos no existe: " + ruta);
        }

        Connection cn = DriverManager.getConnection(url);

        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            cn.close();
            throw e;
        }

        return cn;
    }
}
```

## 11. Recordatorio rápido

```text
ID autogenerado:
INSERT -> RETURN_GENERATED_KEYS -> getGeneratedKeys() -> return id

ID natural:
INSERT -> executeUpdate() -> return filas afectadas

Filtro:
SELECT ... WHERE campo = ? -> ps.setXxx(1, valor)

Cálculo:
SELECT COALESCE(SUM(...), 0)

Errores:
catch SQLException -> throw new DaoException("...", e)

Recursos:
try (Connection; PreparedStatement; ResultSet)
```

# Apuntes examen: SQL, JOIN, errores y chuleta

## 1. SQL esencial para los DAO

### SELECT

```sql
SELECT columna1, columna2
FROM tabla
WHERE condicion
ORDER BY columna ASC|DESC
LIMIT numero;
```

Ejemplo:

```sql
SELECT id, nombre, email
FROM clientes
WHERE email LIKE '%@ejemplo.es%'
ORDER BY nombre ASC
LIMIT 10;
```

### INSERT

```sql
INSERT INTO tabla (columna1, columna2)
VALUES (?, ?);
```

Ejemplo:

```sql
INSERT INTO clientes (nombre, email, telefono)
VALUES (?, ?, ?);
```

### UPDATE

```sql
UPDATE tabla
SET columna1 = ?, columna2 = ?
WHERE id = ?;
```

Ejemplo:

```sql
UPDATE clientes
SET nombre = ?, email = ?, telefono = ?
WHERE id = ?;
```

### DELETE

```sql
DELETE FROM tabla
WHERE id = ?;
```

Ejemplo:

```sql
DELETE FROM clientes
WHERE id = ?;
```

### Funciones de agregación

```sql
SELECT COUNT(*) FROM clientes;

SELECT SUM(cantidad * precio_unitario) FROM tratamientos;

SELECT AVG(precio) FROM productos;

SELECT MIN(precio), MAX(precio) FROM productos;
```

### GROUP BY y HAVING

```sql
SELECT id_cliente, COUNT(*) AS total
FROM mascotas
GROUP BY id_cliente
HAVING COUNT(*) >= 2
ORDER BY total DESC;
```

- `WHERE` filtra **filas antes** de agrupar.
- `HAVING` filtra **grupos después** de agrupar.

### COALESCE

Devuelve el primer valor no nulo.

```sql
SELECT COALESCE(SUM(cantidad * precio_unitario), 0) AS total
FROM tratamientos
WHERE id_consulta = ?;
```

Si no hay tratamientos, devuelve `0` en lugar de `NULL`.

## 2. JOIN y relaciones

### INNER JOIN

Devuelve solo las filas que tienen coincidencia en ambas tablas.

```sql
SELECT m.nombre AS mascota, c.nombre AS cliente
FROM mascotas m
INNER JOIN clientes c
    ON m.id_cliente = c.id;
```

### LEFT JOIN

Devuelve **todas** las filas de la tabla izquierda, aunque no tengan coincidencia en la derecha.

```sql
SELECT c.nombre, COUNT(m.id) AS total_mascotas
FROM clientes c
LEFT JOIN mascotas m
    ON m.id_cliente = c.id
GROUP BY c.id, c.nombre;
```

Incluye clientes sin mascotas; su `COUNT(m.id)` será `0`.

### RIGHT JOIN

Devuelve todas las filas de la tabla derecha, aunque no tengan coincidencia en la izquierda.

```sql
SELECT c.nombre, m.nombre AS mascota
FROM mascotas m
RIGHT JOIN clientes c
    ON m.id_cliente = c.id;
```

En SQLite se puede evitar normalmente usando `LEFT JOIN` e invirtiendo el orden de las tablas.

### JOIN de tres tablas

Ejemplo: consulta, mascota y veterinario.

```sql
SELECT
    co.id AS consulta,
    m.nombre AS mascota,
    v.nombre AS veterinario,
    co.fecha,
    co.estado
FROM consultas co
INNER JOIN mascotas m
    ON co.id_mascota = m.id
INNER JOIN veterinarios v
    ON co.id_veterinario = v.id
ORDER BY co.fecha;
```

### Claves foráneas

Una clave foránea relaciona una tabla hija con una tabla padre.

```sql
id_cliente INTEGER NOT NULL
REFERENCES clientes(id)
```

- Permite insertar solo valores que existan en la tabla referenciada, si las claves foráneas están activadas.
- `ON DELETE RESTRICT` impide borrar un padre si tiene hijos.
- `ON DELETE CASCADE` borra también los hijos.
- La clave foránea **no sustituye** al `JOIN`: para mostrar datos relacionados hay que relacionarlas en la consulta con `ON`.

## 3. Ejercicios tipo de SQL

### Listar todo ordenado

```sql
SELECT id, nombre, email
FROM clientes
ORDER BY nombre;
```

### Filtrar por valor exacto

```sql
SELECT id, nombre, especie
FROM mascotas
WHERE especie = 'Perro';
```

### Filtrar por rango

```sql
SELECT id, nombre, precio
FROM productos
WHERE precio BETWEEN 10 AND 50;
```

### Buscar texto parcial

```sql
SELECT id, nombre
FROM productos
WHERE nombre LIKE '%teclado%';
```

### Contar registros

```sql
SELECT COUNT(*) AS total
FROM mascotas;
```

### Contar por grupo

```sql
SELECT especie, COUNT(*) AS total
FROM mascotas
GROUP BY especie;
```

### Total por relación

```sql
SELECT c.nombre, COUNT(m.id) AS mascotas
FROM clientes c
LEFT JOIN mascotas m
    ON m.id_cliente = c.id
GROUP BY c.id, c.nombre;
```

### Máximo y mínimo

```sql
SELECT MIN(precio) AS mas_barato,
       MAX(precio) AS mas_caro
FROM productos;
```

### Media

```sql
SELECT AVG(precio) AS precio_medio
FROM productos;
```

### Total con registros vacíos

```sql
SELECT c.id, c.nombre,
       COALESCE(SUM(t.cantidad * t.precio_unitario), 0) AS total
FROM consultas co
INNER JOIN clientes c
    ON co.id_mascota = m.id
LEFT JOIN tratamientos t
    ON t.id_consulta = co.id
GROUP BY c.id, c.nombre;
```

### Registros sin relación

Consultas sin tratamientos:

```sql
SELECT co.id
FROM consultas co
LEFT JOIN tratamientos t
    ON t.id_consulta = co.id
WHERE t.id IS NULL;
```

## 4. Excepciones y manejo de errores

### Excepciones habituales

| Excepción | Cuándo usarla |
|---|---|
| `IllegalArgumentException` | Argumento inválido: `id` nulo en `actualizar`, valor imposible, estado no permitido |
| `NullPointerException` | Se exige obligatoriamente un valor y llega `null`, por ejemplo en `eliminar(null)` |
| `SQLException` | Error al conectar o ejecutar SQL |
| `DaoException` | Error de acceso a datos convertido desde `SQLException` |
| `IllegalStateException` | El objeto o sistema está en un estado no válido para la operación |

### DaoException

```java
public class DaoException extends RuntimeException {

    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
```

### Uso correcto

```java
try (Connection cn = ConexionBD.abrir(url);
     PreparedStatement ps = cn.prepareStatement(sql)) {

    // ejecutar consulta

} catch (SQLException e) {
    throw new DaoException("Error al listar clientes", e);
}
```

### Reglas

- Captura `SQLException`, no `Exception` de forma indiscriminada.
- Conserva la causa: `new DaoException("...", e)`.
- No uses solo `e.printStackTrace()`.
- No devuelvas resultados falsos ni listas vacías para ocultar un error grave.
- Valida entradas antes de ejecutar SQL.
- Usa `Optional.empty()` cuando “no encontrado” es un resultado normal.

### Ejemplos

```java
if (id == null) {
    return Optional.empty(); // buscarPorId
}
```

```java
if (id == null) {
    throw new NullPointerException("El id no puede ser nulo"); // eliminar
}
```

```java
if (entidad == null || entidad.getId() == null) {
    throw new IllegalArgumentException("La entidad debe tener id"); // actualizar
}
```

## 5. Chuleta final de una página

### ConexionBD

```java
public static Connection abrir(String url) throws SQLException {
    return new ConexionBD(url).obtenerConexion();
}
```

```text
1. Validar jdbc:sqlite:
2. Comprobar archivo
3. DriverManager.getConnection(url)
4. PRAGMA foreign_keys = ON
5. Devolver Connection
```

### DAO: contratos

```text
listar()              -> List<T>
buscarPorId(id)       -> Optional<T>
insertar(entidad)     -> ID generado
actualizar(entidad)   -> filas afectadas
eliminar(id)          -> filas afectadas
```

### Plantilla JDBC

```java
String sql = "SELECT ... FROM tabla WHERE campo = ?";

try (Connection cn = ConexionBD.abrir(url);
     PreparedStatement ps = cn.prepareStatement(sql)) {

    ps.setInt(1, valor);

    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            lista.add(mapRow(rs));
        }
    }

} catch (SQLException e) {
    throw new DaoException("Error...", e);
}
```

### Mapeo

```java
private Entidad mapRow(ResultSet rs) throws SQLException {
    return new Entidad(
            rs.getInt("id"),
            rs.getString("nombre")
    );
}
```

### INSERT autogenerado

```java
PreparedStatement ps = cn.prepareStatement(
        sql,
        Statement.RETURN_GENERATED_KEYS
);

ps.executeUpdate();

try (ResultSet rs = ps.getGeneratedKeys()) {
    if (rs.next()) {
        int id = rs.getInt(1);
        entidad.setId(id);
        return id;
    }
}
```

### INSERT con ID natural

```java
PreparedStatement ps = cn.prepareStatement(sql);
// ps.setXxx(...)
return ps.executeUpdate();
```

### SQL rápido

```sql
-- Listar
SELECT * FROM tabla ORDER BY id;

-- Buscar
SELECT * FROM tabla WHERE id = ?;

-- Insertar
INSERT INTO tabla (a, b) VALUES (?, ?);

-- Actualizar
UPDATE tabla SET a = ?, b = ? WHERE id = ?;

-- Borrar
DELETE FROM tabla WHERE id = ?;

-- Contar
SELECT COUNT(*) FROM tabla;

-- Sumar evitando NULL
SELECT COALESCE(SUM(a * b), 0) FROM tabla;

-- Relacionar
SELECT *
FROM hija h
INNER JOIN padre p ON h.id_padre = p.id;

-- Incluir padres sin hijos
SELECT p.id, COUNT(h.id)
FROM padre p
LEFT JOIN hija h ON h.id_padre = p.id
GROUP BY p.id;
```

### AbstractDao

```java
public abstract class AbstractDao<T, ID> {

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> consultarLista(PreparedStatement ps) throws SQLException {
        List<T> lista = new ArrayList<>();

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }

        return lista;
    }

    protected Optional<T> consultarUno(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }

        return Optional.empty();
    }

    protected int ejecutarActualizacion(PreparedStatement ps) throws SQLException {
        return ps.executeUpdate();
    }
}
```

### Herencia

```java
public class ClienteDao
        extends AbstractDao<Cliente, Integer>
        implements IClienteDao {
}
```

### Reglas de oro

```text
- PreparedStatement siempre para datos externos.
- try-with-resources siempre.
- ResultSet -> objeto con mapRow.
- SQLException -> DaoException(mensaje, causa).
- buscarPorId(null) -> Optional.empty().
- ID autogenerado -> getGeneratedKeys().
- ID natural -> executeUpdate().
- SUM -> COALESCE(SUM(...), 0).
- BigDecimal -> compareTo(), no equals().
- JOIN relaciona; FOREIGN KEY restringe.
```
