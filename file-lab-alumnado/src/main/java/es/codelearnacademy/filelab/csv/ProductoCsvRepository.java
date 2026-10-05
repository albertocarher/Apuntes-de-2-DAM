package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ProductoCsvRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;

    public ProductoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected Long getId(Producto producto) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    protected List<Producto> readAll() throws IOException {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        throw new UnsupportedOperationException("Función no implementada");
    }
}
