package es.codelearnacademy.filelab.io;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FilesServiceTest {

    @TempDir
    Path tempDir;

    private final FilesService service = new FilesService();

    @Test
    void creaDirectorio() {
        Path directorio = tempDir.resolve("uno");
        assertTrue(service.crearDirectorio(directorio).isPresent());
        assertTrue(Files.isDirectory(directorio));
    }

    @Test
    void creaDirectoriosAnidados() {
        Path directorio = tempDir.resolve("uno/dos/tres");
        assertTrue(service.crearDirectorios(directorio).isPresent());
        assertTrue(Files.isDirectory(directorio));
    }

    @Test
    void creaArchivo() {
        Path archivo = tempDir.resolve("datos.txt");
        assertTrue(service.crearArchivo(archivo).isPresent());
        assertTrue(Files.exists(archivo));
    }

    @Test
    void copiaArchivo() throws Exception {
        Path origen = Files.writeString(tempDir.resolve("origen.txt"), "hola");
        Path destino = tempDir.resolve("copia.txt");
        assertTrue(service.copiar(origen, destino).isPresent());
        assertEquals("hola", Files.readString(destino));
    }

    @Test
    void mueveArchivo() throws Exception {
        Path origen = Files.writeString(tempDir.resolve("origen.txt"), "hola");
        Path destino = tempDir.resolve("destino.txt");
        assertTrue(service.mover(origen, destino).isPresent());
        assertFalse(Files.exists(origen));
        assertTrue(Files.exists(destino));
    }

    @Test
    void eliminaArchivo() throws Exception {
        Path archivo = Files.createFile(tempDir.resolve("datos.txt"));
        assertTrue(service.eliminar(archivo));
        assertFalse(Files.exists(archivo));
    }

    @Test
    void calculaTamanio() throws Exception {
        Path archivo = Files.writeString(tempDir.resolve("datos.txt"), "12345");
        assertEquals(5L, service.tamanio(archivo).orElseThrow());
    }

    @Test
    void tamanioInexistenteVacio() {
        assertTrue(service.tamanio(tempDir.resolve("no-existe.txt")).isEmpty());
    }
}
