package es.codelearnacademy.filelab.config;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class PropertiesConfig {

    private final Path path;

    public PropertiesConfig(Path path) {
        this.path = path;
    }

    public Optional<String> get(String key) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public String getOrDefault(String key, String defaultValue) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public Map<String, String> findAll() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public boolean put(String key, String value) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public boolean remove(String key) {
        throw new UnsupportedOperationException("Función no implementada");
    }
}
