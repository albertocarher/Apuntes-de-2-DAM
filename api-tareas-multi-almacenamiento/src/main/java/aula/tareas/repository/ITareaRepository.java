package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import java.util.List;
import java.util.Optional;

public interface ITareaRepository {
    List<Tarea> findAll();

    Optional<Tarea> findById(long id);

    Tarea create(Tarea tarea);

    Optional<Tarea> update(Tarea tarea);

    boolean delete(long id);
}
