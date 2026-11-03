package es.ejemplo.taller.infra;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConexionBD {
    private ConexionBD() {}

    /**
     * TODO: verificar el archivo antes de conectar y comprobar las tablas.
     * Devuelve una conexión abierta cuya responsabilidad de cierre tiene el llamador.
     * Si la ruta, el archivo o el esquema no son válidos, lanza SQLException.
     */
    public static Connection abrir(String url) throws SQLException {
        throw new SQLException("Pendiente de implementar por el alumnado");
    }
}
