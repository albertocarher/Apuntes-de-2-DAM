package es.codelearnacademy.filelab.repository;

import java.util.List;
import java.util.Optional;

/** Contrato CRUD generico. Ninguna operacion publica propaga IOException. */
public interface IRepository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    boolean create(T entity);

    boolean update(T entity);

    boolean delete(ID id);
}
