package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Mascota;
import java.util.*;

public final class MascotaDao implements IMascotaDao {
 public MascotaDao(String url) { 

 }
 @Override public List<Mascota> listar() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listar'");
 }
 @Override public Optional<Mascota> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
 }
 @Override public int insertar(Mascota entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertar'");
 }
 @Override public int actualizar(Mascota entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
 }
 @Override public int eliminar(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
 }
 @Override
 public List<Mascota> buscarPorCliente(Integer idCliente) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorCliente'");
 }
 @Override
 public List<Mascota> listarPorEspecie(String especie) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listarPorEspecie'");
 }
 }
