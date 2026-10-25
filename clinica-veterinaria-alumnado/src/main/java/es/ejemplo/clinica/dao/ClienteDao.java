package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Cliente;
import java.util.List;
import java.util.Optional;

public final class ClienteDao implements IClienteDao {
 public ClienteDao(String url) { 

 }

 @Override
 public List<Cliente> listar() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listar'");
 }

 @Override
 public Optional<Cliente> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
 }

 @Override
 public int insertar(Cliente entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertar'");
 }

 @Override
 public int actualizar(Cliente entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
 }

 @Override
 public int eliminar(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
 }
 
}
