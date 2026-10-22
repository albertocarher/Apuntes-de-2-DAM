package es.ejemplo.taller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import es.ejemplo.taller.modelo.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class VehiculoDaoTest extends BaseTest {
    @Test @DisplayName("Insertar 1234ABC permite recuperarlo y conserva la matrícula") void caso01() throws Exception { String m=vehiculo(); assertEquals("1234ABC",m); assertTrue(vehiculos.buscarPorId(m).isPresent()); }
    @Test @DisplayName("Buscar matrícula insertada devuelve Optional presente") void caso02() throws Exception { String m=vehiculo(); assertTrue(vehiculos.buscarPorId(m).isPresent()); }
    @Test @DisplayName("Buscar NO devuelve Optional.empty") void caso03() throws Exception { assertTrue(vehiculos.buscarPorId("NO").isEmpty()); }
    @Test @DisplayName("Listar tras insertar devuelve tamaño 1") void caso04() throws Exception { vehiculo(); assertEquals(1,vehiculos.listar().size()); }
    @Test @DisplayName("Buscar por propietario obtenido devuelve tamaño 1") void caso05() throws Exception { String m=vehiculo(); int id=vehiculos.buscarPorId(m).orElseThrow().getIdCliente(); assertEquals(1,vehiculos.buscarPorCliente(id).size()); }
    @Test @DisplayName("Buscar propietario 99 devuelve tamaño 0") void caso06() throws Exception { vehiculo(); assertEquals(0,vehiculos.buscarPorCliente(99).size()); }
    @Test @DisplayName("Filtrar Seat devuelve tamaño 1") void caso07() throws Exception { vehiculo(); assertEquals(1,vehiculos.listarPorMarca("Seat").size()); }
    @Test @DisplayName("Actualizar vehículo existente devuelve 1") void caso08() throws Exception { String m=vehiculo(); int id=vehiculos.buscarPorId(m).orElseThrow().getIdCliente(); assertEquals(1,vehiculos.actualizar(new Vehiculo(m,"Ford","Focus",2020,id))); }
    @Test @DisplayName("Eliminar vehículo existente devuelve 1") void caso09() throws Exception { String m=vehiculo(); assertEquals(1,vehiculos.eliminar(m)); }
    @Test @DisplayName("Insertar matrícula duplicada devuelve 0") void caso10() throws Exception { String m=vehiculo(); int id=vehiculos.buscarPorId(m).orElseThrow().getIdCliente(); assertEquals(0,vehiculos.insertar(new Vehiculo(m,"Seat","Ibiza",2018,id))); }
}
