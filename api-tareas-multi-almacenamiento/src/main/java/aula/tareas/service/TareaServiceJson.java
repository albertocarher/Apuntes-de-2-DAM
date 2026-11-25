package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.modelo.Tarea;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public class TareaServiceJson implements ITareaService {
    private final ITareaRepository repository;

    public TareaServiceJson(@Qualifier("tareaRepositoryJson") ITareaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Tarea> listar() {
        // TODO: Recuperar el listado mediante el repositorio.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public Optional<Tarea> buscar(long id) {
        // TODO: Consultar el repositorio con el identificador.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public Tarea crear(TareaRequest request) {
        // TODO: Convertir la petición a dominio y solicitar el alta.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public Optional<Tarea> actualizar(long id, TareaRequest request) {
        // TODO: Convertir la petición usando el ID y solicitar el cambio.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public boolean eliminar(long id) {
        // TODO: Solicitar el borrado al repositorio.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
