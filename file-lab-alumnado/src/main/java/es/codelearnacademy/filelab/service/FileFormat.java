package es.codelearnacademy.filelab.service;

public enum FileFormat {
    CSV,
    JSON,
    XML;

    public static FileFormat from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("El formato no puede ser null");
        }
        var texto = value.trim();
        for (FileFormat formato : values()) {
            if (formato.name().equalsIgnoreCase(texto)) {
                return formato;
            }
        }
        throw new IllegalArgumentException("Formato no soportado: " + value);
    }
}