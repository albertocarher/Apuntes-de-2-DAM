package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Consulta;
import java.sql.*;
import java.util.*;


public final class ConsultaDao extends ConexionBD implements IConsultaDao {

 public ConsultaDao(String url) { 
    super(url);
  }

 @Override
 public List<Consulta> listar() {
    // TODO Auto-generated method stub
     List<Consulta> lista = new ArrayList<>();
     String consulta = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas ORDER BY id";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta);
          ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
             lista.add(new Consulta(rs.getInt("id"), rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
                     rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado")));
         }
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }
     return lista;

 }

 @Override
 public Optional<Consulta> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
     if (id == null) {
         return Optional.empty();
     }
     String consulta = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas WHERE id = ?";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setInt(1, id);
         try (ResultSet rs = ps.executeQuery()) {
             if (rs.next()) {
                 return Optional.of(new Consulta(rs.getInt("id"), rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
                         rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado")));
             }
         }
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }
     return Optional.empty();

 }

 @Override
 public int insertar(Consulta entidad) {
    // TODO Auto-generated method stub
     String consulta = "INSERT INTO consultas (id_mascota, id_veterinario, fecha, horas, estado) VALUES (?, ?, ?, ?, ?)";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
         ps.setObject(1, entidad.getIdMascota());
         ps.setObject(2, entidad.getIdVeterinario());
         ps.setString(3, entidad.getFecha());
         ps.setBigDecimal(4, entidad.getHoras());
         ps.setString(5, entidad.getEstado());
         ps.executeUpdate();
         try (ResultSet rs = ps.getGeneratedKeys()) {
             if (rs.next()) {
                 return rs.getInt(1);
             }
         }
         throw new SQLException("No se obtuvo el identificador generado");
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }

 }

 @Override
 public int actualizar(Consulta entidad) {
    // TODO Auto-generated method stub
     if (entidad.getId() == null) {
         throw new IllegalArgumentException("El identificador es obligatorio para actualizar");
     }
     String consulta = "UPDATE consultas SET id_mascota = ?, id_veterinario = ?, fecha = ?, horas = ?, estado = ? WHERE id = ?";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setObject(1, entidad.getIdMascota());
         ps.setObject(2, entidad.getIdVeterinario());
         ps.setString(3, entidad.getFecha());
         ps.setBigDecimal(4, entidad.getHoras());
         ps.setString(5, entidad.getEstado());
         ps.setObject(6, entidad.getId());
         return ps.executeUpdate();
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }

 }

 @Override
 public int eliminar(Integer id) {
    // TODO Auto-generated method stub
     String consulta = "DELETE FROM consultas WHERE id = ?";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setInt(1, id);
         return ps.executeUpdate();
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }

 }

 @Override
 public List<Consulta> buscarPorMascota(Integer idMascota) {
    // TODO Auto-generated method stub
     List<Consulta> lista = new ArrayList<>();
     String consulta = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas "
             + "WHERE id_mascota = ? ORDER BY fecha, id";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setInt(1, idMascota);
         try (ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                 lista.add(new Consulta(rs.getInt("id"), rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
                         rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado")));
             }
         }
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }
     return lista;

 }

 @Override
 public List<Consulta> buscarPorVeterinario(Integer idVeterinario) {
    // TODO Auto-generated method stub
     List<Consulta> lista = new ArrayList<>();
     String consulta = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas "
             + "WHERE id_veterinario = ? ORDER BY fecha, id";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta)) {
         ps.setInt(1, idVeterinario);
         try (ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                 lista.add(new Consulta(rs.getInt("id"), rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
                         rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado")));
             }
         }
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }
     return lista;

 }

 @Override
 public List<Consulta> listarProgramadas() {
    // TODO Auto-generated method stub
     List<Consulta> lista = new ArrayList<>();
     String consulta = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas "
             + "WHERE estado = 'PROGRAMADA' ORDER BY fecha, id";
     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(consulta);
          ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
             lista.add(new Consulta(rs.getInt("id"), rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
                     rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado")));
         }
     } catch (SQLException e) {
         throw new DaoException(e.getMessage(), e);
     }
     return lista;

 }
 
}
