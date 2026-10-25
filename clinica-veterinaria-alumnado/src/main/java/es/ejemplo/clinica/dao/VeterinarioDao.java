package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Veterinario;
import java.sql.*;
import java.util.*;

public final class VeterinarioDao extends ConexionBD implements IVeterinarioDao {

    public VeterinarioDao(String url) {
        super(url);
    }

    @Override
    public List<Veterinario> listar() {
        List<Veterinario> lista = new ArrayList<>();
        String consulta = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios ORDER BY id";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Veterinario(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("especialidad"), rs.getBigDecimal("tarifa_hora")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar veterinarios", e);
        }
        return lista;
    }

    @Override
    public Optional<Veterinario> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String consulta = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Veterinario(rs.getInt("id"), rs.getString("nombre"),
                            rs.getString("especialidad"), rs.getBigDecimal("tarifa_hora")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el veterinario " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public int insertar(Veterinario entidad) {
        Objects.requireNonNull(entidad, "entidad");
        String consulta = "INSERT INTO veterinarios (nombre, especialidad, tarifa_hora) VALUES (?, ?, ?)";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecialidad());
            ps.setBigDecimal(3, entidad.getTarifaHora());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No se obtuvo el identificador generado");
        } catch (SQLException e) {
            throw new DaoException("Error al insertar el veterinario", e);
        }
    }

    @Override
    public int actualizar(Veterinario entidad) {
        Objects.requireNonNull(entidad, "entidad");
        if (entidad.getId() == null) {
            throw new IllegalArgumentException("El identificador es obligatorio para actualizar");
        }
        String consulta = "UPDATE veterinarios SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecialidad());
            ps.setBigDecimal(3, entidad.getTarifaHora());
            ps.setObject(4, entidad.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el veterinario", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        Objects.requireNonNull(id, "id");
        String consulta = "DELETE FROM veterinarios WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el veterinario " + id, e);
        }
    }

    @Override
    public List<Veterinario> buscarPorEspecialidad(String especialidad) {
        List<Veterinario> lista = new ArrayList<>();
        String consulta = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE especialidad = ? ORDER BY tarifa_hora, id";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setString(1, especialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Veterinario(rs.getInt("id"), rs.getString("nombre"),
                            rs.getString("especialidad"), rs.getBigDecimal("tarifa_hora")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar veterinarios por especialidad", e);
        }
        return lista;
    }
}