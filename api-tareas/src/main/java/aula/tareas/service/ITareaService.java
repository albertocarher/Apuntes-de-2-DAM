package aula.tareas.service;

import aula.tareas.modelo.Tarea;

import java.util.List;

public interface ITareaService {
    /**
     * Soy la documentacion del metodo
     * @return
     */
    public List<Tarea> listar();

    /**
     * Soy la documentacion del metodo
     * @param id
     * @return
     */
    public Tarea buscar(long id);

    /**
     * Sigo siendo la documentacion del metodo
     * @param titulo
     * @return
     */
    public Tarea crear(String titulo);
}
