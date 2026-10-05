package es.codelearnacademy.filelab.repository;

import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.model.Producto;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public abstract class ProductoRepositoryContractTest {

    protected abstract IProductoRepository crearRepositorio(Path archivo) throws Exception;

    protected abstract Path crearFixture() throws Exception;

    @Test
    void leeTodosLosProductos() throws Exception {
        IProductoRepository repositorio = crearRepositorio(crearFixture());
        assertEquals(4, repositorio.findAll().size());
    }

    @Test
    void encuentraProductoPorId() throws Exception {
        IProductoRepository repositorio = crearRepositorio(crearFixture());
        assertEquals("Monitor", repositorio.findById(2L).orElseThrow().nombre());
    }

    @Test
    void idInexistenteVacio() throws Exception {
        IProductoRepository repositorio = crearRepositorio(crearFixture());
        long idInexistente = 9999L;
        assertTrue(repositorio.findById(idInexistente).isEmpty());
    }

    @Test
    void creaProducto() throws Exception {
        IProductoRepository repositorio = crearRepositorio(crearFixture());
        Producto producto = new Producto(10L, "Micrófono", 89.90, 6);
        assertTrue(repositorio.create(producto));
        assertEquals(producto, repositorio.findById(10L).orElseThrow());
    }

    @Test
    void actualizaProducto() throws Exception {
        IProductoRepository repositorio = crearRepositorio(crearFixture());
        Producto producto = new Producto(2L, "Monitor 4K", 399.90, 3);
        assertTrue(repositorio.update(producto));
        assertEquals("Monitor 4K", repositorio.findById(2L).orElseThrow().nombre());
    }

    @Test
    void eliminaProducto() throws Exception {
        IProductoRepository repositorio = crearRepositorio(crearFixture());
        assertTrue(repositorio.delete(3L));
        assertTrue(repositorio.findById(3L).isEmpty());
    }
}
