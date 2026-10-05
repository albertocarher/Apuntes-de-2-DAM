package es.codelearnacademy.filelab.service;

import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.csv.ProductoCsvRepository;
import es.codelearnacademy.filelab.json.ProductoJsonRepository;
import es.codelearnacademy.filelab.xml.ProductoXmlRepository;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RepositoryFactoryTest {

    private final RepositoryFactory factory = new RepositoryFactory();

    @Test
    void creaRepositorioCsv() {
        assertInstanceOf(ProductoCsvRepository.class,
                factory.create(FileFormat.CSV, Path.of("data/productos.csv")));
    }

    @Test
    void creaRepositorioJson() {
        assertInstanceOf(ProductoJsonRepository.class,
                factory.create(FileFormat.JSON, Path.of("data/productos.json")));
    }

    @Test
    void creaRepositorioXml() {
        assertInstanceOf(ProductoXmlRepository.class,
                factory.create(FileFormat.XML, Path.of("data/productos.xml")));
    }
}
