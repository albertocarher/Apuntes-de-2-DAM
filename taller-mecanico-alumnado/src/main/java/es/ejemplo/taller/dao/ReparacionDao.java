package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Reparacion;
import es.ejemplo.taller.modelo.Vehiculo;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class ReparacionDao extends ConexionBD {

    public ReparacionDao(String url) { super(url); }

    public List<Reparacion> listar() {
        // TODO: implementar según el contrato del README.
        List<Reparacion> reparaciones = new ArrayList<>();
        String consulta = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta)){

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reparaciones.add(new Reparacion(rs.getInt("id_reparacion"), rs.getString("matricula"), rs.getInt("id_mecanico"), rs.getString("descripcion"), rs.getString("fecha"), rs.getBigDecimal("horas_trabajo"),rs.getString("estado")));
            }

        } catch (SQLException e) {
            return List.of();

        }
        return reparaciones;
    }
    public Optional<Reparacion> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones WHERE id_reparacion = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(new Reparacion(id, rs.getString("matricula"), rs.getInt("id_mecanico"), rs.getString("descripcion"), rs.getString("fecha"), rs.getBigDecimal("horas_trabajo"),rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    public int insertar(Reparacion dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null || dato.getEstado().equals("INVALIDA")) {
            return 0;
        }
        String consulta = "INSERT INTO reparaciones (matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado) VALUES (?,?,?,?,?,?)";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6,dato.getEstado());
            return ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")){
                return 0;
            }
            throw new RuntimeException(e);
        }
    }
    public int actualizar(Reparacion dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null){
            return 0;
        }
        String consulta = "UPDATE reparaciones SET matricula = ?, id_mecanico = ?, descripcion = ?, fecha = ?, horas_trabajo = ?, estado = ? WHERE id_reparacion = ?";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6,dato.getEstado());
            ps.setInt(7, dato.getIdReparacion());
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "DELETE FROM reparaciones WHERE id_reparacion = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Reparacion> buscarPorVehiculo(String valor) {
        // TODO: implementar según el contrato del README.
        List<Reparacion> reparaciones = new ArrayList<>();
        String consulta = "SELECT id_reparacion, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setString(1, valor);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reparaciones.add(new Reparacion(rs.getInt("id_reparacion"), valor, rs.getInt("id_mecanico"), rs.getString("descripcion"), rs.getString("fecha"), rs.getBigDecimal("horas_trabajo"),rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reparaciones;
    }
    public List<Reparacion> buscarPorMecanico(Integer valor) {
        // TODO: implementar según el contrato del README.
        List<Reparacion> reparaciones = new ArrayList<>();
        String consulta = "SELECT id_reparacion, matricula,id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones WHERE id_mecanico = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setInt(1, valor);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reparaciones.add(new Reparacion(rs.getInt("id_reparacion"), rs.getString("matricula"), valor, rs.getString("descripcion"), rs.getString("fecha"), rs.getBigDecimal("horas_trabajo"),rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reparaciones;
    }
    public List<Reparacion> listarPendientes() {
        // TODO: implementar según el contrato del README.
        List<Reparacion> reparaciones = new ArrayList<>();
        String consulta = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones WHERE estado LIKE 'PENDIENTE'";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta)){

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reparaciones.add(new Reparacion(rs.getInt("id_reparacion"), rs.getString("matricula"), rs.getInt("id_mecanico"), rs.getString("descripcion"), rs.getString("fecha"), rs.getBigDecimal("horas_trabajo"),rs.getString("estado")));
            }

        } catch (SQLException e) {
            return List.of();

        }
        return reparaciones;
    }
}
