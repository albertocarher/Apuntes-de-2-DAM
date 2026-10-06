package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
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

public class ProductoCsvRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;

    public ProductoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected Long getId(Producto producto) {
        return producto.id();
    }

    @Override
    protected List<Producto> readAll() throws IOException {
        var formato = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        var productos = new ArrayList<Producto>();
        try (var lector = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             var parser = CSVParser.parse(lector, formato)) {
            for (CSVRecord registro : parser) {
                productos.add(new Producto(
                        Long.parseLong(registro.get("id").trim()),
                        registro.get("nombre"),
                        Double.parseDouble(registro.get("precio").trim()),
                        Integer.parseInt(registro.get("stock").trim())));
            }
        }
        return productos;
    }

    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        var formato = CSVFormat.DEFAULT.builder()
                .setHeader("id", "nombre", "precio", "stock")
                .build();
        try (var escritor = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             var printer = new CSVPrinter(escritor, formato)) {
            for (Producto producto : productos) {
                printer.printRecord(producto.id(), producto.nombre(), producto.precio(), producto.stock());
            }
        }
    }
}