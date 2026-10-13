package es.codelearnacademy.filelab;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.persona.csv.*;
import es.codelearnacademy.filelab.model.Persona;
class PersonaCsvTest {
    Path copy() throws IOException {
        Path p=Path.of("target/test-data/csv/personas.csv");
        Files.createDirectories(p.getParent());
        Files.copy(Path.of("data/personas.csv"),p,StandardCopyOption.REPLACE_EXISTING);
        return p;
    }
    @Test void recuperarCinco() throws IOException {
        assertEquals(5,new PersonaCsvRepository(copy()).findAll().size());
    }
    @Test void buscarPorDni() throws IOException {
        assertEquals("Ana Lopez",new PersonaCsvRepository(copy()).findById("12345678A").orElseThrow().nombre());
    }
    @Test void crearYPersistir() throws IOException {
        Path p=copy();
        var r=new PersonaCsvRepository(p);
        assertTrue(r.create(new Persona("9","Nuevo","n@a",22,true)));
        assertEquals(6,new PersonaCsvRepository(p).findAll().size());
    }
    @Test void actualizarYPersistir() throws IOException {
        Path p=copy();
        var r=new PersonaCsvRepository(p);
        assertTrue(r.update(new Persona("12345678A","Nueva Ana","ana@a",22,false)));
        assertEquals("Nueva Ana",new PersonaCsvRepository(p).findById("12345678A").orElseThrow().nombre());
    }
    @Test void separadoresEnNombre() throws IOException {
        Path p=copy();
        var r=new PersonaCsvRepository(p);
        assertTrue(r.create(new Persona("9","Ruiz, Ana","n@a",20,true)));
        assertEquals("Ruiz, Ana",new PersonaCsvRepository(p).findById("9").orElseThrow().nombre());
    }
}
