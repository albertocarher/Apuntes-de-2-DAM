package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.PiezaReparacion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class PiezaReparacionDao  {
    private final String url;
    public PiezaReparacionDao(String url) { this.url = url; }
    
    public List<PiezaReparacion> listar() {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public Optional<PiezaReparacion> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        return Optional.empty();
    }
    public int insertar(PiezaReparacion dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int actualizar(PiezaReparacion dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public List<PiezaReparacion> listarPorReparacion(Integer id) {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public BigDecimal calcularCostePiezas(Integer id) {
        // TODO: implementar según el contrato del README.
        return BigDecimal.ZERO;
    }
}
