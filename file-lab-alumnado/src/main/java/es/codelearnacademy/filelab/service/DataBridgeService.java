package es.codelearnacademy.filelab.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataBridgeService {

    private final RepositoryFactory repositoryFactory;

    public DataBridgeService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public int convert(FileFormat origenFormato, Path origen,
                       FileFormat destinoFormato, Path destino) {
        var repositorioOrigen = repositoryFactory.create(origenFormato, origen);
        var productos = repositorioOrigen.findAll();
        try {
            var carpeta = destino.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.deleteIfExists(destino);
        } catch (IOException e) {
            return 0;
        }
        var repositorioDestino = repositoryFactory.create(destinoFormato, destino);
        int convertidos = 0;
        for (var producto : productos) {
            if (repositorioDestino.create(producto)) {
                convertidos++;
            }
        }
        return convertidos;
    }
}