package es.ejemplo.gestion.dao;

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
        return Optional.empty();
    }

    public boolean eliminar(Integer id) {
        // TODO: DELETE ... WHERE id = ?; ejecutar(ps)
        return false;
    }

    public boolean insertar(Producto p) {
        // TODO: validar, INSERT con tres parámetros, ejecutar(ps)
        return false;
    }

    public boolean actualizar(Producto p) {
        // TODO: validar, UPDATE con parámetros, ejecutar(ps)
        return false;
    }

    public List<Producto> buscarPorNombre(String texto) {
        // TODO: búsqueda parcial case-insensitive, ordenada por nombre e id
        return List.of();
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        // TODO: BETWEEN inclusivo, ordenado por id
        return List.of();
    }

    public int contar() {
        // TODO: SELECT COUNT(*) y consultarEntero(ps)
        return 0;
    }

    public double precioMedio() {
        // TODO: COALESCE(AVG(precio),0) y consultarDecimal(ps)
        return 0;
    }
}
