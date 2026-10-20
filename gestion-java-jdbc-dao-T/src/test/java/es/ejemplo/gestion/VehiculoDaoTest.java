package es.ejemplo.gestion;

import es.ejemplo.gestion.dao.VehiculoDao;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Vehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VehiculoDaoTest extends BaseDatosTest {
    private VehiculoDao dao;

    @BeforeEach
    void prepararDao() { dao = new VehiculoDao(url); }

    @Test @Order(1) void listarTodos() {
        assertTrue(dao.listar().isEmpty());
        assertTrue(dao.insertar(new Vehiculo("ZZZ", "Seat", "Ibiza", 2020)));
        assertTrue(dao.insertar(new Vehiculo("AAA", "Ford", "Focus", 2019)));
        assertEquals(List.of("AAA", "ZZZ"), dao.listar().stream().map(Vehiculo::getMatricula).toList());
    }
    @Test @Order(2) void buscarPorMatricula() {
        assertTrue(dao.insertar(new Vehiculo("1234ABC", "Seat", "Ibiza", 2020)));
        Vehiculo v = dao.buscarPorMatricula("1234ABC").orElseThrow();
        assertAll(() -> assertEquals("1234ABC", v.getMatricula()),
                  () -> assertEquals("Seat", v.getMarca()),
                  () -> assertEquals("Ibiza", v.getModelo()),
                  () -> assertEquals(2020, v.getAnio()));
    }
    @Test @Order(3) void buscarPorMatriculaInexistente() {
        assertTrue(dao.buscarPorMatricula("NOEXISTE").isEmpty());
    }
    @Test @Order(4) void insertar() {
        assertTrue(dao.insertar(new Vehiculo("1234ABC", "Seat", "Ibiza", 2020)));
        assertEquals(1, dao.contar());
    }
    @Test @Order(5) void actualizar() {
        assertTrue(dao.insertar(new Vehiculo("1234ABC", "Seat", "Ibiza", 2020)));
        assertTrue(dao.actualizar(new Vehiculo("1234ABC", "Toyota", "Corolla", 2023)));
        Vehiculo v = dao.buscarPorMatricula("1234ABC").orElseThrow();
        assertAll(() -> assertEquals("Toyota", v.getMarca()),
                  () -> assertEquals("Corolla", v.getModelo()),
                  () -> assertEquals(2023, v.getAnio()));
    }
    @Test @Order(6) void actualizarInexistente() {
        assertFalse(dao.actualizar(new Vehiculo("NOEXISTE", "Seat", "Ibiza", 2020)));
    }
    @Test @Order(7) void eliminar() {
        assertTrue(dao.insertar(new Vehiculo("1234ABC", "Seat", "Ibiza", 2020)));
        assertTrue(dao.eliminar("1234ABC"));
        assertTrue(dao.buscarPorMatricula("1234ABC").isEmpty());
    }
    @Test @Order(8) void eliminarInexistente() {
        assertFalse(dao.eliminar("NOEXISTE"));
    }
    @Test @Order(9) void buscarPorMarcaSinDistinguirMayusculas() {
        assertTrue(dao.insertar(new Vehiculo("AAA", "Toyota", "Corolla", 2021)));
        assertTrue(dao.insertar(new Vehiculo("BBB", "TOYOTA", "Yaris", 2018)));
        assertTrue(dao.insertar(new Vehiculo("CCC", "Ford", "Focus", 2020)));
        assertEquals(2, dao.buscarPorMarca("toYoTa").size());
    }
    @Test @Order(10) void buscarPorMarcaInexistente() {
        assertTrue(dao.insertar(new Vehiculo("AAA", "Seat", "Ibiza", 2020)));
        assertTrue(dao.buscarPorMarca("Renault").isEmpty());
    }
    @Test @Order(11) void buscarPorAnioExtremosIncluidos() {
        assertTrue(dao.insertar(new Vehiculo("AAA", "Seat", "A", 2010)));
        assertTrue(dao.insertar(new Vehiculo("BBB", "Seat", "B", 2020)));
        assertTrue(dao.insertar(new Vehiculo("CCC", "Seat", "C", 2021)));
        assertEquals(List.of("AAA", "BBB"), dao.buscarPorAnio(2010, 2020).stream().map(Vehiculo::getMatricula).toList());
    }
    @Test @Order(12) void buscarPorAnioRangoInvertido() {
        assertTrue(dao.buscarPorAnio(2025, 2020).isEmpty());
    }
    @Test @Order(13) void contarVehiculos() {
        assertEquals(0, dao.contar());
        assertTrue(dao.insertar(new Vehiculo("AAA", "Seat", "Ibiza", 2010)));
        assertTrue(dao.insertar(new Vehiculo("BBB", "Ford", "Focus", 2020)));
        assertEquals(2, dao.contar());
    }
    @Test @Order(14) void contarPorMarca() {
        assertTrue(dao.contarPorMarca().isEmpty());
        assertTrue(dao.insertar(new Vehiculo("AAA", "Seat", "Ibiza", 2010)));
        assertTrue(dao.insertar(new Vehiculo("BBB", "Ford", "Focus", 2020)));
        assertTrue(dao.insertar(new Vehiculo("CCC", "Seat", "León", 2021)));
        assertEquals(Map.of("Seat", 2, "Ford", 1), dao.contarPorMarca());
    }
    @Test @Order(15) void insertarDuplicado() {
        assertTrue(dao.insertar(new Vehiculo("AAA", "Seat", "Ibiza", 2020)));
        assertFalse(dao.insertar(new Vehiculo("AAA", "Ford", "Focus", 2019)));
        assertEquals(1, dao.contar());
    }
    @Test @Order(16) void insertarMatriculaMinusculas() {
        assertFalse(dao.insertar(new Vehiculo("abc", "Ford", "Focus", 2019)));
        assertEquals(0, dao.contar());
    }
    @Test @Order(17) void insertarMarcaVacia() {
        assertFalse(dao.insertar(new Vehiculo("BBB", " ", "Focus", 2019)));
        assertEquals(0, dao.contar());
    }
    @Test @Order(18) void insertarModeloVacio() {
        assertFalse(dao.insertar(new Vehiculo("CCC", "Ford", " ", 2019)));
        assertEquals(0, dao.contar());
    }
    @Test @Order(19) void insertarAnioInvalido() {
        assertFalse(dao.insertar(new Vehiculo("DDD", "Ford", "Focus", 1885)));
        assertEquals(0, dao.contar());
    }
}
