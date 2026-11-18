package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository()
public class TareaRepository implements ITareaRepository{
    private final AtomicLong secuencia = new AtomicLong();
    private final ConcurrentSkipListMap<Long, Tarea> tareas = new ConcurrentSkipListMap<>();

    @Override
    public Tarea create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new Tarea(id, titulo.strip());
        tareas.put(id, tarea);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        tareas.remove(identificador);
        return true;
    }

    @Override
    public List<Tarea> findAll() {
        return List.copyOf(tareas.values());
    }

    @Override
    public Tarea findById(long identificador) {
        return tareas.get(identificador);
    }

    @Override
    public Tarea update(Tarea tarea) {
        // buscar la tarea por el id
        // actualizar la tarea
        return null;
    }

}
