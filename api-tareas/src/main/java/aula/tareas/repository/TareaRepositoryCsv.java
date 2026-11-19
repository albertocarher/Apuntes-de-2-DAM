package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Repository("repositoryCsv")
public class TareaRepositoryCsv implements ITareaRepository{
    private  Path path;
    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true).build();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "titulo")
            .build();

    public TareaRepositoryCsv() {
        path = Path.of("data", "tarea.csv");
    }

    @Override
    public TareaRequest create(String titulo) {
        List<TareaRequest> lista = new ArrayList<>(findAll());
        long id = lista.stream().mapToLong(TareaRequest::id).max().orElse(0) + 1;
        var tarea = new TareaRequest(id, titulo.strip());
        lista.add(tarea);
        saveAll(lista);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        List<TareaRequest> lista = new ArrayList<>(findAll());
        boolean eliminada = lista.removeIf(tarea -> tarea.id() == identificador);
        if (eliminada) {
            saveAll(lista);
        }
        return eliminada;
    }

    @Override
    public List<TareaRequest> findAll() {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }
        try {
            List<TareaRequest> result = new ArrayList<>();
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                 CSVParser parser = inputFormat.parse(reader)) {
                for (CSVRecord row : parser) {
                    result.add(new TareaRequest(
                            Long.parseLong(row.get("id")),
                            row.get("titulo")));
                }
            }
            return result;
        }catch (Exception e) {
            return List.of();
        }

    }

    @Override
    public TareaRequest findById(long identificador) {
        return findAll().stream()
                .filter(tarea -> tarea.id() == identificador)
                .findFirst()
                .orElse(null);
    }

    @Override
    public TareaRequest update(TareaRequest tareaRequest) {
        List<TareaRequest> lista = new ArrayList<>(findAll());
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == tareaRequest.id()) {
                lista.set(i, tareaRequest);
                saveAll(lista);
                return tareaRequest;
            }
        }
        return null;
    }

    private void saveAll(List<TareaRequest> items) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                 CSVPrinter printer = outputFormat.print(writer)) {
                for (TareaRequest tareaRequest : items) {
                    printer.printRecord(tareaRequest.id(), tareaRequest.titulo());
                }
            }
        }catch (Exception exception) {
        }
    }
}