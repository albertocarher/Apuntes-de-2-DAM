package es.ejemplo.taller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import es.ejemplo.taller.modelo.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class ReparacionDaoTest extends BaseTest {
    @Test @DisplayName("Insertar reparación devuelve ID positivo") void caso01() throws Exception { assertTrue(reparacion()>0); }
    @Test @DisplayName("Buscar ID insertado devuelve Optional presente") void caso02() throws Exception { int id=reparacion(); assertTrue(reparaciones.buscarPorId(id).isPresent()); }
    @Test @DisplayName("Buscar 999 devuelve Optional.empty") void caso03() throws Exception { assertTrue(reparaciones.buscarPorId(999).isEmpty()); }
    @Test @DisplayName("Listar tras insertar devuelve tamaño 1") void caso04() throws Exception { reparacion(); assertEquals(1,reparaciones.listar().size()); }
    @Test @DisplayName("Buscar por 1234ABC devuelve tamaño 1") void caso05() throws Exception { reparacion(); assertEquals(1,reparaciones.buscarPorVehiculo("1234ABC").size()); }
    @Test @DisplayName("Buscar por mecánico obtenido devuelve tamaño 1") void caso06() throws Exception { int id=reparacion(); int mec=reparaciones.buscarPorId(id).orElseThrow().getIdMecanico(); assertEquals(1,reparaciones.buscarPorMecanico(mec).size()); }
    @Test @DisplayName("Listar pendientes devuelve tamaño 1") void caso07() throws Exception { reparacion(); assertEquals(1,reparaciones.listarPendientes().size()); }
    @Test @DisplayName("Actualizar reparación existente devuelve 1") void caso08() throws Exception { int id=reparacion(); int mec=reparaciones.buscarPorId(id).orElseThrow().getIdMecanico(); assertEquals(1,reparaciones.actualizar(new Reparacion(id,"1234ABC",mec,"Ok","2026-01-11",new BigDecimal("3"),"FINALIZADA"))); }
    @Test @DisplayName("Eliminar reparación existente devuelve 1") void caso09() throws Exception { int id=reparacion(); assertEquals(1,reparaciones.eliminar(id)); }
    @Test @DisplayName("Insertar estado INVALIDA devuelve 0") void caso10() throws Exception { String m=vehiculo(); int mec=mecanico(); assertEquals(0,reparaciones.insertar(new Reparacion(null,m,mec,"Error","2026-01-10",new BigDecimal("2"),"INVALIDA"))); }
}
