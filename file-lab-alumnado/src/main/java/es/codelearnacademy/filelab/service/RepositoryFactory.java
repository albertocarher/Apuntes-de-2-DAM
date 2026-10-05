package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.nio.file.Path;

public class RepositoryFactory {

    public IProductoRepository create(FileFormat format, Path path) {
        throw new UnsupportedOperationException("Función no implementada");
    }
}
