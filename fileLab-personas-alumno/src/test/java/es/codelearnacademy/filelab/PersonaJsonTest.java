package es.codelearnacademy.filelab;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.persona.json.*;
import es.codelearnacademy.filelab.model.Persona;
class PersonaJsonTest {
    Path copy() throws IOException {
        Path p=Path.of("target/test-data/json/personas.json");
        Files.createDirectories(p.getParent());
        Files.copy(Path.of("data/personas.json"),p,StandardCopyOption.REPLACE_EXISTING);
        return p;
    }
    @Test void recuperarCinco() throws IOException {
        assertEquals(5,new PersonaJsonRepository(copy()).findAll().size());
    }
    @Test void buscarMarta() throws IOException {
        assertEquals("Marta Diaz",new PersonaJsonRepository(copy()).findById("34567890C").orElseThrow().nombre());
    }
    @Test void crearYPersistir() throws IOException {
        Path p=copy();
        var r=new PersonaJsonRepository(p);
        assertTrue(r.create(new Persona("9","Test","t@a",42,true)));
        assertEquals(6,new PersonaJsonRepository(p).findAll().size());
    }
    @Test void borrarYPersistir() throws IOException {
        Path p=copy();
        var r=new PersonaJsonRepository(p);
        assertTrue(r.delete("34567890C"));
        assertTrue(new PersonaJsonRepository(p).findById("34567890C").isEmpty());
    }
    @Test void conservaTipos() throws IOException {
        var p=new PersonaJsonRepository(copy()).findById("23456789B").orElseThrow();
        assertEquals(17,p.edad());
        assertTrue(p.activo());
    }
}
