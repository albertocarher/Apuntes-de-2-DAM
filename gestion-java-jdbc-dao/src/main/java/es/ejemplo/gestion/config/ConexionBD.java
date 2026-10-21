package es.ejemplo.gestion.config;

import es.ejemplo.gestion.exception.AplicationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Abre una conexión en la base SQLite indicada por el constructor. */
public  class ConexionBD {
    private  String url;

    public ConexionBD(String url) {
        this.url = url;
        Path path = Path.of(url);

        if(!Files.exists(path)) {
            try{
                Files.createFile(path);
            }catch(IOException e){
                throw new AplicationException(e.getMessage());
            }
        }
    }

    public Connection obtenerConexion() {
         try {
        return DriverManager.getConnection(url);
    } catch (SQLException e) {
        throw new AplicationException("Error conectando con SQLite", e);
    }
    }
}
