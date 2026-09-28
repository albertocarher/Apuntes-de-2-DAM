package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository implements IRepository {

    private Path path;

    List<Producto> productos;

    public Path getPath() {
        return path;
    }

    public AbstractRepository(Path path) {
        if(path == null) {
            throw new RuntimeException("El path es null");
        }
        this.path = path;
        if(Files.notExists(path)){
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }
    }

    public abstract void saveAll(List<Producto> productos);
    public abstract List<Producto> load();

    @Override
    public List<Producto> findAll() {
        return productos;
    }

    @Override
    public Optional<Producto> findById(long id) {
        return findAll().stream().filter(p -> p.id() == id).findFirst();
    }

    @Override
    public void create(Producto producto){
        if(producto == null || producto.id() < 0){
            return;
        }
        if (productos.stream().anyMatch(p -> p.id() == producto.id()))
            throw new IllegalArgumentException("Id duplicado: " + producto.id());
        productos.add(producto);
        saveAll(productos);
    }

    @Override
    public boolean update(Producto producto){
        if(producto == null || producto.id() < 0){
            return false;
        }
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).id() == producto.id()) {
                productos.set(i, producto);
                saveAll(productos);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        boolean removed = productos.removeIf(p -> p.id() == id);
        if (removed) {
            saveAll(productos);
        }
        return removed;
    }
}
