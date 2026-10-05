package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VehiculoCsvRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;

    public VehiculoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        throw new UnsupportedOperationException("Función no implementada");
    }
}
