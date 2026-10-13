package es.codelearnacademy.filelab;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.config.*;
class PropertiesConfigTest {
    Path copy() throws IOException {
        Path p=Path.of("target/test-data/properties/academia.properties");
        Files.createDirectories(p.getParent());
        Files.copy(Path.of("data/academia.properties"),p,StandardCopyOption.REPLACE_EXISTING);
        return p;
    }
    @Test void consultaExistente() throws IOException {
        assertEquals("Centro Formativo",new PropertiesConfig(copy()).get("academia.nombre").orElseThrow());
    }
    @Test void ausente() throws IOException {
        assertTrue(new PropertiesConfig(copy()).get("no.existe").isEmpty());
    }
    @Test void valorDefecto() throws IOException {
        assertEquals("otro",new PropertiesConfig(copy()).getOrDefault("no.existe","otro"));
    }
    @Test void findAll() throws IOException {
        assertEquals("30",new PropertiesConfig(copy()).findAll().get("academia.plazas"));
    }
    @Test void putPersistente() throws IOException {
        Path p=copy();
        assertTrue(new PropertiesConfig(p).put("academia.plazas","45"));
        assertEquals("45",new PropertiesConfig(p).get("academia.plazas").orElseThrow());
    }
    @Test void insertPersistente() throws IOException {
        Path p=copy();
        assertTrue(new PropertiesConfig(p).put("nueva","si"));
        assertEquals("si",new PropertiesConfig(p).get("nueva").orElseThrow());
    }
    @Test void removePersistente() throws IOException {
        Path p=copy();
        assertTrue(new PropertiesConfig(p).remove("academia.curso"));
        assertTrue(new PropertiesConfig(p).get("academia.curso").isEmpty());
    }
}
