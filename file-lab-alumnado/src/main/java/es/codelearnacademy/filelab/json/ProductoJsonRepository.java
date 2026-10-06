package es.codelearnacademy.filelab.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ProductoJsonRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;
    private final ObjectMapper mapper;

    public ProductoJsonRepository(Path path) {
        this(path, new ObjectMapper());
    }

    public ProductoJsonRepository(Path path, ObjectMapper mapper) {
        this.path = path;
        this.mapper = mapper;
    }

    @Override
    protected Long getId(Producto producto) {
        return producto.id();
    }

    @Override
    protected List<Producto> readAll() throws IOException {
        return mapper.readValue(path.toFile(), new TypeReference<List<Producto>>() {
        });
    }

    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        mapper.writeValue(path.toFile(), productos);
    }
}