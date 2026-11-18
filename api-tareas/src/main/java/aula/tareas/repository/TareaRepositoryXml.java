package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("repositoryXml")
public class TareaRepositoryXml implements ITareaRepository{
    private final AtomicLong secuencia = new AtomicLong();
    private Path path;
    private final XmlMapper mapper = new XmlMapper();
    private List<Tarea> lista;

    public TareaRepositoryXml() {
        this("data/tarea.xml");
    }

    public TareaRepositoryXml(String url) {
        path = Path.of(url);
        if (!Files.exists(path)) {
            try {
                Path parent = path.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        lista = getAll();
        secuencia.set(lista.stream().mapToLong(Tarea::id).max().orElse(0));
    }

    @Override
    public Tarea create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new Tarea(id, titulo.strip());
        lista.add(tarea);
        saveAll(lista);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        boolean eliminada = lista.removeIf(tarea -> tarea.id() == identificador);
        if (eliminada) {
            saveAll(lista);
        }
        return eliminada;
    }

    @Override
    public List<Tarea> findAll() {
        return List.copyOf(lista);
    }

    @Override
    public Tarea findById(long identificador) {
        return lista.stream()
                .filter(tarea -> tarea.id() == identificador)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Tarea update(Tarea tarea) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == tarea.id()) {
                lista.set(i, tarea);
                saveAll(lista);
                return tarea;
            }
        }
        return null;
    }

    private void saveAll(List<Tarea> items) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), items);
        }catch (Exception exception) {
        }
    }

    public List<Tarea> getAll() {
        try {
            return new ArrayList<>(mapper.readValue(
                    path.toFile(), new TypeReference<List<Tarea>>() {}));
        }catch (Exception exception) {
            return new ArrayList<>();
        }
    }
}