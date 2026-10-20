package es.ejemplo.gestion.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Abre conexiones SQLite sin cerrarlas antes de devolverlas. */
public class ConexionBD {
    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());
    private final String url;

    public ConexionBD(String url) { this.url = url; }

    /** Crea el directorio de una ruta SQLite normal y devuelve su URL absoluta.
     *  No modifica las URLs especiales (por ejemplo, jdbc:sqlite::memory: o file: URI).
     */
    public static String normalizarUrl(String url) {
        if (url == null || !url.startsWith("jdbc:sqlite:")) return url;
        String archivo = url.substring("jdbc:sqlite:".length());
        if (archivo.isBlank() || archivo.equals(":memory:") || archivo.startsWith("file:")) return url;
        try {
            Path ruta = Paths.get(archivo);
            if (!ruta.isAbsolute()) {
                // IntelliJ puede arrancar desde la carpeta padre del módulo Maven.
                Path cwd = Paths.get("").toAbsolutePath().normalize();
                Path proyecto = cwd;
                if (!Files.exists(proyecto.resolve("pom.xml")) &&
                    Files.exists(proyecto.resolve("gestion-java-jdbc-dao/pom.xml"))) {
                    proyecto = proyecto.resolve("gestion-java-jdbc-dao");
                }
                ruta = proyecto.resolve(ruta);
            }
            ruta = ruta.toAbsolutePath().normalize();
            Path parent = ruta.getParent();
            if (parent != null) Files.createDirectories(parent);
            return "jdbc:sqlite:" + ruta;
        } catch (IOException | RuntimeException e) {
            LOG.log(Level.WARNING, "No se pudo preparar el directorio SQLite", e);
            return url;
        }
    }

    /** Nunca uses try-with-resources aquí: el DAO es dueño de la Connection. */
    public Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(normalizarUrl(url));
        } catch (SQLException | RuntimeException e) {
            LOG.log(Level.WARNING, "No se pudo abrir conexión SQLite: " + url, e);
            return null;
        }
    }
}
