package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao extends ConexionBD {
    public ClienteDao(String url) {
        super(url);
        }


    public List<Cliente> listar() {
        // TODO: implementar según el contrato del README.
        List<Cliente> clientes = new ArrayList<>();
        String query = "SELECT id_cliente, nombre, email, telefono FROM clientes";
        try(Connection con = obtenerConexion();
            PreparedStatement ps = con.prepareStatement(query)){
            ResultSet rs = ps.executeQuery();
        } catch (SQLException e) {
            return List.of();
        }
        return clientes;
    }
    public Optional<Cliente> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        return Optional.empty();
    }
    public int insertar(Cliente dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int actualizar(Cliente dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
}
