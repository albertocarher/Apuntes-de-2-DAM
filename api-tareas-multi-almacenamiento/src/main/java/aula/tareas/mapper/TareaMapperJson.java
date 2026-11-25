package aula.tareas.mapper;

import aula.tareas.entity.TareaEntityJson;
import aula.tareas.modelo.Tarea;
import org.springframework.stereotype.Component;

@Component("tareaMapperJson")
public class TareaMapperJson {
    private TareaMapperJson() {}

    public TareaEntityJson toEntity(Tarea tarea) {
        // TODO: Convertir al tipo de destino.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public Tarea toDomain(TareaEntityJson entity) {
        // TODO: Convertir al tipo de destino.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
