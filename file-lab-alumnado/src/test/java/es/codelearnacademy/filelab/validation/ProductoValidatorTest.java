package es.codelearnacademy.filelab.validation;

import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.model.Producto;
import org.junit.jupiter.api.Test;

class ProductoValidatorTest {

    @Test
    void aceptaProductoValido() {
        assertDoesNotThrow(() ->
                ProductoValidator.validar(new Producto(1L, "Teclado", 49.99, 10)));
    }

    @Test
    void rechazaIdNoPositivo() {
        assertThrows(IllegalArgumentException.class,
                () -> ProductoValidator.validar(new Producto(0L, "Teclado", 49.99, 10)));
    }

    @Test
    void rechazaNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> ProductoValidator.validar(new Producto(1L, " ", 49.99, 10)));
    }

    @Test
    void rechazaPrecioNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> ProductoValidator.validar(new Producto(1L, "Teclado", -1, 10)));
    }

    @Test
    void rechazaStockNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> ProductoValidator.validar(new Producto(1L, "Teclado", 10, -1)));
    }
}
