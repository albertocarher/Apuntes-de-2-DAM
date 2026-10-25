package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Mascota;
import java.sql.*;
import java.util.*;

public final class MascotaDao extends ConexionBD implements IMascotaDao {

    public MascotaDao(String url) {
        super(url);
    }

    @Override
    public List<Mascota> listar() {
        List<Mascota> lista = new ArrayList<>();
        String consulta = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas ORDER BY id";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Mascota(rs.getInt("id"), rs.getString("nombre"), rs.getString("especie"),
                        rs.getString("raza"), rs.getString("fecha_nacimiento"), rs.getInt("id_cliente")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar mascotas", e);
        }
        return lista;
    }

    @Override
    public Optional<Mascota> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String consulta = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Mascota(rs.getInt("id"), rs.getString("nombre"), rs.getString("especie"),
                            rs.getString("raza"), rs.getString("fecha_nacimiento"), rs.getInt("id_cliente")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la mascota " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public int insertar(Mascota entidad) {
        Objects.requireNonNull(entidad, "entidad");
        String consulta = "INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, id_cliente) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecie());
            ps.setString(3, entidad.getRaza());
            ps.setString(4, entidad.getFechaNacimiento());
            ps.setObject(5, entidad.getIdCliente());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No se obtuvo el identificador generado");
        } catch (SQLException e) {
            throw new DaoException("Error al insertar la mascota", e);
        }
    }

    @Override
    public int actualizar(Mascota entidad) {
        Objects.requireNonNull(entidad, "entidad");
        if (entidad.getId() == null) {
            throw new IllegalArgumentException("El identificador es obligatorio para actualizar");
        }
        String consulta = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ?, id_cliente = ? WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecie());
            ps.setString(3, entidad.getRaza());
            ps.setString(4, entidad.getFechaNacimiento());
            ps.setObject(5, entidad.getIdCliente());
            ps.setObject(6, entidad.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        Objects.requireNonNull(id, "id");
        String consulta = "DELETE FROM mascotas WHERE id = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la mascota " + id, e);
        }
    }

    @Override
    public List<Mascota> buscarPorCliente(Integer idCliente) {
        List<Mascota> lista = new ArrayList<>();
        String consulta = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE id_cliente = ? ORDER BY id";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Mascota(rs.getInt("id"), rs.getString("nombre"), rs.getString("especie"),
                            rs.getString("raza"), rs.getString("fecha_nacimiento"), rs.getInt("id_cliente")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar mascotas por cliente", e);
        }
        return lista;
    }

    @Override
    public List<Mascota> listarPorEspecie(String especie) {
        List<Mascota> lista = new ArrayList<>();
        String consulta = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE especie = ? ORDER BY id";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setString(1, especie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Mascota(rs.getInt("id"), rs.getString("nombre"), rs.getString("especie"),
                            rs.getString("raza"), rs.getString("fecha_nacimiento"), rs.getInt("id_cliente")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar mascotas por especie", e);
        }
        return lista;
    }
}