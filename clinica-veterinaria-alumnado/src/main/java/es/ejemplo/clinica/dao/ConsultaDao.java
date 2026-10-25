package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Consulta;
import java.sql.*;
import java.util.*;


public final class ConsultaDao implements IConsultaDao {
 public ConsultaDao(String url) { 

  }

 @Override
 public List<Consulta> listar() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listar'");
 }

 @Override
 public Optional<Consulta> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
 }

 @Override
 public int insertar(Consulta entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertar'");
 }

 @Override
 public int actualizar(Consulta entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
 }

 @Override
 public int eliminar(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
 }

 @Override
 public List<Consulta> buscarPorMascota(Integer idMascota) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorMascota'");
 }

 @Override
 public List<Consulta> buscarPorVeterinario(Integer idVeterinario) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorVeterinario'");
 }

 @Override
 public List<Consulta> listarProgramadas() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listarProgramadas'");
 }
 
}
