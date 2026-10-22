package es.ejemplo.taller.infra;

import es.ejemplo.taller.modelo.Cliente;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

public abstract class ConexionBD {
    public static String url;

    protected ConexionBD(String url) {
        ConexionBD.url = url;
    }

    public static String normalizarUrl(String url) {
        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            return url;
        }
        String archivo = url.substring("jdbc:sqlite:".length());
        if (archivo.isBlank() || archivo.equals(":memory:") || archivo.startsWith("file:")) {
            return url;
        }
        try {
            Path ruta = Paths.get(archivo);
            if (!ruta.isAbsolute()) {
                // IntelliJ puede arrancar desde la carpeta padre del módulo Maven.
                Path cwd = Paths.get("").toAbsolutePath().normalize();
                Path proyecto = cwd;
                if (!Files.exists(proyecto.resolve("pom.xml")) &&
                        Files.exists(proyecto.resolve("taller-mecanico-alumnado/pom.xml"))) {
                    proyecto = proyecto.resolve("taller-mecanico-alumnado");
                }
                ruta = proyecto.resolve(ruta);
            }
            ruta = ruta.toAbsolutePath().normalize();
            Path parent = ruta.getParent();
            if (parent != null) Files.createDirectories(parent);
            return "jdbc:sqlite:" + ruta;
        } catch (IOException | RuntimeException e) {
            return url;
        }
    }

    public static Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(normalizarUrl(url));
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener conexion con la base de datos", ex);
        }
    }

    /**
     * TODO: verificar el archivo antes de conectar y comprobar las tablas.
     * Devuelve una conexión abierta cuya responsabilidad de cierre tiene el llamador.
     * Si la ruta, el archivo o el esquema no son válidos, lanza SQLException.
     */
    public static Connection abrir(String url) throws SQLException {
        throw new SQLException("Pendiente de implementar por el alumnado");
    }
}
