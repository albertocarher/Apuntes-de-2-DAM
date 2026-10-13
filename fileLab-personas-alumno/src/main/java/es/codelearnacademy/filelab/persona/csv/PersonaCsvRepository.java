package es.codelearnacademy.filelab.persona.csv;
import es.codelearnacademy.filelab.model.Persona;
import es.codelearnacademy.filelab.persona.IPersonaRepository;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.util.*;
public class PersonaCsvRepository extends AbstractFileRepository<Persona,String> implements IPersonaRepository {
    private final Path path;
    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("dni", "nombre", "email", "edad", "activo")
            .build();

    public PersonaCsvRepository(Path path) {
        this.path=path;
    }

    @Override
    protected String getId(Persona p) {
        return p.dni();
    }

    @Override
    protected List<Persona> readAll()  {
        if(Files.notExists(path)) {
            return new ArrayList<>();
        }
        List<Persona> personas = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = inputFormat.parse(reader)) {
            for (CSVRecord record : parser) {
                personas.add(new Persona(record.get("id"),record.get("nombre"),
                        record.get("email"), Integer.parseInt(record.get("edad")),Boolean.parseBoolean(record.get("activo"))));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return personas;
    }

    @Override
    protected void writeAll(List<Persona> personas)  {
       personas = findAll();
       try (Writer writer = Files.newBufferedWriter(path,StandardCharsets.UTF_8);
            CSVPrinter csvPrinter = outputFormat.print(writer)){
           for(Persona persona : personas) {
               csvPrinter.printRecord(persona.dni(), persona.nombre(), persona.email(), persona.edad(), persona.activo());
           }
       } catch (IOException e) {
           throw new RuntimeException(e);
       }

    }
}
