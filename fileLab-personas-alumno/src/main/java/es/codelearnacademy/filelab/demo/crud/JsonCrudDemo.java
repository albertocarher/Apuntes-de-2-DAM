package es.codelearnacademy.filelab.demo.crud;

import es.codelearnacademy.filelab.model.Producto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonCrudDemo {

    private final Path path;
    private final ObjectMapper mapper;

    public JsonCrudDemo(Path path, ObjectMapper mapper) {
        this.path = path;
        this.mapper = mapper;
    }

    public List<Producto> findAll() throws IOException {
        if (Files.notExists(path) || Files.size(path) == 0) return new ArrayList<>();
        return new ArrayList<>(mapper.readValue(
                path.toFile(), new TypeReference<List<Producto>>() {}));
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
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), items);
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonCrudDemo repo = new JsonCrudDemo(Path.of("data", "productos.json"), mapper);
        repo.create(new Producto(1, "Teclado", 49.99));
        repo.create(new Producto(2, "Ratón", 24.90));
        System.out.println(repo.findById(1).orElseThrow());
        repo.update(new Producto(1, "Teclado mecánico", 79.90));
        repo.delete(2);
        repo.findAll().forEach(System.out::println);
    }
}