package es.codelearnacademy.filelab;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.persona.xml.*;
import es.codelearnacademy.filelab.model.Persona;
class PersonaXmlTest {
    Path copy() throws IOException {
        Path p=Path.of("target/test-data/xml/personas.xml");
        Files.createDirectories(p.getParent());
        Files.copy(Path.of("data/personas.xml"),p,StandardCopyOption.REPLACE_EXISTING);
        return p;
    }
    @Test void recuperarCinco() throws IOException {
        assertEquals(5,new PersonaXmlRepository(copy()).findAll().size());
    }
    @Test void localizarCarlos() throws IOException {
        assertEquals("Carlos Ruiz",new PersonaXmlRepository(copy()).findById("45678901D").orElseThrow().nombre());
    }
    @Test void crearYPersistir() throws IOException {
        Path p=copy();
        var r=new PersonaXmlRepository(p);
        assertTrue(r.create(new Persona("9","Nuevo","n@a",25,true)));
        assertEquals(6,new PersonaXmlRepository(p).findAll().size());
    }
    @Test void actualizarYPersistir() throws IOException {
        Path p=copy();
        var r=new PersonaXmlRepository(p);
        assertTrue(r.update(new Persona("12345678A","Ana Nueva","n@a",33,false)));
        assertEquals(33,new PersonaXmlRepository(p).findById("12345678A").orElseThrow().edad());
    }
    @Test void borrarYPersistir() throws IOException {
        Path p=copy();
        var r=new PersonaXmlRepository(p);
        assertTrue(r.delete("34567890C"));
        assertEquals(4,new PersonaXmlRepository(p).findAll().size());
    }
    @Test void elementosXml() throws IOException {
        Path p=copy();
        var r=new PersonaXmlRepository(p);
        assertTrue(r.create(new Persona("9","Ana","n@a",25,true)));
        String s=Files.readString(p);
        assertTrue(s.contains("<personas"));
        assertTrue(s.contains("<persona>"));
    }
    @Test void documentoLista() {
        assertEquals(1,new DocumentoPersonas(List.of(new Persona("1","A","a",2,true))).getPersonas().size());
    }
}
