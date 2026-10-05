package es.codelearnacademy.filelab.io;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PathServiceTest {

    private final PathService service = new PathService();

    @Test
    void creaRuta() {
        assertEquals(Path.of("data", "productos.csv"),
                service.crear("data", "productos.csv"));
    }

    @Test
    void obtieneNombre() {
        assertEquals("productos.csv",
                service.nombre(Path.of("data", "productos.csv")));
    }

    @Test
    void obtienePadre() {
        assertEquals(Path.of("data"),
                service.padre(Path.of("data", "productos.csv")));
    }

    @Test
    void normalizaRuta() {
        assertEquals(Path.of("data", "productos.csv"),
                service.normalizar(Path.of("data", ".", "temp", "..", "productos.csv")));
    }

    @Test
    void resuelveRuta() {
        assertEquals(Path.of("data", "productos.csv"),
                service.resolver(Path.of("data"), "productos.csv"));
    }

    @Test
    void relativizaRuta() {
        assertEquals(Path.of("productos.csv"),
                service.relativizar(Path.of("data"), Path.of("data", "productos.csv")));
    }

    @Test
    void obtieneExtension() {
        assertEquals("csv", service.extension(Path.of("data", "productos.csv")));
    }

    @Test
    void extensionVacia() {
        assertEquals("", service.extension(Path.of("README")));
    }
}
