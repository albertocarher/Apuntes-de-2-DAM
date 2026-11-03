package es.ejemplo.taller;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

/** 40 casos adicionales: el contrato completo aparece en TESTS.md. */
public class AmpliacionTest extends BaseTest {
    @TempDir Path temporal;
    private static BigDecimal bd(String n) { return new BigDecimal(n); }
    private static void dinero(String esperado, BigDecimal real) {
        assertNotNull(real); assertEquals(0,bd(esperado).compareTo(real));
    }
    private int otroCliente(String correo) {
        int id=clientes.insertar(new Cliente(null,"Otro",correo,null)); assertTrue(id>0); return id;
    }
    private void veh(String matricula,String marca,int cliente) {
        assertEquals(1,vehiculos.insertar(new Vehiculo(matricula,marca,"Modelo",2020,cliente)));
    }
    private int nuevaReparacion(String matricula,int mecanico,String estado) {
        int id=reparaciones.insertar(new Reparacion(null,matricula,mecanico,"Revision","2026-01-10",bd("2"),estado));
        assertTrue(id>0);return id;
    }
    private int nuevaPieza(int reparacion,String nombre,int cantidad,String precio) {
        int id=piezas.insertar(new PiezaReparacion(null,reparacion,nombre,cantidad,bd(precio)));
        assertTrue(id>0);return id;
    }

    /** Preparación: Base de ejemplo entregada. Operación: Abrir con ConexionBD. Esperado: Conexión abierta y cinco tablas presentes. */
    @Test @DisplayName("archivoTrabajoValido: Conexión abierta y cinco tablas presentes")
    void archivoTrabajoValido() throws Exception {
        try (var c=ConexionBD.abrir("jdbc:sqlite:datos/taller.sqlite3")) { assertFalse(c.isClosed()); }
    }

    /** Preparación: Plantilla original. Operación: Abrir y contar las cinco tablas. Esperado: Conexión abierta; 0 filas en cada tabla. */
    @Test @DisplayName("plantillaVaciaValida: Conexión abierta; 0 filas en cada tabla")
    void plantillaVaciaValida() throws Exception {
        try (var c=ConexionBD.abrir("jdbc:sqlite:src/test/resources/taller-test.sqlite3"); var st=c.createStatement()) { for(String t:List.of("clientes","vehiculos","mecanicos","reparaciones","piezas_reparacion")) try(var rs=st.executeQuery("SELECT COUNT(*) FROM "+t)){assertTrue(rs.next());assertEquals(0,rs.getInt(1));} }
    }

    /** Preparación: Ruta sin archivo dentro de directorio temporal. Operación: Intentar abrir. Esperado: SQLException y el archivo sigue sin existir. */
    @Test @DisplayName("rutaInexistenteNoCreaArchivo: SQLException y el archivo sigue sin existir")
    void rutaInexistenteNoCreaArchivo() throws Exception {
        Path p=temporal.resolve("ausente.sqlite3"); assertThrows(SQLException.class,()->ConexionBD.abrir("jdbc:sqlite:"+p)); assertFalse(Files.exists(p));
    }

    /** Preparación: Directorio temporal. Operación: Intentar abrir como BBDD. Esperado: SQLException; sigue siendo directorio. */
    @Test @DisplayName("directorioNoEsBase: SQLException; sigue siendo directorio")
    void directorioNoEsBase() throws Exception {
        assertThrows(SQLException.class,()->ConexionBD.abrir("jdbc:sqlite:"+temporal)); assertTrue(Files.isDirectory(temporal));
    }

    /** Preparación: Archivo con texto ordinario. Operación: Intentar abrir. Esperado: SQLException; no se acepta como BBDD. */
    @Test @DisplayName("archivoNoSqlite: SQLException; no se acepta como BBDD")
    void archivoNoSqlite() throws Exception {
        Path p=temporal.resolve("texto.db"); Files.writeString(p,"No soy una base SQLite"); assertThrows(SQLException.class,()->ConexionBD.abrir("jdbc:sqlite:"+p));
    }

    /** Preparación: BBDD SQLite con una tabla ajena. Operación: Intentar abrir. Esperado: SQLException por esquema incompleto. */
    @Test @DisplayName("baseSinTablas: SQLException por esquema incompleto")
    void baseSinTablas() throws Exception {
        Path p=temporal.resolve("otra.db"); try(var c=DriverManager.getConnection("jdbc:sqlite:"+p);var st=c.createStatement()){st.execute("CREATE TABLE otra(id INTEGER)");} assertThrows(SQLException.class,()->ConexionBD.abrir("jdbc:sqlite:"+p));
    }

    /** Preparación: Plantilla vacía. Operación: listar. Esperado: Lista no nula de tamaño 0. */
    @Test @DisplayName("listarClientesVacios: Lista no nula de tamaño 0")
    void listarClientesVacios() throws Exception {
        assertNotNull(clientes.listar()); assertTrue(clientes.listar().isEmpty());
    }

    /** Preparación: Plantilla vacía. Operación: listar. Esperado: Lista no nula de tamaño 0. */
    @Test @DisplayName("listarMecanicosVacios: Lista no nula de tamaño 0")
    void listarMecanicosVacios() throws Exception {
        assertNotNull(mecanicos.listar()); assertTrue(mecanicos.listar().isEmpty());
    }

    /** Preparación: Plantilla vacía. Operación: listar. Esperado: Lista no nula de tamaño 0. */
    @Test @DisplayName("listarVehiculosVacios: Lista no nula de tamaño 0")
    void listarVehiculosVacios() throws Exception {
        assertNotNull(vehiculos.listar()); assertTrue(vehiculos.listar().isEmpty());
    }

    /** Preparación: Plantilla vacía. Operación: listar. Esperado: Lista no nula de tamaño 0. */
    @Test @DisplayName("listarReparacionesVacios: Lista no nula de tamaño 0")
    void listarReparacionesVacios() throws Exception {
        assertNotNull(reparaciones.listar()); assertTrue(reparaciones.listar().isEmpty());
    }

    /** Preparación: Plantilla vacía. Operación: listar. Esperado: Lista no nula de tamaño 0. */
    @Test @DisplayName("listarPiezasVacios: Lista no nula de tamaño 0")
    void listarPiezasVacios() throws Exception {
        assertNotNull(piezas.listar()); assertTrue(piezas.listar().isEmpty());
    }

    /** Preparación: Cliente con nombre de espacios. Operación: insertar. Esperado: 0; no se guarda cliente. */
    @Test @DisplayName("nombreClienteBlanco: 0; no se guarda cliente")
    void nombreClienteBlanco() throws Exception {
        assertEquals(0,clientes.insertar(new Cliente(null,"   ","x@ejemplo.com",null))); assertTrue(clientes.listar().isEmpty());
    }

    /** Preparación: Mecánico con especialidad en blanco. Operación: insertar. Esperado: 0; no se guarda mecánico. */
    @Test @DisplayName("especialidadMecanicoBlanca: 0; no se guarda mecánico")
    void especialidadMecanicoBlanca() throws Exception {
        assertEquals(0,mecanicos.insertar(new Mecanico(null,"Luis"," ",BigDecimal.ONE))); assertTrue(mecanicos.listar().isEmpty());
    }

    /** Preparación: Cliente existente y matrícula vacía. Operación: insertar vehículo. Esperado: 0; no se guarda vehículo. */
    @Test @DisplayName("matriculaVacia: 0; no se guarda vehículo")
    void matriculaVacia() throws Exception {
        int c=cliente(); assertEquals(0,vehiculos.insertar(new Vehiculo("","Seat","Ibiza",2018,c))); assertTrue(vehiculos.listar().isEmpty());
    }

    /** Preparación: Base vacía. Operación: insertar null. Esperado: 0; no se guarda reparación. */
    @Test @DisplayName("reparacionNula: 0; no se guarda reparación")
    void reparacionNula() throws Exception {
        assertEquals(0,reparaciones.insertar(null)); assertTrue(reparaciones.listar().isEmpty());
    }

    /** Preparación: Un vehículo existente. Operación: listarPorMarca(null). Esperado: Lista no nula de tamaño 0. */
    @Test @DisplayName("marcaNula: Lista no nula de tamaño 0")
    void marcaNula() throws Exception {
        vehiculo(); assertNotNull(vehiculos.listarPorMarca(null)); assertTrue(vehiculos.listarPorMarca(null).isEmpty());
    }

    /** Preparación: Cliente Ana existente. Operación: Modificar nombre, email y teléfono; recuperar. Esperado: 1; Eva, eva@ejemplo.com y 777. */
    @Test @DisplayName("actualizarClientePersiste: 1; Eva, eva@ejemplo.com y 777")
    void actualizarClientePersiste() throws Exception {
        int id=cliente(); assertEquals(1,clientes.actualizar(new Cliente(id,"Eva","eva@ejemplo.com","777"))); var d=clientes.buscarPorId(id).orElseThrow(); assertEquals(id,d.getIdCliente()); assertEquals("Eva",d.getNombre()); assertEquals("eva@ejemplo.com",d.getEmail()); assertEquals("777",d.getTelefono());
    }

    /** Preparación: Mecánico existente. Operación: Modificar nombre, especialidad y tarifa; recuperar. Esperado: 1; Eva, Chapa y 50.25. */
    @Test @DisplayName("actualizarMecanicoPersiste: 1; Eva, Chapa y 50.25")
    void actualizarMecanicoPersiste() throws Exception {
        int id=mecanico(); assertEquals(1,mecanicos.actualizar(new Mecanico(id,"Eva","Chapa",bd("50.25")))); var d=mecanicos.buscarPorId(id).orElseThrow(); assertEquals(id,d.getIdMecanico()); assertEquals("Eva",d.getNombre()); assertEquals("Chapa",d.getEspecialidad()); dinero("50.25",d.getTarifaHora());
    }

    /** Preparación: Vehículo y un segundo propietario. Operación: Modificar marca, modelo, año y propietario; recuperar. Esperado: 1; Ford, Focus, 2020 y el nuevo propietario. */
    @Test @DisplayName("actualizarVehiculoPersiste: 1; Ford, Focus, 2020 y el nuevo propietario")
    void actualizarVehiculoPersiste() throws Exception {
        String m=vehiculo(); int c=otroCliente("otro@ejemplo.com"); assertEquals(1,vehiculos.actualizar(new Vehiculo(m,"Ford","Focus",2020,c))); var d=vehiculos.buscarPorId(m).orElseThrow(); assertEquals(m,d.getMatricula()); assertEquals("Ford",d.getMarca()); assertEquals("Focus",d.getModelo()); assertEquals(2020,d.getAnio()); assertEquals(c,d.getIdCliente());
    }

    /** Preparación: Reparación; segundo vehículo y mecánico. Operación: Modificar todos los campos no clave; recuperar. Esperado: 1; nuevos vínculos, descripción, fecha, horas y estado. */
    @Test @DisplayName("actualizarReparacionPersiste: 1; nuevos vínculos, descripción, fecha, horas y estado")
    void actualizarReparacionPersiste() throws Exception {
        int id=reparacion(); int c=otroCliente("otro@ejemplo.com"); assertEquals(1,vehiculos.insertar(new Vehiculo("OTRO","Ford","Fiesta",2020,c))); int m=mecanicos.insertar(new Mecanico(null,"Eva","Chapa",bd("30"))); assertTrue(m>0); assertEquals(1,reparaciones.actualizar(new Reparacion(id,"OTRO",m,"Cambio","2026-02-03",bd("3.25"),"FINALIZADA"))); var d=reparaciones.buscarPorId(id).orElseThrow(); assertEquals(id,d.getIdReparacion()); assertEquals("OTRO",d.getMatricula()); assertEquals(m,d.getIdMecanico()); assertEquals("Cambio",d.getDescripcion()); assertEquals("2026-02-03",d.getFecha()); dinero("3.25",d.getHorasTrabajo()); assertEquals("FINALIZADA",d.getEstado());
    }

    /** Preparación: Pieza y segunda reparación. Operación: Modificar reparación, nombre, cantidad y precio; recuperar. Esperado: 1; nueva reparación, Aceite, 3 y 15.25. */
    @Test @DisplayName("actualizarPiezaPersiste: 1; nueva reparación, Aceite, 3 y 15.25")
    void actualizarPiezaPersiste() throws Exception {
        int id=pieza(); var anterior=reparaciones.listar().get(0); int rep=nuevaReparacion(anterior.getMatricula(),anterior.getIdMecanico(),"PENDIENTE"); assertEquals(1,piezas.actualizar(new PiezaReparacion(id,rep,"Aceite",3,bd("15.25")))); var d=piezas.buscarPorId(id).orElseThrow(); assertEquals(id,d.getIdPieza()); assertEquals(rep,d.getIdReparacion()); assertEquals("Aceite",d.getNombre()); assertEquals(3,d.getCantidad()); dinero("15.25",d.getPrecioUnitario());
    }

    /** Preparación: Dos clientes sin dependencias. Operación: Borrar el primero y consultar ambos. Esperado: 1; primero ausente y segundo presente. */
    @Test @DisplayName("borrarClientePersiste: 1; primero ausente y segundo presente")
    void borrarClientePersiste() throws Exception {
        int a=cliente(),b=otroCliente("b@ejemplo.com"); assertEquals(1,clientes.eliminar(a)); assertTrue(clientes.buscarPorId(a).isEmpty()); assertTrue(clientes.buscarPorId(b).isPresent()); assertEquals(1,clientes.listar().size());
    }

    /** Preparación: Dos mecánicos sin reparaciones. Operación: Borrar el primero y consultar ambos. Esperado: 1; primero ausente y segundo presente. */
    @Test @DisplayName("borrarMecanicoPersiste: 1; primero ausente y segundo presente")
    void borrarMecanicoPersiste() throws Exception {
        int a=mecanico(),b=mecanicos.insertar(new Mecanico(null,"Eva","Chapa",bd("30"))); assertTrue(b>0); assertEquals(1,mecanicos.eliminar(a)); assertTrue(mecanicos.buscarPorId(a).isEmpty()); assertTrue(mecanicos.buscarPorId(b).isPresent()); assertEquals(1,mecanicos.listar().size());
    }

    /** Preparación: Dos vehículos sin reparaciones. Operación: Borrar el primero y consultar ambos. Esperado: 1; primero ausente y segundo presente. */
    @Test @DisplayName("borrarVehiculoPersiste: 1; primero ausente y segundo presente")
    void borrarVehiculoPersiste() throws Exception {
        String a=vehiculo();int c=vehiculos.buscarPorId(a).orElseThrow().getIdCliente(); assertEquals(1,vehiculos.insertar(new Vehiculo("OTRO","Ford","Focus",2020,c))); assertEquals(1,vehiculos.eliminar(a)); assertTrue(vehiculos.buscarPorId(a).isEmpty()); assertTrue(vehiculos.buscarPorId("OTRO").isPresent()); assertEquals(1,vehiculos.listar().size());
    }

    /** Preparación: Dos reparaciones sin piezas. Operación: Borrar la primera y consultar ambas. Esperado: 1; primera ausente y segunda presente. */
    @Test @DisplayName("borrarReparacionPersiste: 1; primera ausente y segunda presente")
    void borrarReparacionPersiste() throws Exception {
        int a=reparacion();var d=reparaciones.buscarPorId(a).orElseThrow();int b=nuevaReparacion(d.getMatricula(),d.getIdMecanico(),"PENDIENTE");assertEquals(1,reparaciones.eliminar(a));assertTrue(reparaciones.buscarPorId(a).isEmpty());assertTrue(reparaciones.buscarPorId(b).isPresent());assertEquals(1,reparaciones.listar().size());
    }

    /** Preparación: Dos piezas. Operación: Borrar la primera y consultar ambas. Esperado: 1; primera ausente y segunda presente. */
    @Test @DisplayName("borrarPiezaPersiste: 1; primera ausente y segunda presente")
    void borrarPiezaPersiste() throws Exception {
        int a=pieza();int rep=piezas.buscarPorId(a).orElseThrow().getIdReparacion();int b=nuevaPieza(rep,"Aceite",1,"3");assertEquals(1,piezas.eliminar(a));assertTrue(piezas.buscarPorId(a).isEmpty());assertTrue(piezas.buscarPorId(b).isPresent());assertEquals(1,piezas.listar().size());
    }

    /** Preparación: Dos Motor y un Chapa. Operación: buscarPorEspecialidad Motor. Esperado: Exactamente los dos IDs de Motor. */
    @Test @DisplayName("filtrarEspecialidadMultiples: Exactamente los dos IDs de Motor")
    void filtrarEspecialidadMultiples() throws Exception {
        int a=mecanico(),b=mecanicos.insertar(new Mecanico(null,"Eva","Motor",bd("30"))),c=mecanicos.insertar(new Mecanico(null,"Paz","Chapa",bd("20")));assertTrue(b>0&&c>0);assertEquals(Set.of(a,b),mecanicos.buscarPorEspecialidad("Motor").stream().map(Mecanico::getIdMecanico).collect(Collectors.toSet()));assertEquals(2,mecanicos.buscarPorEspecialidad("Motor").size());
    }

    /** Preparación: Dos vehículos de A y uno de B. Operación: buscarPorCliente A. Esperado: Exactamente las matrículas A1 y A2. */
    @Test @DisplayName("filtrarPropietarioMultiples: Exactamente las matrículas A1 y A2")
    void filtrarPropietarioMultiples() throws Exception {
        int a=cliente(),b=otroCliente("b@ejemplo.com");veh("A1","Seat",a);veh("A2","Ford",a);veh("B1","Seat",b);assertEquals(Set.of("A1","A2"),vehiculos.buscarPorCliente(a).stream().map(Vehiculo::getMatricula).collect(Collectors.toSet()));assertEquals(2,vehiculos.buscarPorCliente(a).size());
    }

    /** Preparación: Dos Seat y un Ford. Operación: listarPorMarca Seat. Esperado: Exactamente A1 y A2. */
    @Test @DisplayName("filtrarMarcaMultiples: Exactamente A1 y A2")
    void filtrarMarcaMultiples() throws Exception {
        int c=cliente();veh("A1","Seat",c);veh("A2","Seat",c);veh("B1","Ford",c);assertEquals(Set.of("A1","A2"),vehiculos.listarPorMarca("Seat").stream().map(Vehiculo::getMatricula).collect(Collectors.toSet()));assertEquals(2,vehiculos.listarPorMarca("Seat").size());
    }

    /** Preparación: Dos reparaciones de A y una de B. Operación: buscarPorVehiculo A. Esperado: Exactamente las dos reparaciones de A. */
    @Test @DisplayName("filtrarReparacionesVehiculoMultiples: Exactamente las dos reparaciones de A")
    void filtrarReparacionesVehiculoMultiples() throws Exception {
        int c=cliente(),m=mecanico();veh("A","Seat",c);veh("B","Ford",c);int a=nuevaReparacion("A",m,"PENDIENTE"),b=nuevaReparacion("A",m,"FINALIZADA");nuevaReparacion("B",m,"PENDIENTE");assertEquals(Set.of(a,b),reparaciones.buscarPorVehiculo("A").stream().map(Reparacion::getIdReparacion).collect(Collectors.toSet()));assertEquals(2,reparaciones.buscarPorVehiculo("A").size());
    }

    /** Preparación: Dos reparaciones de A y una de B. Operación: buscarPorMecanico A. Esperado: Exactamente las dos reparaciones de A. */
    @Test @DisplayName("filtrarReparacionesMecanicoMultiples: Exactamente las dos reparaciones de A")
    void filtrarReparacionesMecanicoMultiples() throws Exception {
        String v=vehiculo();int a=mecanico(),b=mecanico();int r1=nuevaReparacion(v,a,"PENDIENTE"),r2=nuevaReparacion(v,a,"FINALIZADA");nuevaReparacion(v,b,"PENDIENTE");assertEquals(Set.of(r1,r2),reparaciones.buscarPorMecanico(a).stream().map(Reparacion::getIdReparacion).collect(Collectors.toSet()));assertEquals(2,reparaciones.buscarPorMecanico(a).size());
    }

    /** Preparación: Dos pendientes, una en proceso y otra finalizada. Operación: listarPendientes. Esperado: Exactamente los dos IDs pendientes. */
    @Test @DisplayName("filtrarPendientesMultiples: Exactamente los dos IDs pendientes")
    void filtrarPendientesMultiples() throws Exception {
        String v=vehiculo();int m=mecanico();int a=nuevaReparacion(v,m,"PENDIENTE"),b=nuevaReparacion(v,m,"PENDIENTE");nuevaReparacion(v,m,"EN_PROCESO");nuevaReparacion(v,m,"FINALIZADA");assertEquals(Set.of(a,b),reparaciones.listarPendientes().stream().map(Reparacion::getIdReparacion).collect(Collectors.toSet()));assertEquals(2,reparaciones.listarPendientes().size());
    }

    /** Preparación: Dos piezas de A y una de B. Operación: listarPorReparacion A. Esperado: Exactamente los dos IDs de piezas de A. */
    @Test @DisplayName("filtrarPiezasMultiples: Exactamente los dos IDs de piezas de A")
    void filtrarPiezasMultiples() throws Exception {
        int a=reparacion();var d=reparaciones.buscarPorId(a).orElseThrow();int b=nuevaReparacion(d.getMatricula(),d.getIdMecanico(),"PENDIENTE");int p1=nuevaPieza(a,"Filtro",1,"2"),p2=nuevaPieza(a,"Aceite",1,"3");nuevaPieza(b,"Otro",1,"4");assertEquals(Set.of(p1,p2),piezas.listarPorReparacion(a).stream().map(PiezaReparacion::getIdPieza).collect(Collectors.toSet()));assertEquals(2,piezas.listarPorReparacion(a).size());
    }

    /** Preparación: Piezas: 2 × 10 y 3 × 5. Operación: calcularCostePiezas. Esperado: 35. */
    @Test @DisplayName("costeVariasPiezas: 35")
    void costeVariasPiezas() throws Exception {
        int r=reparacion();nuevaPieza(r,"A",2,"10");nuevaPieza(r,"B",3,"5");dinero("35",piezas.calcularCostePiezas(r));
    }

    /** Preparación: Piezas: 2 × 10.25 y 3 × 0.10. Operación: calcularCostePiezas. Esperado: 20.80, comparación numérica. */
    @Test @DisplayName("costeDecimal: 20.80, comparación numérica")
    void costeDecimal() throws Exception {
        int r=reparacion();nuevaPieza(r,"A",2,"10.25");nuevaPieza(r,"B",3,"0.10");dinero("20.80",piezas.calcularCostePiezas(r));
    }

    /** Preparación: Reparación sin piezas. Operación: calcularCostePiezas. Esperado: 0, no null. */
    @Test @DisplayName("costeSinPiezas: 0, no null")
    void costeSinPiezas() throws Exception {
        int r=reparacion();dinero("0",piezas.calcularCostePiezas(r));
    }

    /** Preparación: Reparación A con coste 6 y B con coste 100. Operación: calcularCostePiezas A. Esperado: 6; excluye los 100 de B. */
    @Test @DisplayName("costeExcluyeOtraReparacion: 6; excluye los 100 de B")
    void costeExcluyeOtraReparacion() throws Exception {
        int a=reparacion();var d=reparaciones.buscarPorId(a).orElseThrow();int b=nuevaReparacion(d.getMatricula(),d.getIdMecanico(),"PENDIENTE");nuevaPieza(a,"A",2,"3");nuevaPieza(b,"B",1,"100");dinero("6",piezas.calcularCostePiezas(a));
    }

    /** Preparación: Dos clientes con correos diferentes. Operación: Insertar y recuperar ambos IDs. Esperado: IDs positivos distintos y datos correspondientes. */
    @Test @DisplayName("idsGeneradosDistintos: IDs positivos distintos y datos correspondientes")
    void idsGeneradosDistintos() throws Exception {
        int a=cliente(),b=otroCliente("b@ejemplo.com");assertTrue(a>0&&b>0);assertNotEquals(a,b);assertEquals("ana@example.com",clientes.buscarPorId(a).orElseThrow().getEmail());assertEquals("b@ejemplo.com",clientes.buscarPorId(b).orElseThrow().getEmail());
    }

    /** Preparación: Cliente previo y segundo cliente. Operación: Asociar vehículo al segundo cliente. Esperado: Inserción 1 y propietario igual al ID obtenido, distinto de 1. */
    @Test @DisplayName("relacionConIdNoUno: Inserción 1 y propietario igual al ID obtenido, distinto de 1")
    void relacionConIdNoUno() throws Exception {
        cliente();int id=otroCliente("segundo@ejemplo.com");assertNotEquals(1,id);veh("SEGUNDO","Seat",id);assertEquals(id,vehiculos.buscarPorId("SEGUNDO").orElseThrow().getIdCliente());
    }

    /** Preparación: Dos copias de plantilla. Operación: Insertar en primera y consultar ambas. Esperado: Primera con 1 cliente; segunda con 0. */
    @Test @DisplayName("basesTemporalesIndependientes: Primera con 1 cliente; segunda con 0")
    void basesTemporalesIndependientes() throws Exception {
        Path otra=temporal.resolve("otra.sqlite3");Files.copy(archivo,otra);int id=cliente();assertTrue(id>0);var otros=new es.ejemplo.taller.dao.ClienteDao("jdbc:sqlite:"+otra);assertEquals(1,clientes.listar().size());assertTrue(otros.listar().isEmpty());
    }
}
