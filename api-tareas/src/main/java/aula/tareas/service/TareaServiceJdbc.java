package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceJdbc")
public class TareaServiceJdbc implements ITareaService{

    private final ITareaRepository tareaRepositoryJdbc;

    public TareaServiceJdbc(@Qualifier("repositoryJdbc") ITareaRepository tareaRepositoryJdbc) {
        this.tareaRepositoryJdbc = tareaRepositoryJdbc;
    }

    @Override
    public List<TareaRequest> listar() {
        return tareaRepositoryJdbc.findAll();
    }

    @Override
    public TareaRequest buscar(long id) {
        return tareaRepositoryJdbc.findById(id);
    }

    @Override
    public TareaRequest crear(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return tareaRepositoryJdbc.create(titulo);
    }
}