package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.PiezaReparacion;
import es.ejemplo.taller.modelo.Reparacion;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class PiezaReparacionDao extends ConexionBD {

    public PiezaReparacionDao(String url) { super(url); }

    public List<PiezaReparacion> listar() {
        List<PiezaReparacion> piezas = new ArrayList<>();
        String consulta = "SELECT id_pieza, id_reparacion, nombre, cantidad, precio_unitario FROM piezas_reparacion";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                piezas.add(new PiezaReparacion(
                        rs.getInt("id_pieza"),
                        rs.getInt("id_reparacion"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("precio_unitario")
                ));
            }
        } catch (SQLException e) {
            return List.of();
        }
        return piezas;
    }
    public Optional<PiezaReparacion> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "SELECT id_pieza,id_reparacion, nombre, cantidad, precio_unitario FROM piezas_reparacion WHERE id_pieza = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(new PiezaReparacion(id, rs.getInt("id_reparacion"), rs.getString("nombre"), rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    public int insertar(PiezaReparacion dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null || dato.getCantidad().equals(0)) {
            return 0;
        }
        String consulta = "INSERT INTO piezas_reparacion (id_reparacion, nombre, cantidad, precio_unitario) VALUES (?,?,?,?)";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            return ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")){
                return 0;
            }
            throw new RuntimeException(e);
        }
    }
    public int actualizar(PiezaReparacion dato) {
        // TODO: implementar según el contrato del README.
        if(dato == null){
            return 0;
        }
        String consulta = "UPDATE piezas_reparacion SET id_reparacion = ?, nombre = ?, cantidad = ?, precio_unitario = ? WHERE id_pieza = ?";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            ps.setInt(5, dato.getIdPieza());
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "DELETE FROM piezas_reparacion WHERE id_pieza = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)){
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<PiezaReparacion> listarPorReparacion(Integer id) {
        // TODO: implementar según el contrato del README.
        List<PiezaReparacion> piezas = new ArrayList<>();
        String consulta = "SELECT id_pieza, id_reparacion, nombre, cantidad, precio_unitario FROM piezas_reparacion WHERE id_reparacion = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    piezas.add(new PiezaReparacion(
                            rs.getInt("id_pieza"),
                            rs.getInt("id_reparacion"),
                            rs.getString("nombre"),
                            rs.getInt("cantidad"),
                            rs.getBigDecimal("precio_unitario")
                    ));
                }
            }
        } catch (SQLException e) {
            return List.of();
        }
        return piezas;
    }
    public BigDecimal calcularCostePiezas(Integer id) {
        // TODO: implementar según el contrato del README.
        String consulta = "SELECT SUM(cantidad * precio_unitario) AS coste_total FROM piezas_reparacion WHERE id_reparacion = ?";
        BigDecimal costeTotal = BigDecimal.ZERO;

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal resultado = rs.getBigDecimal("coste_total");
                    if (resultado != null) {
                        costeTotal = resultado;
                    }
                }
            }
        } catch (SQLException e) {
            return BigDecimal.ZERO;
        }

        return costeTotal;
    }
}
