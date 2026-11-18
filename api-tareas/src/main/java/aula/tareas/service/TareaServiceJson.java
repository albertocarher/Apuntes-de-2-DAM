package aula.tareas.service;

import aula.tareas.modelo.Tarea;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceJson")
public class TareaServiceJson implements ITareaService{

    private final ITareaRepository tareaRepositoryJson;

    public TareaServiceJson(@Qualifier("repositoryJson") ITareaRepository tareaRepositoryJson) {
        this.tareaRepositoryJson = tareaRepositoryJson;
    }

    @Override
    public List<Tarea> listar() {
        return tareaRepositoryJson.findAll();
    }

    @Override
    public Tarea buscar(long id) {
        return tareaRepositoryJson.findById(id);
    }

    @Override
    public Tarea crear(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return tareaRepositoryJson.create(titulo);
    }
}