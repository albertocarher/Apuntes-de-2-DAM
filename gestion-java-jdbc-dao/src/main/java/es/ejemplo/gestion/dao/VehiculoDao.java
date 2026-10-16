package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import es.ejemplo.gestion.model.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VehiculoDao extends ConexionBD {
    private  ConexionBD conexionBD;

    public VehiculoDao(String url) {
        super(url);
    }
    

    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY matricula";

        try (Connection connection = this.obtenerConexion()){

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoVehiculo(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        Optional<Vehiculo> producto = Optional.empty();
        String sql = "Select matricula, marca, modelo, anio FROM Vehiculos WHERE matricula = ?";
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, matricula);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                producto = Optional.of(mapeoVehiculo(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return producto;
    }

    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMatricula());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Vehiculo vehiculo) {
        boolean actualizado = false;
        String query = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, vehiculo.getMarca());
            ps.setString(2, vehiculo.getModelo());
            ps.setInt(3, vehiculo.getAnio());
            ps.setString(4, vehiculo.getMatricula());

            actualizado = ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return actualizado;
    }

    public boolean eliminar(String matricula) {
        boolean eliminado = false;
        String query = "DELETE FROM Vehiculos WHERE matricula = ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, matricula);

            eliminado = ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return eliminado;
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca LIKE ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, "%" + texto + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoVehiculo(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE anio BETWEEN ? AND ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, desde);
            ps.setInt(2, hasta);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoVehiculo(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public int contar() {
        int total = 0;
        String query = "SELECT COUNT(*) FROM Vehiculos";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return total;
    }

    public Map<String, Integer> contarPorMarca() {
        String query = "SELECT marca, COUNT(*) AS total FROM Vehiculos GROUP BY marca";
        Map<String, Integer> resultado = new HashMap<>();

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String marca = rs.getString("marca");
                int total = rs.getInt("total");
                resultado.put(marca, total);
            }

        } catch (SQLException e) { // Es mejor capturar excepciones específicas de SQL
            throw new AplicationException(e.getMessage(), e);
        }
        return resultado;
    }

    private Vehiculo mapeoVehiculo(ResultSet rs) {

        try {
            return new Vehiculo(rs.getString(1), rs.getString(2),rs.getString(3),rs.getInt(4) );
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
