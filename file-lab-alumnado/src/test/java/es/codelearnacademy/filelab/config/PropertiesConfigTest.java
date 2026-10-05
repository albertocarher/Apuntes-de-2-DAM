package es.codelearnacademy.filelab.config;

import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.support.FixtureSupport;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PropertiesConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void leePropiedad() throws Exception {
        Path archivo = FixtureSupport.copiar("app.properties", tempDir.resolve("app.properties"));
        var config = new PropertiesConfig(archivo);
        assertEquals("csv", config.get("input.format").orElseThrow());
    }

    @Test
    void usaValorPorDefecto() {
        var config = new PropertiesConfig(tempDir.resolve("no-existe.properties"));
        assertEquals("xml", config.getOrDefault("format", "xml"));
    }

    @Test
    void lecturaInexistenteVacia() {
        var config = new PropertiesConfig(tempDir.resolve("no-existe.properties"));
        assertTrue(config.findAll().isEmpty());
    }

    @Test
    void persistePropiedad() {
        Path archivo = tempDir.resolve("app.properties");
        var config = new PropertiesConfig(archivo);
        assertTrue(config.put("output.format", "json"));
        assertEquals("json", new PropertiesConfig(archivo).get("output.format").orElseThrow());
    }

    @Test
    void eliminaPropiedad() {
        Path archivo = tempDir.resolve("app.properties");
        var config = new PropertiesConfig(archivo);
        assertTrue(config.put("modo", "test"));
        assertTrue(config.remove("modo"));
        assertTrue(config.get("modo").isEmpty());
    }
}
