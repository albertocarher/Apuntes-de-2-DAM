package es.codelearnacademy.filelab.io;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TextFileServiceTest {

    @TempDir
    Path tempDir;

    private final TextFileService service = new TextFileService();

    @Test
    void escribeYLeeUtf8() {
        Path archivo = tempDir.resolve("texto.txt");
        assertTrue(service.escribir(archivo, "España, piñón, programación"));
        assertEquals("España, piñón, programación", service.leer(archivo));
    }

    @Test
    void escribeYLeeLineas() {
        Path archivo = tempDir.resolve("lineas.txt");
        List<String> lineas = List.of("uno", "dos", "tres");
        assertTrue(service.escribirLineas(archivo, lineas));
        assertEquals(lineas, service.leerLineas(archivo));
    }

    @Test
    void lecturaInexistenteVacia() {
        assertEquals("", service.leer(tempDir.resolve("no-existe.txt")));
    }

    @Test
    void lineasInexistentesVacias() {
        assertEquals(List.of(), service.leerLineas(tempDir.resolve("no-existe.txt")));
    }

    @Test
    void anexaContenido() {
        Path archivo = tempDir.resolve("texto.txt");
        assertTrue(service.escribir(archivo, "uno"));
        assertTrue(service.anexar(archivo, "dos"));
        assertEquals("unodos", service.leer(archivo));
    }
}
