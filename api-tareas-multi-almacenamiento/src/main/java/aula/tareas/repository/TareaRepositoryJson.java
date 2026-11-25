package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("tareaRepositoryJson")
public class TareaRepositoryJson implements ITareaRepository {
    private final Path path;
    private final JsonMapper mapper;

    @Autowired
    public TareaRepositoryJson(JsonMapper mapper) {
        this(Path.of("data", "tarea.json"), mapper);
    }

    public TareaRepositoryJson(Path path, JsonMapper mapper) {
        this.path = path;
        this.mapper = mapper;
    }

    @Override
    public synchronized List<Tarea> findAll() {
        // TODO: Recuperar las tareas de este almacenamiento.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized Optional<Tarea> findById(long id) {
        // TODO: Buscar una tarea por identificador.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized Tarea create(Tarea tarea) {
        // TODO: Guardar la tarea nueva y devolverla con su identificador.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized Optional<Tarea> update(Tarea tarea) {
        // TODO: Actualizar una tarea existente.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public synchronized boolean delete(long id) {
        // TODO: Eliminar la tarea y comunicar si existía.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    private void saveAll(List<Tarea> tareas) {
        // TODO: Escribir la lista completa en el fichero.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
