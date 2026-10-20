package es.ejemplo.gestion;

import es.ejemplo.gestion.dao.ProductoDao;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoDaoTest extends BaseDatosTest {
    private ProductoDao dao;

    @BeforeEach
    void prepararDao() {
        dao = new ProductoDao(url);
    }

    @Test @Order(1)
    void listarTodos() {
        assertTrue(dao.listar().isEmpty());
        assertTrue(dao.insertar(new Producto(3, "Ratón", 12)));
        assertTrue(dao.insertar(new Producto(1, "Monitor", 100)));
        List<Producto> productos = dao.listar();
        assertEquals(2, productos.size());
        assertEquals(List.of(1, 3), productos.stream().map(Producto::getId).toList());
    }

    @Test @Order(2)
    void buscarPorId() {
        assertTrue(dao.insertar(new Producto(1, "Teclado", 25.50)));
        Producto encontrado = dao.buscarPorId(1).orElseThrow();
        assertAll(
            () -> assertEquals(1, encontrado.getId()),
            () -> assertEquals("Teclado", encontrado.getNombre()),
            () -> assertEquals(25.50, encontrado.getPrecio(), 0.0001)
        );
    }

    @Test @Order(3)
    void buscarPorIdInexistente() {
        assertTrue(dao.buscarPorId(77).isEmpty());
    }

    @Test @Order(4)
    void insertar() {
        assertTrue(dao.insertar(new Producto(1, "Teclado", 25.50)));
        assertEquals(1, dao.contar());
    }

    @Test @Order(5)
    void actualizar() {
        assertTrue(dao.insertar(new Producto(2, "Viejo", 5)));
        assertTrue(dao.actualizar(new Producto(2, "Nuevo", 9.99)));
        Producto actualizado = dao.buscarPorId(2).orElseThrow();
        assertAll(
            () -> assertEquals("Nuevo", actualizado.getNombre()),
            () -> assertEquals(9.99, actualizado.getPrecio(), 0.0001)
        );
    }

    @Test @Order(6)
    void actualizarInexistente() {
        assertFalse(dao.actualizar(new Producto(404, "Sin registro", 1)));
    }

    @Test @Order(7)
    void eliminar() {
        assertTrue(dao.insertar(new Producto(2, "Teclado", 25)));
        assertTrue(dao.eliminar(2));
        assertTrue(dao.buscarPorId(2).isEmpty());
    }

    @Test @Order(8)
    void eliminarInexistente() {
        assertFalse(dao.eliminar(404));
    }

    @Test @Order(9)
    void filtrarPorNombreIgnorandoMayusculas() {
        assertTrue(dao.insertar(new Producto(1, "Teclado mecánico", 40)));
        assertTrue(dao.insertar(new Producto(2, "TECLADO compacto", 30)));
        assertTrue(dao.insertar(new Producto(3, "Pantalla", 150)));
        assertEquals(List.of(2, 1), dao.buscarPorNombre("teCLado")
            .stream().map(Producto::getId).toList());
        assertTrue(dao.buscarPorNombre("inexistente").isEmpty());
    }

    @Test @Order(10)
    void filtrarPorPrecioConExtremosIncluidos() {
        assertTrue(dao.insertar(new Producto(1, "A", 10)));
        assertTrue(dao.insertar(new Producto(2, "B", 20)));
        assertTrue(dao.insertar(new Producto(3, "C", 21)));
        assertEquals(List.of(1, 2), dao.buscarPorPrecio(10, 20)
            .stream().map(Producto::getId).toList());
    }

    @Test @Order(11)
    void filtrarPorPrecioRangoInvertido() {
        assertTrue(dao.insertar(new Producto(1, "A", 20)));
        assertTrue(dao.buscarPorPrecio(30, 10).isEmpty());
    }

    @Test @Order(12)
    void contarProductos() {
        assertEquals(0, dao.contar());
        assertTrue(dao.insertar(new Producto(1, "A", 10)));
        assertTrue(dao.insertar(new Producto(2, "B", 30)));
        assertEquals(2, dao.contar());
    }

    @Test @Order(13)
    void calcularPrecioMedio() {
        assertEquals(0, dao.precioMedio(), 0.0001);
        assertTrue(dao.insertar(new Producto(1, "A", 10)));
        assertTrue(dao.insertar(new Producto(2, "B", 30)));
        assertEquals(20, dao.precioMedio(), 0.0001);
    }

    @Test @Order(14)
    void insertarDuplicado() {
        assertTrue(dao.insertar(new Producto(1, "Válido", 0)));
        assertFalse(dao.insertar(new Producto(1, "Duplicado", 5)));
        assertEquals(1, dao.contar());
    }

    @Test @Order(15)
    void insertarIdNegativo() {
        assertFalse(dao.insertar(new Producto(-1, "Negativo", 10)));
        assertEquals(0, dao.contar());
    }

    @Test @Order(16)
    void insertarNombreVacio() {
        assertFalse(dao.insertar(new Producto(2, "   ", 10)));
        assertEquals(0, dao.contar());
    }

    @Test @Order(17)
    void insertarPrecioNegativo() {
        assertFalse(dao.insertar(new Producto(3, "Precio inválido", -0.01)));
        assertEquals(0, dao.contar());
    }
}
