package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Tratamiento;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public final class TratamientoDao implements ITratamientoDao {
 public TratamientoDao(String url) { 

 }

 @Override
 public List<Tratamiento> listar() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listar'");
 }

 @Override
 public Optional<Tratamiento> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
 }

 @Override
 public int insertar(Tratamiento entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertar'");
 }

 @Override
 public int actualizar(Tratamiento entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
 }

 @Override
 public int eliminar(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
 }

 @Override
 public List<Tratamiento> listarPorConsulta(Integer idConsulta) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listarPorConsulta'");
 }

 @Override
 public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'calcularCosteTratamientos'");
 }
 
}
