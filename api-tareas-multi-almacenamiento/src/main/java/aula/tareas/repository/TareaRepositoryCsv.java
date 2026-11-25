package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.apache.commons.csv.CSVFormat;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("tareaRepositoryCsv")
public class TareaRepositoryCsv implements ITareaRepository {
    private final Path path;

    public TareaRepositoryCsv() {
        this(Path.of("data", "tarea.csv"));
    }

    public TareaRepositoryCsv(Path path) {
        this.path = path;
    }

    private static final CSVFormat ENTRADA = CSVFormat.DEFAULT.builder()
            .setHeader().setSkipHeaderRecord(true).get();
    private static final CSVFormat SALIDA = CSVFormat.DEFAULT.builder()
            .setHeader("id", "titulo").get();

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
