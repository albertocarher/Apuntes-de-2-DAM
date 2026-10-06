package es.codelearnacademy.filelab.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class PropertiesConfig {

    private final Path path;

    public PropertiesConfig(Path path) {
        this.path = path;
    }

    public Optional<String> get(String key) {
        if (key == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(cargar().getProperty(key));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public String getOrDefault(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    public Map<String, String> findAll() {
        try {
            var propiedades = cargar();
            var resultado = new HashMap<String, String>();
            for (String clave : propiedades.stringPropertyNames()) {
                resultado.put(clave, propiedades.getProperty(clave));
            }
            return resultado;
        } catch (IOException e) {
            return Map.of();
        }
    }

    public boolean put(String key, String value) {
        if (key == null || value == null) {
            return false;
        }
        try {
            var propiedades = Files.exists(path) ? cargar() : new Properties();
            propiedades.setProperty(key, value);
            guardar(propiedades);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean remove(String key) {
        if (key == null) {
            return false;
        }
        try {
            var propiedades = cargar();
            propiedades.remove(key);
            guardar(propiedades);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Properties cargar() throws IOException {
        var propiedades = new Properties();
        try (var lector = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            propiedades.load(lector);
        }
        return propiedades;
    }

    private void guardar(Properties propiedades) throws IOException {
        try (var escritor = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            propiedades.store(escritor, null);
        }
    }
}