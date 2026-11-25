package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("tareaRepositoryOrm")
public class TareaRepositoryOrm implements ITareaRepository {
    private final ITareaRepositoryJpa jpa;

    public TareaRepositoryOrm(ITareaRepositoryJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Tarea> findAll() {
        // TODO: Recuperar las tareas de este almacenamiento.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public Optional<Tarea> findById(long id) {
        // TODO: Buscar una tarea por identificador.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public Tarea create(Tarea tarea) {
        // TODO: Guardar la tarea nueva y devolverla con su identificador.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public Optional<Tarea> update(Tarea tarea) {
        // TODO: Actualizar una tarea existente.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public boolean delete(long id) {
        // TODO: Eliminar la tarea y comunicar si existía.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
