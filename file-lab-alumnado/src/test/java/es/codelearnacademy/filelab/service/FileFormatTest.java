package es.codelearnacademy.filelab.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FileFormatTest {

    @Test
    void reconoceCsv() {
        assertEquals(FileFormat.CSV, FileFormat.from("csv"));
    }

    @Test
    void reconoceJsonSinImportarMayusculas() {
        assertEquals(FileFormat.JSON, FileFormat.from("JsOn"));
    }

    @Test
    void rechazaFormatoDesconocido() {
        assertThrows(IllegalArgumentException.class, () -> FileFormat.from("yaml"));
    }
}
