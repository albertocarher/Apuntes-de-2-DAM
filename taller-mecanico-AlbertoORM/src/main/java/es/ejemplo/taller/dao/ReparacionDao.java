package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Reparacion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class ReparacionDao  {
    private final String url;
    public ReparacionDao(String url) { this.url = url; }

    public List<Reparacion> listar() {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public Optional<Reparacion> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        return Optional.empty();
    }
    public int insertar(Reparacion dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int actualizar(Reparacion dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public List<Reparacion> buscarPorVehiculo(String valor) {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public List<Reparacion> buscarPorMecanico(Integer valor) {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public List<Reparacion> listarPendientes() {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
}
