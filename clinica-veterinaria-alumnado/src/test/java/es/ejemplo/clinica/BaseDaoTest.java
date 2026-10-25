package es.ejemplo.clinica;

import es.ejemplo.clinica.dao.*;
import es.ejemplo.clinica.modelo.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/**
 * Preparacion comun de las pruebas DAO: crea un fichero SQLite temporal por test,
 * ejecuta el esquema, inicializa los DAO y elimina el fichero después del test.
 * No se modifica la base de datos de ejemplo del proyecto.
 */
abstract class BaseDaoTest {
 protected Path archivoBaseDatosTemporal;
 protected String urlConexionBaseDatos;
 protected IClienteDao clienteDao;
 protected IMascotaDao mascotaDao;
 protected IVeterinarioDao veterinarioDao;
 protected IConsultaDao consultaDao;
 protected ITratamientoDao tratamientoDao;

 @BeforeEach void prepararBaseDatosDePrueba() throws Exception {
  archivoBaseDatosTemporal=Files.createTempFile("clinica-junit-", ".sqlite3");
  urlConexionBaseDatos="jdbc:sqlite:" + archivoBaseDatosTemporal.toAbsolutePath();
  try(Connection conexion=abrir(urlConexionBaseDatos); Statement sentenciaSql=conexion.createStatement()) {
   String esquemaSql=new String(Objects.requireNonNull(getClass().getResourceAsStream("/schema.sql")).readAllBytes());
   for(String sentencia: esquemaSql.split(";")) if(!sentencia.isBlank()) sentenciaSql.execute(sentencia);
  }
  clienteDao=new ClienteDao(urlConexionBaseDatos); mascotaDao=new MascotaDao(urlConexionBaseDatos); veterinarioDao=new VeterinarioDao(urlConexionBaseDatos);
  consultaDao=new ConsultaDao(urlConexionBaseDatos); tratamientoDao=new TratamientoDao(urlConexionBaseDatos);
 }
 @AfterEach void eliminarBaseDatosTemporal() throws Exception { Files.deleteIfExists(archivoBaseDatosTemporal); }
 /** Inserta un cliente de ejemplo y devuelve su identificador. */
 protected int insertarClienteDePrueba() { return clienteDao.insertar(new Cliente(null,"Ana","ana@ejemplo.es","600123123")); }
 /** Inserta una mascota y su propietario de prueba; devuelve el ID de la mascota. */
 protected int insertarMascotaDePrueba() { int idCliente=insertarClienteDePrueba(); return mascotaDao.insertar(new Mascota(null,"Luna","Perro","Labrador","2020-01-01",idCliente)); }
 /** Inserta un veterinario de prueba y devuelve su identificador. */
 protected int insertarVeterinarioDePrueba() { return veterinarioDao.insertar(new Veterinario(null,"Elena","Medicina general",new BigDecimal("50.00"))); }
 /** Inserta una consulta con mascota y veterinario de prueba; devuelve su ID. */
 protected int insertarConsultaDePrueba() { return consultaDao.insertar(new Consulta(null,insertarMascotaDePrueba(),insertarVeterinarioDePrueba(),"2026-11-01 09:00",new BigDecimal("1.5"),"PROGRAMADA")); }

public Connection abrir(String url) throws SQLException {
  if (url == null || !url.startsWith("jdbc:sqlite:")) throw new IllegalArgumentException("URL SQLite no válida");
  String ruta = url.substring("jdbc:sqlite:".length());
  if (!ruta.equals(":memory:") && !ruta.startsWith("file:") && !Files.isRegularFile(Path.of(ruta)))
   throw new IllegalArgumentException("La base de datos no existe: " + ruta);
  Connection cn = DriverManager.getConnection(url);
  try (Statement st = cn.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
  catch (SQLException e) { cn.close(); throw e; }
  return cn;
 }

}
