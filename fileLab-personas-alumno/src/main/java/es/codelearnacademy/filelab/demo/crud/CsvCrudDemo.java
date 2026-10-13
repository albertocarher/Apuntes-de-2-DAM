package es.codelearnacademy.filelab.demo.crud;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import es.codelearnacademy.filelab.model.Producto;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvCrudDemo {

    private final Path path;
    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "nombre", "precio")
            .build();

    public CsvCrudDemo(Path path) { this.path = path; }

    public List<Producto> findAll() throws IOException {
        if (Files.notExists(path)) return new ArrayList<>();
        List<Producto> result = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = inputFormat.parse(reader)) {
            for (CSVRecord row : parser) {
                result.add(new Producto(
                        Long.parseLong(row.get("id")),
                        row.get("nombre"),
                        Double.parseDouble(row.get("precio"))));
            }
        }
        return result;
    }

    public Optional<Producto> findById(long id) throws IOException {
        return findAll().stream().filter(p -> p.id() == id).findFirst();
    }

    public void create(Producto producto) throws IOException {
        List<Producto> items = findAll();
        if (items.stream().anyMatch(p -> p.id() == producto.id()))
            throw new IllegalArgumentException("Id duplicado: " + producto.id());
        items.add(producto);
        saveAll(items);
    }

    public boolean update(Producto producto) throws IOException {
        List<Producto> items = findAll();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).id() == producto.id()) {
                items.set(i, producto);
                saveAll(items);
                return true;
            }
        }
        return false;
    }

    public boolean delete(long id) throws IOException {
        List<Producto> items = findAll();
        boolean removed = items.removeIf(p -> p.id() == id);
        if (removed) saveAll(items);
        return removed;
    }

    private void saveAll(List<Producto> items) throws IOException {
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, outputFormat)) {
            for (Producto p : items) printer.printRecord(p.id(), p.nombre(), p.precio());
        }
    }

    public static void main(String[] args) throws IOException {
        CsvCrudDemo repo = new CsvCrudDemo(Path.of("data", "productos.csv"));
        repo.create(new Producto(1, "Teclado", 49.99));
        repo.create(new Producto(2, "Monitor, 27 pulgadas", 219.90));
        System.out.println(repo.findById(2).orElseThrow());
        repo.update(new Producto(1, "Teclado mecánico", 79.90));
        repo.delete(2);
        repo.findAll().forEach(System.out::println);
    }
}