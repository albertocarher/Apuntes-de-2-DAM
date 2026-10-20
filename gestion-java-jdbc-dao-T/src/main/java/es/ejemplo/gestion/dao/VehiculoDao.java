package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.model.Vehiculo;
import java.sql.*;
import java.util.*;

public class VehiculoDao extends AbstractDao<Vehiculo> {
    public VehiculoDao(String url) { super(url); }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        // TODO: leer matricula, marca, modelo, anio con rs.getXXX(...)
        try {
            return Optional.of(new Vehiculo(
                    rs.getString("matricula"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("anio")));

        }catch (SQLException | RuntimeException e){
            registrar("mapRsToClass vehiculo",e);
            return Optional.empty();
        }
    }

    public List<Vehiculo> listar() {
        // TODO: seguir el ejemplo de ProductoDao.listar()
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos ORDER BY matricula";
        try (Connection con = obtenerConexion();
        PreparedStatement ps = con.prepareStatement(sql);){
            return consultar(ps);
        }catch (SQLException | RuntimeException e){
            registrar("listar vehiculo",e);
            return List.of();
        }
    }

    public Optional<Vehiculo> buscarPorId(String matricula) {
        // TODO: SELECT WHERE matricula = ?; ps.setString(1, matricula)
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorId vehiculo", e);
            return Optional.empty();
        }
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        // TODO: delegar en buscarPorId(matricula)
        return buscarPorId(matricula);
    }

    public boolean eliminar(String matricula) {
        // TODO: DELETE WHERE matricula = ?
        String sql = "DELETE FROM Vehiculos WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return ejecutar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("eliminar vehiculo", e);
            return false;
        }
    }

    public boolean insertar(Vehiculo v) {
        // TODO: validar e insertar
        if (v == null
                || v.getMatricula() == null || v.getMatricula().isBlank()
                || !v.getMatricula().equals(v.getMatricula().toUpperCase())
                || v.getMarca() == null || v.getMarca().isBlank()
                || v.getModelo() == null || v.getModelo().isBlank()
                || v.getAnio() < 1886) {
            return false;
        }
        String sql = "INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, v.getMatricula());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            return ejecutar(ps);
        }catch (SQLException | RuntimeException e){
            registrar("insertar vehiculo",e);
            return false;
        }
    }

    public boolean actualizar(Vehiculo v) {
        // TODO: validar y actualizar
        if (v == null || v.getMatricula() == null || v.getMatricula().isBlank()) {
            return false;
        }
        // Orden de los parámetros: 1 = marca, 2 = modelo, 3 = anio, 4 = matricula
        String sql = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getMarca());
            ps.setString(2, v.getModelo());
            ps.setInt(3, v.getAnio());
            ps.setString(4, v.getMatricula());
            return ejecutar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("actualizar vehiculo", e);
            return false;
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        // TODO: LIKE sin distinguir mayúsculas
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos "
                + "WHERE LOWER(marca) LIKE LOWER(?) ORDER BY marca, matricula";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, "%"+texto+"%");
            return consultar(ps);
        }catch (SQLException | RuntimeException e){
            registrar("buscarPorMarca vehiculo",e);
            return List.of();
        }
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        // TODO: BETWEEN inclusivo
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE anio >= ? AND anio <= ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, desde);
            ps.setInt(2, hasta);
            return consultar(ps);
        }catch (SQLException | RuntimeException e){
            registrar("buscarPorAnio vehiculo",e);
            return List.of();
        }
    }

    public int contar() {
        // TODO: consultarEntero(ps)
        String sql = "SELECT COUNT(*) FROM Vehiculos";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)){
            return consultarEntero(ps);
        }catch (SQLException | RuntimeException e){
            registrar("contar vehiculo",e);
        }
        return 0;
    }

    public Map<String, Integer> contarPorMarca() {
        // TODO: GROUP BY marca y consultarAgrupado(ps)
        String sql = "SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY marca";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultarAgrupado(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("contarPorMarca vehiculo", e);
            return Map.of();
        }
    }
}
