package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Cliente;
import java.sql.*;
import java.util.*;

public final class ClienteDao extends ConexionBD implements IClienteDao {

    public ClienteDao(String url) {
        super(url);
    }

    @Override
    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        String consulta = "SELECT id, nombre, email, telefono FROM clientes ORDER BY id";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("email"), rs.getString("telefono")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar clientes", e);
        }
        return clientes;
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String consulta = "SELECT id, nombre, email, telefono FROM clientes WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Cliente(rs.getInt("id"), rs.getString("nombre"),
                            rs.getString("email"), rs.getString("telefono")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el cliente " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public int insertar(Cliente entidad) {
        Objects.requireNonNull(entidad, "entidad");
        String consulta = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No se obtuvo el identificador generado");
        } catch (SQLException e) {
            throw new DaoException("Error al insertar el cliente", e);
        }
    }

    @Override
    public int actualizar(Cliente entidad) {
        Objects.requireNonNull(entidad, "entidad");
        if (entidad.getId() == null) {
            throw new IllegalArgumentException("El identificador es obligatorio para actualizar");
        }
        String consulta = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.setObject(4, entidad.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el cliente", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        Objects.requireNonNull(id, "id");
        String consulta = "DELETE FROM clientes WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el cliente " + id, e);
        }
    }
}