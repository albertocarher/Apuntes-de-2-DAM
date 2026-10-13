package es.codelearnacademy.filelab.config;
import java.nio.file.*;
import java.util.*;
public class PropertiesConfig {
    private final Path path;
    public PropertiesConfig(Path path) {
        this.path=path;
    }
    public Optional<String> get(String key) {
        throw new UnsupportedOperationException("TODO: get");
    }
    public String getOrDefault(String key,String defaultValue) {
        throw new UnsupportedOperationException("TODO: getOrDefault");
    }
    public Map<String,String> findAll() {
        throw new UnsupportedOperationException("TODO: findAll");
    }
    public boolean put(String key,String value) {
        throw new UnsupportedOperationException("TODO: put");
    }
    public boolean remove(String key) {
        throw new UnsupportedOperationException("TODO: remove");
    }
}
