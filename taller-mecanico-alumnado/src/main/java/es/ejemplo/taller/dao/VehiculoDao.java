package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class VehiculoDao extends ConexionBD {
    public VehiculoDao(String url) {
        super(url);
    }

    public List<Vehiculo> listar() {
        // TODO: implementar según el contrato del README.
        List<Vehiculo> vehiculos = new ArrayList<>();
        String consulta = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta)){

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vehiculos.add(new Vehiculo(rs.getString("matricula"), rs.getString("marca"), rs.getString("modelo"), rs.getInt("anio"), rs.getInt("id_cliente")));
            }

        } catch (SQLException e) {
            return List.of();

        }
        return vehiculos;
    }
    public Optional<Vehiculo> buscarPorId(String id) {
        // TODO: implementar según el contrato del README.
        String consulta = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(new Vehiculo(id, rs.getString("marca"), rs.getString("modelo"), rs.getInt("anio"), rs.getInt("id_cliente")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    public int insertar(Vehiculo dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null ) {
            return 0;
        }
        String consulta = "INSERT INTO vehiculos (matricula, marca, modelo, anio, id_cliente ) VALUES (?,?,?,?,?)";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, dato.getMatricula());
            ps.setString(2, dato.getMarca());
            ps.setString(3, dato.getModelo());
            ps.setInt(4, dato.getAnio());
            ps.setInt(5,dato.getIdCliente());
            return ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")){
                return 0;
            }
            throw new RuntimeException(e);
        }
    }
    public int actualizar(Vehiculo dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null){
            return 0;
        }
        String consulta = "UPDATE vehiculos SET marca = ?, modelo = ?, anio = ?, id_cliente = ? WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setString(1, dato.getMarca());
            ps.setString(2, dato.getModelo());
            ps.setInt(3, dato.getAnio());
            ps.setInt(4, dato.getIdCliente());
            ps.setString(5, dato.getMatricula());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int eliminar(String id) {
        // TODO: implementar según el contrato del README.
        String consulta = "DELETE FROM vehiculos WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setString(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        // TODO: implementar según el contrato del README.
        List<Vehiculo> vehiculos = new ArrayList<>();
        String consulta = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos WHERE id_cliente = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                vehiculos.add(new Vehiculo(rs.getString("matricula"), rs.getString("marca"), rs.getString("modelo"), rs.getInt("anio"), idCliente));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vehiculos;
    }
    public List<Vehiculo> listarPorMarca(String marca) {
        // TODO: implementar según el contrato del README.
        List<Vehiculo> vehiculos = new ArrayList<>();
        String consulta = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos WHERE marca = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setString(1, marca);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                vehiculos.add(new Vehiculo(rs.getString("matricula"), marca, rs.getString("modelo"), rs.getInt("anio"), rs.getInt("id_cliente")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vehiculos;
    }
}
