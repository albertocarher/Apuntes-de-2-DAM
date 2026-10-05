package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class JsonRepository extends AbstractRepository {
    private final ObjectMapper mapper;
    public JsonRepository(Path path) {
        super(path);
        productos = load();
        mapper = new ObjectMapper();
    }

    @Override
    public void saveAll(List<Producto> productos) {
        Path temporal = null;
        try {
            Path destino = getPath().toAbsolutePath();
            Path directorio = destino.getParent();
            Files.createDirectories(directorio);
            temporal = Files.createTempFile(directorio, "productos-", ".json.tmp");
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporal.toFile(), productos);
            try {
                Files.move(temporal, destino,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + getPath(), e);
        } finally {
            if (temporal != null) {
                try { Files.deleteIfExists(temporal); } catch (IOException ignored) { }
            }
        }
    }

    @Override
    public List<Producto> load() {
        try {
            List<Producto> leidos = mapper.readValue(
                    getPath().toFile(), new TypeReference<List<Producto>>() {});
            if (leidos == null) {
                throw new IllegalArgumentException("El JSON debe contener una lista, no null");
            }
            productos.clear();
            productos.addAll(leidos);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar " + getPath(), e);
        }
        return productos;
    }
}
