package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Veterinario;
import java.sql.*;
import java.util.*;

public final class VeterinarioDao implements IVeterinarioDao {
 public VeterinarioDao(String url) { 

 }
 @Override public List<Veterinario> listar() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listar'");
 }
 @Override public Optional<Veterinario> buscarPorId(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
 }
 @Override public int insertar(Veterinario entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertar'");
 }
 @Override public int actualizar(Veterinario entidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
 }
 @Override public int eliminar(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
 }
 @Override
 public List<Veterinario> buscarPorEspecialidad(String especialidad) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorEspecialidad'");
 }
}

 