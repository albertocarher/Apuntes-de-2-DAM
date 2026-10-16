package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductoDao extends ConexionBD{
    //private  ConexionBD conexionBD;

    public ProductoDao(String url) {
        super(url);
    }


    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String query = "SELECT id,nombre,precio FROM Productos";

        try (Connection connection = this.obtenerConexion()){

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoProducto(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        //conexion
        // sql
        // preparateStatement
        //ResultSet rs
        //rs.next
        /**
         * while(rs.next()) {
         * producto = new Producto(rs.get(id). rs.get(no
         *
         * }
         *
         *
         */
        return lista;
    }

    public Optional<Producto> buscarPorId(int id) {
        Optional<Producto> producto = Optional.empty();
        String sql = "Select id, nombre, precio FROM Productos WHERE id = ?";
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                producto = Optional.of(mapeoProducto(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return producto;
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Producto producto) {
        boolean actualizado = false;
        String query = "UPDATE Productos SET nombre = ?, precio = ? WHERE id = ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getId());

            actualizado = ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return actualizado;

    }

    public boolean eliminar(int id) {
        boolean eliminado = false;
        String query = "DELETE FROM Productos WHERE id = ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);

            eliminado = ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return eliminado;
    }

    public List<Producto> buscarPorNombre(String texto) {
        List<Producto> lista = new ArrayList<>();
        String query = "SELECT id,nombre,precio FROM Productos WHERE nombre LIKE ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, "%" + texto + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoProducto(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;

    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        List<Producto> lista = new ArrayList<>();
        String query = "SELECT id,nombre,precio FROM Productos WHERE precio BETWEEN ? AND ?";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setDouble(1, minimo);
            ps.setDouble(2, maximo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoProducto(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public int contar() {
        int total = 0;
        String query = "SELECT COUNT(*) FROM Productos";

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

    public double precioMedio() {
        double media = 0;
        String query = "SELECT AVG(precio) FROM Productos";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            // AVG devuelve NULL si la tabla está vacía; getDouble lo convierte en 0
            while (rs.next()) {
                media = rs.getDouble(1);
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return media;
    }

    private Producto mapeoProducto(ResultSet rs) {

        try {
            return new Producto(rs.getInt(1), rs.getString(2),rs.getDouble(3) );
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
