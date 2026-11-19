package aula.tareas.repository;

import aula.tareas.api.TareaRequest;

import java.util.List;

public interface ITareaRepository {
    public TareaRequest create(String titulo);
    public boolean delete(long identificador);
    public List<TareaRequest> findAll();
    public TareaRequest findById(long identificador);
    public TareaRequest update(TareaRequest tareaRequest);
}
