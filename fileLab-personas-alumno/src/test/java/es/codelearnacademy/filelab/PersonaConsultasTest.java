package es.codelearnacademy.filelab;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.persona.*;
import es.codelearnacademy.filelab.model.Persona;
import es.codelearnacademy.filelab.repository.IRepository;
class PersonaConsultasTest {
    private IPersonaRepository repo() {
        return new IPersonaRepository() {
            final List<Persona> p=List.of(new Persona("1","Ana","a",21,true),new Persona("2","Luis","b",17,true),new Persona("3","Marta","c",35,false));
            public List<Persona> findAll() {
                return p;
            }
            public Optional<Persona> findById(String id) {
                return p.stream().filter(x->x.dni().equals(id)).findFirst();
            }
            public boolean create(Persona p) {
                return false;
            }
            public boolean update(Persona p) {
                return false;
            }
            public boolean delete(String id) {
                return false;
            }
        }
        ;
    }
    @Test void edadMinima() {
        assertEquals(2,repo().findByEdadMinima(18).size());
    }
    @Test void edadInclusiva() {
        assertEquals("1",repo().findByEdadMinima(21).get(0).dni());
    }
    @Test void edadSinCoincidencias() {
        assertTrue(repo().findByEdadMinima(99).isEmpty());
    }
    @Test void activos() {
        assertEquals(2,repo().findByActivo(true).size());
    }
    @Test void inactivos() {
        assertEquals("3",repo().findByActivo(false).get(0).dni());
    }
    @Test void ordenConservado() {
        assertEquals(List.of("1","2"),repo().findByActivo(true).stream().map(Persona::dni).toList());
    }
}
