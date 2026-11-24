package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceOrm")
public class TareaServiceOrm implements ITareaService{

    private final ITareaRepository tareaRepositoryOrm;

    public TareaServiceOrm(@Qualifier("repositoryOrm") ITareaRepository tareaRepositoryOrm) {
        this.tareaRepositoryOrm = tareaRepositoryOrm;
    }

    @Override
    public List<TareaRequest> listar() {
        return tareaRepositoryOrm.findAll();
    }

    @Override
    public TareaRequest buscar(long id) {
        return tareaRepositoryOrm.findById(id);
    }

    @Override
    public TareaRequest crear(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return tareaRepositoryOrm.create(titulo);
    }
}