package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

/** Operaciones JDBC comunes. El DAO llamador cierra Connection y PreparedStatement. */
public abstract class AbstractDao<T> extends ConexionBD {
    private static final Logger LOG = Logger.getLogger(AbstractDao.class.getName());

    protected AbstractDao(String url) { super(url); }

    /** Cada DAO captura errores de lectura y devuelve Optional.empty() si falla. */
    protected abstract Optional<T> mapRsToClass(ResultSet rs);

    protected List<T> consultar(PreparedStatement ps) {
        if (ps == null) return List.of();
        try (ResultSet rs = ps.executeQuery()) {
            List<T> resultado = new ArrayList<>();
            while (rs.next()) {
                Optional<T> entidad = mapRsToClass(rs);
                if (entidad.isEmpty()) return List.of(); // No devolver una lista parcial
                resultado.add(entidad.get());
            }
            return resultado;
        } catch (SQLException | RuntimeException e) {
            registrar("consultar", e);
            return List.of();
        }
    }

    protected Optional<T> consultarUno(PreparedStatement ps) {
        if (ps == null) return Optional.empty();
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapRsToClass(rs) : Optional.empty();
        } catch (SQLException | RuntimeException e) {
            registrar("consultarUno", e);
            return Optional.empty();
        }
    }

    protected boolean ejecutar(PreparedStatement ps) {
        if (ps == null) return false;
        try { return ps.executeUpdate() > 0; }
        catch (SQLException | RuntimeException e) { registrar("ejecutar", e); return false; }
    }

    protected int consultarEntero(PreparedStatement ps) {
        if (ps == null) return 0;
        try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        catch (SQLException | RuntimeException e) { registrar("consultarEntero", e); return 0; }
    }

    protected double consultarDecimal(PreparedStatement ps) {
        if (ps == null) return 0;
        try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getDouble(1) : 0; }
        catch (SQLException | RuntimeException e) { registrar("consultarDecimal", e); return 0; }
    }

    protected Map<String, Integer> consultarAgrupado(PreparedStatement ps) {
        if (ps == null) return Map.of();
        try (ResultSet rs = ps.executeQuery()) {
            Map<String, Integer> mapa = new LinkedHashMap<>();
            while (rs.next()) mapa.put(rs.getString(1), rs.getInt(2));
            return mapa;
        } catch (SQLException | RuntimeException e) {
            registrar("consultarAgrupado", e);
            return Map.of();
        }
    }

    protected void registrar(String operacion, Exception e) {
        LOG.log(Level.WARNING, "Fallo JDBC: " + operacion, e);
    }
}
