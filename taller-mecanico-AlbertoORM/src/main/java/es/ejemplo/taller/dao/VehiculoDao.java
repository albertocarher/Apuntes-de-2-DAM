package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class VehiculoDao  {
    private final String url;
    public VehiculoDao(String url) { this.url = url; }

    public List<Vehiculo> listar() {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public Optional<Vehiculo> buscarPorId(String id) {
        // TODO: implementar según el contrato del README.
        return Optional.empty();
    }
    public int insertar(Vehiculo dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int actualizar(Vehiculo dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int eliminar(String id) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public List<Vehiculo> listarPorMarca(String marca) {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
}
