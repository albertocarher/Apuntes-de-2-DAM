package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class VehiculoCsvRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;

    public VehiculoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        return vehiculo.matricula();
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        var formato = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        var vehiculos = new ArrayList<Vehiculo>();
        try (var lector = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             var parser = CSVParser.parse(lector, formato)) {
            for (CSVRecord registro : parser) {
                vehiculos.add(new Vehiculo(
                        registro.get("matricula"),
                        registro.get("marca"),
                        registro.get("modelo"),
                        Integer.parseInt(registro.get("anio").trim())));
            }
        }
        return vehiculos;
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        var formato = CSVFormat.DEFAULT.builder()
                .setHeader("matricula", "marca", "modelo", "anio")
                .build();
        try (var escritor = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             var printer = new CSVPrinter(escritor, formato)) {
            for (Vehiculo vehiculo : vehiculos) {
                printer.printRecord(vehiculo.matricula(), vehiculo.marca(), vehiculo.modelo(), vehiculo.anio());
            }
        }
    }
}