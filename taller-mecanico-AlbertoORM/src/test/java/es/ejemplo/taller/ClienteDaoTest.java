package es.ejemplo.taller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import es.ejemplo.taller.modelo.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class ClienteDaoTest extends BaseTest {
    @Test @DisplayName("Insertar Ana devuelve un ID positivo") void caso01() throws Exception { assertTrue(cliente()>0); }
    @Test @DisplayName("Buscar el ID insertado devuelve nombre Ana") void caso02() throws Exception { int id=cliente(); assertEquals("Ana",clientes.buscarPorId(id).orElseThrow().getNombre()); }
    @Test @DisplayName("Buscar 999 devuelve Optional.empty") void caso03() throws Exception { assertTrue(clientes.buscarPorId(999).isEmpty()); }
    @Test @DisplayName("Listar tras una inserción devuelve una lista de tamaño 1") void caso04() throws Exception { cliente(); assertEquals(1,clientes.listar().size()); }
    @Test @DisplayName("Actualizar Ana por Eva devuelve 1") void caso05() throws Exception { int id=cliente(); assertEquals(1,clientes.actualizar(new Cliente(id,"Eva","eva@example.com","666"))); }
    @Test @DisplayName("Consultar después de actualizar devuelve nombre Eva") void caso06() throws Exception { int id=cliente(); clientes.actualizar(new Cliente(id,"Eva","eva@example.com","666")); assertEquals("Eva",clientes.buscarPorId(id).orElseThrow().getNombre()); }
    @Test @DisplayName("Eliminar el ID existente devuelve 1") void caso07() throws Exception { int id=cliente(); assertEquals(1,clientes.eliminar(id)); }
    @Test @DisplayName("Eliminar 999 devuelve 0") void caso08() throws Exception { assertEquals(0,clientes.eliminar(999)); }
    @Test @DisplayName("Insertar correo duplicado devuelve 0") void caso09() throws Exception { cliente(); assertEquals(0,clientes.insertar(new Cliente(null,"Luis","ana@example.com","666"))); }
    @Test @DisplayName("Insertar null devuelve 0") void caso10() throws Exception { assertEquals(0,clientes.insertar(null)); }
}
