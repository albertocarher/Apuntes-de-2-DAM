package es.codelearnacademy.filelab.persona.json;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codelearnacademy.filelab.model.Persona;
import es.codelearnacademy.filelab.persona.IPersonaRepository;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import java.nio.file.*;
import java.io.*;
import java.util.*;
public class PersonaJsonRepository extends AbstractFileRepository<Persona,String> implements IPersonaRepository {
    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();
    public PersonaJsonRepository(Path path) {
        this.path=path;
    }

    @Override
    protected String getId(Persona p) {
        return p.dni();
    }

    @Override
    protected List<Persona> readAll() {
        List<Persona> personas;
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            personas = mapper.readValue(path.toFile(), new TypeReference<>() {});

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return personas;

    }

    @Override
    protected void writeAll(List<Persona> personas) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        personas.forEach(persona -> {
            try {
                mapper.writeValue(path.toFile(), persona);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
