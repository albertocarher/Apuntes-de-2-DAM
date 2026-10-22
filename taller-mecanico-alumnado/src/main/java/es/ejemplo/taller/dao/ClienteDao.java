package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao extends ConexionBD {
    public ClienteDao(String url) {
        super(url);
        }

    public List<Cliente> listar() {
        // TODO: implementar según el contrato del README.
        List<Cliente> clientes = new ArrayList<>();
        String consulta = "SELECT id_cliente, nombre, email, telefono FROM clientes";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta)){

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"), rs.getString("email"), rs.getString("telefono")));
            }

        } catch (SQLException e) {
            return List.of();
        }
        return clientes;
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "SELECT id_cliente, nombre, email, telefono FROM clientes WHERE id_cliente = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(new Cliente(id, rs.getString("nombre"), rs.getString("email"), rs.getString("telefono")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public int insertar(Cliente dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null){
            return 0;
        }
        String consulta = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";
        try(Connection con = obtenerConexion();
        PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)){
        ps.setString(1, dato.getNombre());
        ps.setString(2, dato.getEmail());
        ps.setString(3, dato.getTelefono());
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

    public int actualizar(Cliente dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null){
            return 0;
        }
        String consulta = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id_cliente = ?";
        try (Connection con = obtenerConexion();
        PreparedStatement ps = con.prepareStatement(consulta)){
        ps.setString(1, dato.getNombre());
        ps.setString(2, dato.getEmail());
        ps.setString(3, dato.getTelefono());
        ps.setInt(4,dato.getIdCliente());
        return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection con = obtenerConexion();
        PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
