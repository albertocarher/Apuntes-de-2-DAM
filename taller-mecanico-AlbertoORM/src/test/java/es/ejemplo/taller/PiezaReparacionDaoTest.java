package es.ejemplo.taller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import es.ejemplo.taller.modelo.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class PiezaReparacionDaoTest extends BaseTest {
    @Test @DisplayName("Insertar pieza devuelve ID positivo") void caso01() throws Exception { assertTrue(pieza()>0); }
    @Test @DisplayName("Buscar ID insertado devuelve Optional presente") void caso02() throws Exception { int id=pieza(); assertTrue(piezas.buscarPorId(id).isPresent()); }
    @Test @DisplayName("Buscar 999 devuelve Optional.empty") void caso03() throws Exception { assertTrue(piezas.buscarPorId(999).isEmpty()); }
    @Test @DisplayName("Listar tras insertar devuelve tamaño 1") void caso04() throws Exception { pieza(); assertEquals(1,piezas.listar().size()); }
    @Test @DisplayName("Filtrar por reparación obtenida devuelve tamaño 1") void caso05() throws Exception { int id=pieza(); int rep=piezas.buscarPorId(id).orElseThrow().getIdReparacion(); assertEquals(1,piezas.listarPorReparacion(rep).size()); }
    @Test @DisplayName("Coste de dos filtros a 10.00 devuelve 20.00") void caso06() throws Exception { int id=pieza(); int rep=piezas.buscarPorId(id).orElseThrow().getIdReparacion(); assertEquals(0,piezas.calcularCostePiezas(rep).compareTo(new BigDecimal("20"))); }
    @Test @DisplayName("Actualizar pieza existente devuelve 1") void caso07() throws Exception { int id=pieza(); int rep=piezas.buscarPorId(id).orElseThrow().getIdReparacion(); assertEquals(1,piezas.actualizar(new PiezaReparacion(id,rep,"Filtro",3,new BigDecimal("15")))); }
    @Test @DisplayName("Eliminar pieza existente devuelve 1") void caso08() throws Exception { int id=pieza(); assertEquals(1,piezas.eliminar(id)); }
    @Test @DisplayName("Insertar cantidad 0 devuelve 0") void caso09() throws Exception { int rep=reparacion(); assertEquals(0,piezas.insertar(new PiezaReparacion(null,rep,"F",0,new BigDecimal("10")))); }
    @Test @DisplayName("Insertar null devuelve 0") void caso10() throws Exception { assertEquals(0,piezas.insertar(null)); }
}
