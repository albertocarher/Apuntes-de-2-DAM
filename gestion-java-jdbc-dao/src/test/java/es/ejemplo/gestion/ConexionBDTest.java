package es.ejemplo.gestion;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ConexionBDTest extends BaseDatosTest {
    @Test void conectaConBaseDePruebas() throws Exception {
        try (var con = new ConexionBD(url).obtenerConexion()) {
            assertFalse(con.isClosed());
            assertTrue(con.isValid(2));
        }
    }
    @Test void fallaSiNoExisteLaBase() {
        String inexistente = "jdbc:sqlite:" + Path.of("datos/no-existe-esta-base.sqlite3").toAbsolutePath();
        assertThrows(AplicationException.class, () -> new ConexionBD(inexistente).obtenerConexion());
    }
}
