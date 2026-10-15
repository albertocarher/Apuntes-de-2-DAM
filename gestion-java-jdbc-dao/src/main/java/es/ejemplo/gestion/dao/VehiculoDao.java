package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiculoDao {
    private  ConexionBD conexionBD;

    public VehiculoDao(String url) {
        
    }
    

    public List<Vehiculo> listar() {
        return null;
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        return null;
    }

    public boolean insertar(Vehiculo vehiculo) {
        return false;
    }

    public boolean actualizar(Vehiculo vehiculo) {
        return false;
    }

    public boolean eliminar(String matricula) {
        return false;
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        return null;
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        return null;
    }

    public int contar() {
        return 0;
    }

    public Map<String, Integer> contarPorMarca() {
        return null;
    }
}
