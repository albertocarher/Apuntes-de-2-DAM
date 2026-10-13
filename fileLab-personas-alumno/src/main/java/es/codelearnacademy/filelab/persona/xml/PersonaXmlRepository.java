package es.codelearnacademy.filelab.persona.xml;
import es.codelearnacademy.filelab.model.Persona;
import es.codelearnacademy.filelab.persona.IPersonaRepository;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import java.nio.file.*;
import java.io.*;
import java.util.*;
public class PersonaXmlRepository extends AbstractFileRepository<Persona,String> implements IPersonaRepository {
    private final Path path;
    public PersonaXmlRepository(Path path) {
        this.path=path;
    }
    @Override
    protected String getId(Persona p) {
        return p.dni();
    }
    @Override
    protected List<Persona> readAll() {
        throw new UnsupportedOperationException("TODO: leer XML con XmlMapper");
    }
    @Override
    protected void writeAll(List<Persona> personas) {
        throw new UnsupportedOperationException("TODO: escribir XML con XmlMapper");
    }
}
