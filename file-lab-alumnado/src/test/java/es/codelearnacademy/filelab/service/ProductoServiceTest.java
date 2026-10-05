package es.codelearnacademy.filelab.service;

import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.support.ProductoRepositoryMemoria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductoServiceTest {

    private ProductoService service;

    @BeforeEach
    void preparaProductos() {
        var repositorio = new ProductoRepositoryMemoria(List.of(
                new Producto(1L, "Teclado mecánico", 49.99, 10),
                new Producto(2L, "Monitor", 219.90, 4),
                new Producto(3L, "Ratón", 24.50, 25),
                new Producto(4L, "Webcam", 79.00, 0)
        ));
        service = new ProductoService(repositorio);
    }

    @Test
    void encuentraMaximoPrecio() {
        assertEquals("Monitor", service.maximoPrecio().orElseThrow().nombre());
    }

    @Test
    void encuentraMinimoPrecio() {
        assertEquals("Ratón", service.minimoPrecio().orElseThrow().nombre());
    }

    @Test
    void encuentraMaximoStock() {
        assertEquals("Ratón", service.maximoStock().orElseThrow().nombre());
    }

    @Test
    void encuentraMinimoStock() {
        assertEquals("Webcam", service.minimoStock().orElseThrow().nombre());
    }

    @Test
    void calculaStockTotal() {
        assertEquals(39, service.stockTotal());
    }

    @Test
    void calculaValorInventario() {
        double esperado = 49.99 * 10 + 219.90 * 4 + 24.50 * 25;
        assertEquals(esperado, service.valorInventario(), 0.001);
    }

    @Test
    void encuentraProductosSinStock() {
        assertEquals(List.of("Webcam"),
                service.sinStock().stream().map(Producto::nombre).toList());
    }

    @Test
    void buscaPorNombre() {
        assertEquals(List.of("Teclado mecánico"),
                service.buscar("TECLA").stream().map(Producto::nombre).toList());
    }
}
