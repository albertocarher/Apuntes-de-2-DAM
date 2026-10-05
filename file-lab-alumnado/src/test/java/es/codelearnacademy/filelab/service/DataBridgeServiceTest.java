package es.codelearnacademy.filelab.service;

import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.support.FixtureSupport;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DataBridgeServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void convierteCsvAJson() throws Exception {
        Path origen = FixtureSupport.copiar("productos.csv", tempDir.resolve("productos.csv"));
        Path destino = tempDir.resolve("productos.json");

        var service = new DataBridgeService(new RepositoryFactory());

        assertEquals(4, service.convert(FileFormat.CSV, origen, FileFormat.JSON, destino));
        assertTrue(java.nio.file.Files.exists(destino));
    }
}
