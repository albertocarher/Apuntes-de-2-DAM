package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.model.Vehiculo;
import java.sql.*;
import java.util.*;

public class VehiculoDao extends AbstractDao<Vehiculo> {
    public VehiculoDao(String url) { super(url); }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        // TODO: leer matricula, marca, modelo, anio con rs.getXXX(...)
        // Capturar SQLException y RuntimeException aquí; nunca declarar throws.
        return Optional.empty();
    }

    public List<Vehiculo> listar() {
        // TODO: seguir el ejemplo de ProductoDao.listar()
        return List.of();
    }

    public Optional<Vehiculo> buscarPorId(String matricula) {
        // TODO: SELECT WHERE matricula = ?; ps.setString(1, matricula)
        return Optional.empty();
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        // TODO: delegar en buscarPorId(matricula)
        return Optional.empty();
    }

    public boolean eliminar(String matricula) {
        // TODO: DELETE WHERE matricula = ?
        return false;
    }

    public boolean insertar(Vehiculo v) {
        // TODO: validar e insertar
        return false;
    }

    public boolean actualizar(Vehiculo v) {
        // TODO: validar y actualizar
        return false;
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        // TODO: LIKE sin distinguir mayúsculas
        return List.of();
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        // TODO: BETWEEN inclusivo
        return List.of();
    }

    public int contar() {
        // TODO: consultarEntero(ps)
        return 0;
    }

    public Map<String, Integer> contarPorMarca() {
        // TODO: GROUP BY marca y consultarAgrupado(ps)
        return Map.of();
    }
}
