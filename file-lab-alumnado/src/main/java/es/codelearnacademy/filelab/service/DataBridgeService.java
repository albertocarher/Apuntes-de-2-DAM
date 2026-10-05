package es.codelearnacademy.filelab.service;

import java.nio.file.Path;

public class DataBridgeService {

    private final RepositoryFactory repositoryFactory;

    public DataBridgeService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public int convert(FileFormat origenFormato, Path origen,
                       FileFormat destinoFormato, Path destino) {
        throw new UnsupportedOperationException("Función no implementada");
    }
}
