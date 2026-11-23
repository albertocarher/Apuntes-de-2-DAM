package aula.tareas.mapper;

import aula.tareas.api.TareaRequest;
import aula.tareas.entity.TareaEntityJdbc;

public class TareaMapperJdbc {

    static public TareaRequest toDomain(TareaEntityJdbc entityJdbc) {
        return null;
    }

    static public TareaEntityJdbc toJdbc(TareaRequest tareaRequest){
        return null;
    }

    static public TareaEntityJdbc toEntity(TareaRequest tareaRequest){
        return new TareaEntityJdbc(tareaRequest.id(), tareaRequest.titulo());
    }
}
