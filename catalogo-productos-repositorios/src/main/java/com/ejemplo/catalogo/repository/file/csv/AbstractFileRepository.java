package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Identificable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class AbstractFileRepository <T extends Identificable<ID>, ID>
        implements IRepository<T, ID> {
    Path path;
    List<T> list;

    public Path getPath() {
        return path;
    }

    protected abstract List<T> readAll();
    protected abstract void writeAll(List<T> elements);

    public AbstractFileRepository(Path path) {
        if (path == null) {
            throw new RuntimeException("El path es null");
        }
        this.path = path;
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<T> findAll() {
        return list;
    }

    @Override
    public Optional<T> findById(ID id)  {
        return
                list.stream
                        ().filter(p ->
                        p.id
                                ().equals(id)).findFirst();
    }

    @Override
    public void create(T element) {
        if (element == null ||
                element.id
                        ()  == null) {
            return;
        }
        if (
                list.stream
                        ().anyMatch(p ->
                        p.id
                                () ==
                                element.id
                                        ()))
            throw new IllegalArgumentException("Id duplicado: " +
                    element.id
                            ());
        list.add(element);
        writeAll(list);
    }

    @Override
    public boolean update(T element) {
        if (element == null ||
                element.id
                        () == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id() ==
                    element.id
                            ()) {
                list.set(i, element);
                writeAll(list);
                return true;
            }
        }
        return false;    }

    @Override
    public boolean delete(ID id) {

        boolean removed = list.removeIf(p ->
                p.id
                        () == id);
        if (removed) {
            writeAll(list);
        }
        return removed;
    }

}