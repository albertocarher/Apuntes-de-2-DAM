package es.ejemplo.gestion;

import es.ejemplo.gestion.config.ConexionBD;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConexionBDTest extends BaseDatosTest {
    @Test void conectaConBaseDePruebas() throws Exception {
        try (var con = new ConexionBD(url).obtenerConexion()) {
            assertNotNull(con);
            assertFalse(con.isClosed());
            assertTrue(con.isValid(2));
        }
    }
    @Test void falloControladoSinExcepcion() {
        // Una URI read-only a una base inexistente no puede crear la base.
        String uri = "jdbc:sqlite:file:/ruta-inexistente-para-tests-jdbc-no-db/ausente.sqlite3?mode=ro";
        assertNull(new ConexionBD(uri).obtenerConexion());
    }
}
