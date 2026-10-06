package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.config.PropertiesConfig;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class ConfiguredDataBridge {

    private final PropertiesConfig config;
    private final DataBridgeService bridge;

    public ConfiguredDataBridge(PropertiesConfig config, DataBridgeService bridge) {
        this.config = config;
        this.bridge = bridge;
    }

    public int execute() {
        var formatoEntrada = config.get("input.format");
        var archivoEntrada = config.get("input.file");
        var formatoSalida = config.get("output.format");
        var archivoSalida = config.get("output.file");
        if (formatoEntrada.isEmpty() || archivoEntrada.isEmpty()
                || formatoSalida.isEmpty() || archivoSalida.isEmpty()) {
            return 0;
        }
        try {
            return bridge.convert(
                    FileFormat.from(formatoEntrada.get()),
                    Path.of(archivoEntrada.get()),
                    FileFormat.from(formatoSalida.get()),
                    Path.of(archivoSalida.get()));
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
}