package es.codelearnacademy.filelab.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractFileRepository<T, ID> implements IRepository<T, ID> {

    @Override
    public List<T> findAll() {
        try {
            return readAll();
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return findAll().stream()
                .filter(entity -> Objects.equals(getId(entity), id))
                .findFirst();
    }

    @Override
    public boolean create(T entity) {
        if (entity == null) {
            return false;
        }
        try {
            List<T> actuales;
            try {
                actuales = readAll();
            } catch (FileNotFoundException | NoSuchFileException e) {
                actuales = List.of();
            }
            var entities = new ArrayList<>(actuales);
            if (posicion(entities, getId(entity)) >= 0) {
                return false;
            }
            entities.add(entity);
            writeAll(entities);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean update(T entity) {
        if (entity == null) {
            return false;
        }
        try {
            var entities = new ArrayList<>(readAll());
            int posicion = posicion(entities, getId(entity));
            if (posicion < 0) {
                return false;
            }
            entities.set(posicion, entity);
            writeAll(entities);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean delete(ID id) {
        try {
            var entities = new ArrayList<>(readAll());
            int posicion = posicion(entities, id);
            if (posicion < 0) {
                return false;
            }
            entities.remove(posicion);
            writeAll(entities);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private int posicion(List<T> entities, ID id) {
        for (int i = 0; i < entities.size(); i++) {
            if (Objects.equals(getId(entities.get(i)), id)) {
                return i;
            }
        }
        return -1;
    }

    protected abstract ID getId(T entity);

    protected abstract List<T> readAll() throws IOException;

    protected abstract void writeAll(List<T> entities) throws IOException;
}