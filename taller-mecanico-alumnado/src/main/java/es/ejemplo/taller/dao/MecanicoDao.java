package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.modelo.Mecanico;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class MecanicoDao extends ConexionBD {
    public MecanicoDao(String url) {
        super(url);
    }
    
    public List<Mecanico> listar() {
        // TODO: implementar según el contrato del README.
        List<Mecanico> mecanicos = new ArrayList<>();
        String consulta = "SELECT id_mecanico, nombre, especialidad, tarifa_Hora FROM mecanicos";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta)){

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mecanicos.add(new Mecanico(rs.getInt("id_mecanico"), rs.getString("nombre"), rs.getString("especialidad"), rs.getBigDecimal("tarifa_hora")));
            }

        } catch (SQLException e) {
            return List.of();
        }
        return mecanicos;
    }

    public Optional<Mecanico> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM mecanicos WHERE id_mecanico = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(new Mecanico(id, rs.getString("nombre"), rs.getString("especialidad"), rs.getBigDecimal("tarifa_hora")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public int insertar(Mecanico dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null || dato.getTarifaHora() == null || dato.getTarifaHora().compareTo(BigDecimal.ZERO) < 0) {
            return 0;
        }
        String consulta = "INSERT INTO mecanicos (nombre, especialidad, tarifa_hora ) VALUES (?, ?, ?)";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")){
                return 0;
            }
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int actualizar(Mecanico dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null){
            return 0;
        }
        String consulta = "UPDATE mecanicos SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id_mecanico = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            ps.setInt(4,dato.getIdMecanico());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "DELETE FROM mecanicos WHERE id_mecanico = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        // TODO: implementar según el contrato del README.
        List<Mecanico> mecanicos = new ArrayList<>();
        String consulta = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM mecanicos WHERE especialidad = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setString(1, especialidad);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                mecanicos.add(new Mecanico(rs.getInt("id_mecanico"),rs.getString("nombre"),especialidad,rs.getBigDecimal("tarifa_hora")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mecanicos;
    }
}
