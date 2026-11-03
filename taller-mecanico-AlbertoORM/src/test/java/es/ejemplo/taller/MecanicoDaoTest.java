package es.ejemplo.taller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import es.ejemplo.taller.modelo.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class MecanicoDaoTest extends BaseTest {
    @Test @DisplayName("Insertar Luis devuelve un ID positivo") void caso01() throws Exception { assertTrue(mecanico()>0); }
    @Test @DisplayName("Buscar el ID insertado devuelve un Optional presente") void caso02() throws Exception { int id=mecanico(); assertTrue(mecanicos.buscarPorId(id).isPresent()); }
    @Test @DisplayName("Buscar 999 devuelve Optional.empty") void caso03() throws Exception { assertTrue(mecanicos.buscarPorId(999).isEmpty()); }
    @Test @DisplayName("Listar tras una inserción devuelve tamaño 1") void caso04() throws Exception { mecanico(); assertEquals(1,mecanicos.listar().size()); }
    @Test @DisplayName("Filtrar Motor devuelve tamaño 1") void caso05() throws Exception { mecanico(); assertEquals(1,mecanicos.buscarPorEspecialidad("Motor").size()); }
    @Test @DisplayName("Filtrar Chapa sin coincidencias devuelve tamaño 0") void caso06() throws Exception { mecanico(); assertEquals(0,mecanicos.buscarPorEspecialidad("Chapa").size()); }
    @Test @DisplayName("Actualizar especialidad y tarifa devuelve 1") void caso07() throws Exception { int id=mecanico(); assertEquals(1,mecanicos.actualizar(new Mecanico(id,"Luis","Chapa",new BigDecimal("50")))); }
    @Test @DisplayName("Eliminar el mecánico devuelve 1") void caso08() throws Exception { int id=mecanico(); assertEquals(1,mecanicos.eliminar(id)); }
    @Test @DisplayName("Insertar tarifa -1 devuelve 0") void caso09() throws Exception { assertEquals(0,mecanicos.insertar(new Mecanico(null,"Eva","Chapa",new BigDecimal("-1")))); }
    @Test @DisplayName("Actualizar null devuelve 0") void caso10() throws Exception { assertEquals(0,mecanicos.actualizar(null)); }
}
