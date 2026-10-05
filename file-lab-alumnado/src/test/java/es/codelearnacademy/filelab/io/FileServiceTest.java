package es.codelearnacademy.filelab.io;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileServiceTest {

    @TempDir
    Path tempDir;

    private final FileService service = new FileService();

    @Test
    void detectaArchivoExistente() throws Exception {
        Path archivo = Files.createFile(tempDir.resolve("datos.txt"));
        assertTrue(service.existe(archivo.toFile()));
    }

    @Test
    void detectaArchivo() throws Exception {
        File archivo = Files.createFile(tempDir.resolve("datos.txt")).toFile();
        assertTrue(service.esArchivo(archivo));
    }

    @Test
    void detectaDirectorio() {
        assertTrue(service.esDirectorio(tempDir.toFile()));
    }

    @Test
    void obtieneNombre() {
        File archivo = tempDir.resolve("datos.txt").toFile();
        assertEquals("datos.txt", service.nombre(archivo));
    }

    @Test
    void convierteAPath() {
        File archivo = tempDir.resolve("datos.txt").toFile();
        assertEquals(archivo.toPath(), service.convertirAPath(archivo));
    }

    @Test
    void convierteAFile() {
        Path archivo = tempDir.resolve("datos.txt");
        assertEquals(archivo.toFile(), service.convertirAFile(archivo));
    }
}
