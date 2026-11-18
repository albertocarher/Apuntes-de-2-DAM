package aula.tareas.service;

import aula.tareas.modelo.Tarea;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceXml")
public class TareaServiceXml implements ITareaService{

    private final ITareaRepository tareaRepositoryXml;

    public TareaServiceXml(@Qualifier("repositoryXml") ITareaRepository tareaRepositoryXml) {
        this.tareaRepositoryXml = tareaRepositoryXml;
    }

    @Override
    public List<Tarea> listar() {
        return tareaRepositoryXml.findAll();
    }

    @Override
    public Tarea buscar(long id) {
        return tareaRepositoryXml.findById(id);
    }

    @Override
    public Tarea crear(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return tareaRepositoryXml.create(titulo);
    }
}