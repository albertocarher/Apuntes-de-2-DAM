package es.ejemplo.gestion;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class BaseDatosTest {
    protected String url;
    private Path baseTemporal;

    @BeforeEach
    void prepararBaseDatos() throws Exception {
        baseTemporal = Files.createTempFile("gestion-test-", ".sqlite3");
        Files.copy(Path.of("datos/test.sqlite3"), baseTemporal,
                   StandardCopyOption.REPLACE_EXISTING);
        url = "jdbc:sqlite:" + baseTemporal.toAbsolutePath();
    }

    @AfterEach
    void limpiarBaseDatos() throws Exception {
        Files.deleteIfExists(baseTemporal);
    }
}
