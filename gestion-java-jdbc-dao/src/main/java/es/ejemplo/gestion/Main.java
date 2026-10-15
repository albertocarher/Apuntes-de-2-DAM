package es.ejemplo.gestion;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.ui.MenuConsola;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:datos/gestion.sqlite3";
        try (Connection conexion = new ConexionBD(url).obtenerConexion()) {
            System.out.println("Conectado a datos/gestion.sqlite3");
        } catch (SQLException | AplicationException e) {
            System.err.println("No se pudo abrir la base de datos: " + e.getMessage());
            return;
        }
        new MenuConsola(url).iniciar();
    }
}
