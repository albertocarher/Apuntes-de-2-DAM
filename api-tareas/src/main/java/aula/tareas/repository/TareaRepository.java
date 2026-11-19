package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository("repository")
public class TareaRepository implements ITareaRepository{
    private final AtomicLong secuencia = new AtomicLong();
    private final ConcurrentSkipListMap<Long, TareaRequest> tareas = new ConcurrentSkipListMap<>();

    @Override
    public TareaRequest create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new TareaRequest(id, titulo.strip());
        tareas.put(id, tarea);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        tareas.remove(identificador);
        return true;
    }

    @Override
    public List<TareaRequest> findAll() {
        return List.copyOf(tareas.values());
    }

    @Override
    public TareaRequest findById(long identificador) {
        return tareas.get(identificador);
    }

    @Override
    public TareaRequest update(TareaRequest tareaRequest) {
        // buscar la tarea por el id
        // actualizar la tarea
        return null;
    }

}
