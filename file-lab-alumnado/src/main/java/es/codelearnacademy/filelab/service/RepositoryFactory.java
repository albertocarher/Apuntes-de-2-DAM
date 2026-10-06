package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.csv.ProductoCsvRepository;
import es.codelearnacademy.filelab.json.ProductoJsonRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import es.codelearnacademy.filelab.xml.ProductoXmlRepository;
import java.nio.file.Path;

public class RepositoryFactory {

    public IProductoRepository create(FileFormat format, Path path) {
        if (format == null) {
            throw new IllegalArgumentException("El formato no puede ser null");
        }
        return switch (format) {
            case CSV -> new ProductoCsvRepository(path);
            case JSON -> new ProductoJsonRepository(path);
            case XML -> new ProductoXmlRepository(path);
        };
    }
}