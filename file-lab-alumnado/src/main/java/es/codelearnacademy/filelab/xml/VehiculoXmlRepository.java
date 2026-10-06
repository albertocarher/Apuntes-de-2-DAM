package es.codelearnacademy.filelab.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VehiculoXmlRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;
    private final XmlMapper mapper = new XmlMapper();

    public VehiculoXmlRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        return vehiculo.matricula();
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        var documento = mapper.readValue(path.toFile(), DocumentoVehiculos.class);
        var vehiculos = documento.getVehiculos();
        return vehiculos == null ? List.of() : vehiculos;
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        mapper.writeValue(path.toFile(), new DocumentoVehiculos(vehiculos));
    }
}