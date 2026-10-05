package es.codelearnacademy.filelab.repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractFileRepository<T, ID> implements IRepository<T, ID> {

    @Override
    public List<T> findAll() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    public Optional<T> findById(ID id) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    public boolean create(T entity) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    public boolean update(T entity) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    public boolean delete(ID id) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    protected abstract ID getId(T entity);

    protected abstract List<T> readAll() throws IOException;

    protected abstract void writeAll(List<T> entities) throws IOException;
}
