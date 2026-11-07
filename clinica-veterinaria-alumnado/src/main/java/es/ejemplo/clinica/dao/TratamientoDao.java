package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Tratamiento;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public final class TratamientoDao extends ConexionBD implements ITratamientoDao {
 public TratamientoDao(String url) { 
    super(url);
 }

 @Override
 public List<Tratamiento> listar() {
    // TODO Auto-generated method stub
     List<Tratamiento> lista = new ArrayList<>();
     String consulta = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos ORDER BY id";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta);
          ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
             lista.add(new Tratamiento(rs.getInt("id"), rs.getInt("id_consulta"), rs.getString("descripcion"),
                     rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario")));
         }
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }
     return lista;
 }

 @Override
 public Optional<Tratamiento> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
     if (id == null) {
         return Optional.empty();
     }
     String consulta = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos WHERE id = ?";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setInt(1, id);
         try (ResultSet rs = ps.executeQuery()) {
             if (rs.next()) {
                 return Optional.of(new Tratamiento(rs.getInt("id"), rs.getInt("id_consulta"),
                         rs.getString("descripcion"), rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario")));
             }
         }
     } catch (SQLException e) {
         throw new DaoException("Error al buscar el tratamiento " + id, e);
     }
     return Optional.empty();
 }

 @Override
 public int insertar(Tratamiento entidad) {
    // TODO Auto-generated method stub
     String consulta = "INSERT INTO tratamientos (id_consulta, descripcion, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
         ps.setObject(1, entidad.getIdConsulta());
         ps.setString(2, entidad.getDescripcion());
         ps.setObject(3, entidad.getCantidad());
         ps.setBigDecimal(4, entidad.getPrecioUnitario());
         ps.executeUpdate();
         try (ResultSet rs = ps.getGeneratedKeys()) {
             if (rs.next()) {
                 return rs.getInt(1);
             }
         }
         throw new SQLException("No se obtuvo el identificador generado");
     } catch (SQLException e) {
         throw new DaoException("Error al insertar el tratamiento", e);
     }

 }

 @Override
 public int actualizar(Tratamiento entidad) {
    // TODO Auto-generated method stub
     if (entidad.getId() == null) {
         throw new IllegalArgumentException("El identificador es obligatorio para actualizar");
     }
     String consulta = "UPDATE tratamientos SET id_consulta = ?, descripcion = ?, cantidad = ?, precio_unitario = ? WHERE id = ?";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setObject(1, entidad.getIdConsulta());
         ps.setString(2, entidad.getDescripcion());
         ps.setObject(3, entidad.getCantidad());
         ps.setBigDecimal(4, entidad.getPrecioUnitario());
         ps.setObject(5, entidad.getId());
         return ps.executeUpdate();
     } catch (SQLException e) {
         throw new DaoException("Error al actualizar el tratamiento", e);
     }

 }

 @Override
 public int eliminar(Integer id) {
    // TODO Auto-generated method stub
     String consulta = "DELETE FROM tratamientos WHERE id = ?";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setInt(1, id);
         return ps.executeUpdate();
     } catch (SQLException e) {
         throw new DaoException("Error al eliminar el tratamiento " + id, e);
     }

 }

 @Override
 public List<Tratamiento> listarPorConsulta(Integer idConsulta) {
    // TODO Auto-generated method stub
     List<Tratamiento> lista = new ArrayList<>();
     String consulta = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos WHERE id_consulta = ? ORDER BY id";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setInt(1, idConsulta);
         try (ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                 lista.add(new Tratamiento(rs.getInt("id"), rs.getInt("id_consulta"),
                         rs.getString("descripcion"), rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario")));
             }
         }
     } catch (SQLException e) {
         throw new DaoException("Error al listar tratamientos de la consulta " + idConsulta, e);
     }
     return lista;

 }

 @Override
 public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
    // TODO Auto-generated method stub
     BigDecimal total = BigDecimal.ZERO;
     for (Tratamiento t : listarPorConsulta(idConsulta)) {
         total = total.add(t.getPrecioUnitario().multiply(BigDecimal.valueOf(t.getCantidad())));
     }
     return total;

 }
 
}
