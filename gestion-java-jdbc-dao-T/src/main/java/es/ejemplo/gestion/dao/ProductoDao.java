package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import java.sql.*;
import java.util.*;

public class ProductoDao extends AbstractDao<Producto> {
    public ProductoDao(String url) { super(url); }

    /** EJEMPLO COMPLETO: transformar ResultSet a Producto sin propagar excepciones. */
    @Override
    protected Optional<Producto> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass producto", e);
            return Optional.empty();
        }
    }

    /** EJEMPLO COMPLETO: listar productos mediante PreparedStatement. */
    public List<Producto> listar() {
        String sql = "SELECT id,nombre,precio FROM Productos ORDER BY id";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar productos", e);
            return List.of();
        }
    }

    // TODO: implementa cada método con PreparedStatement y try-with-resources.
    // IMPORTANTE: buscarPorId y eliminar usan Integer; la clase base NO necesita ID.
    public Optional<Producto> buscarPorId(Integer id) {
        // TODO: SELECT ... WHERE id = ?; ps.setInt(1, id); consultarUno(ps)
        String sql = "SELECT id,nombre,precio FROM Productos WHERE id = ?";
        List<Producto> productos = null;
        try (Connection con = obtenerConexion();
        PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, id);
            return consultarUno(ps);

        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean eliminar(Integer id) {
        // TODO: DELETE ... WHERE id = ?; ejecutar(ps)
        String query = "DELETE FROM Productos WHERE id = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | RuntimeException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean insertar(Producto p) {
        // TODO: validar, INSERT con tres parámetros, ejecutar(ps)
        if(p == null
                || p.getId() < 0
                || p.getNombre() == null
                || p.getNombre().isBlank()
                || p.getPrecio() < 0) {
            return false;
        }
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            return ejecutar(ps);
        } catch (SQLException | RuntimeException e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Producto p) {
        // TODO: validar, UPDATE con parámetros, ejecutar(ps)
        String query = "UPDATE Productos SET nombre = ?, precio = ? WHERE id = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {


            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException | RuntimeException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Producto> buscarPorNombre(String texto) {
        // TODO: búsqueda parcial case-insensitive, ordenada por nombre e id
        String query = "SELECT id,nombre,precio FROM Productos WHERE LOWER(nombre) LIKE LOWER(?) ORDER BY nombre, id";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query);) {


            ps.setString(1, "%" + texto + "%");
            return consultar(ps);

        } catch (SQLException | RuntimeException e) {
            throw new AplicationException(e.getMessage(), e);
        }

    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        // TODO: BETWEEN inclusivo, ordenado por id
        String query = "SELECT id,nombre,precio FROM Productos WHERE precio BETWEEN ? AND ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setDouble(1, minimo);
            ps.setDouble(2, maximo);

            return consultar(ps);

        } catch (SQLException | RuntimeException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public int contar() {
        // TODO: SELECT COUNT(*) y consultarEntero(ps)
        double media = 0;
        String query = "SELECT COUNT(*) FROM Productos";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {

            return consultarEntero(ps);

        } catch (SQLException | RuntimeException e) {
            throw new AplicationException(e.getMessage(), e);
        }

    }

    public double precioMedio() {
        // TODO: COALESCE(AVG(precio),0) y consultarDecimal(ps)
        String query = "SELECT COALESCE(AVG(precio),0) FROM Productos";
        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {
            return consultarDecimal(ps);
        } catch (SQLException | RuntimeException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }
}
