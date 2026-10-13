package es.codelearnacademy.filelab.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Infraestructura CRUD reutilizable. Proporcionada por el docente. */
public abstract class AbstractFileRepository<T, ID> implements IRepository<T, ID> {

    protected abstract ID getId(T entity);

    // Los errores de E/S solo se declaran en los metodos protegidos internos.
    protected abstract List<T> readAll() ;

    protected abstract void writeAll(List<T> entities);

    @Override
    public List<T> findAll() {
        return readAll();
    }

    @Override
    public Optional<T> findById(ID id) {throw new UnsupportedOperationException("Todo: findById");}

    @Override
    public boolean create(T entity) {
        throw new UnsupportedOperationException("TODO: create");
    }

    @Override
    public boolean update(T entity) {
        throw new UnsupportedOperationException("TODO: update");
    }

    @Override
    public boolean delete(ID id) {
        throw new UnsupportedOperationException("TODO: delete");
    }
}
