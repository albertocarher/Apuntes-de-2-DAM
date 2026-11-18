package aula.tareas.repository;

import aula.tareas.modelo.Tarea;

import java.util.List;

public interface ITareaRepository {
    public Tarea create(String titulo);
    public boolean delete(long identificador);
    public List<Tarea> findAll();
    public Tarea findById(long identificador);
    public Tarea update(Tarea tarea);
}
