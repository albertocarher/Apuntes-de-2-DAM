package aula.tareas.repository;

import aula.tareas.modelo.Tarea;

import java.util.List;
import java.util.Optional;

/** CRUD compartido por los formatos de fichero, con lectura del fichero en cada operación. */
abstract class RepositorioTareaFichero implements ITareaRepository {
    abstract List<Tarea> readAll();
    abstract void writeAll(List<Tarea> tareas);

    @Override
    public synchronized List<Tarea> findAll() {
        // TODO: Leer el documento XML y recuperar sus tareas.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized Optional<Tarea> findById(long id) {
        // TODO: Buscar una tarea por identificador.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized Tarea create(Tarea tarea) {
        // TODO: Guardar la tarea nueva y devolverla con su identificador.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized Optional<Tarea> update(Tarea tarea) {
        // TODO: Actualizar una tarea existente.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized boolean delete(long id) {
        // TODO: Eliminar la tarea y comunicar si existía.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
