package es.codelearnacademy.filelab.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VehiculoJsonRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();

    public VehiculoJsonRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        return vehiculo.matricula();
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        return mapper.readValue(path.toFile(), new TypeReference<List<Vehiculo>>() {
        });
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        mapper.writeValue(path.toFile(), vehiculos);
    }
}