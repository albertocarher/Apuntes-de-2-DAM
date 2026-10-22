package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Mecanico;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class MecanicoDao  {
    private final String url;
    public MecanicoDao(String url) { this.url = url; }
    
    public List<Mecanico> listar() {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public Optional<Mecanico> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        return Optional.empty();
    }
    public int insertar(Mecanico dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int actualizar(Mecanico dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
}
