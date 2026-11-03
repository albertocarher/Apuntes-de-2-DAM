package es.ejemplo.taller;
import es.ejemplo.taller.dao.*;
import es.ejemplo.taller.modelo.*;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.sql.*;
import java.math.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
abstract class BaseTest {
    Path archivo;
    String url;
    ClienteDao clientes;
    VehiculoDao vehiculos;
    MecanicoDao mecanicos;
    ReparacionDao reparaciones;
    PiezaReparacionDao piezas;
    @BeforeEach void preparar() throws Exception {
        archivo = Files.createTempFile("taller-test-", ".db");
        try (java.io.InputStream plantilla = getClass().getResourceAsStream("/taller-test.sqlite3")) {
            if (plantilla == null) throw new java.io.IOException("Falta taller-test.sqlite3 en src/test/resources");
            Files.copy(plantilla, archivo, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        url = "jdbc:sqlite:" + archivo.toAbsolutePath();
        clientes = new ClienteDao(url);
        vehiculos = new VehiculoDao(url);
        mecanicos = new MecanicoDao(url);
        reparaciones = new ReparacionDao(url);
        piezas = new PiezaReparacionDao(url);
        try (Connection c = DriverManager.getConnection(url);
             Statement st = c.createStatement();
             java.io.InputStream in = getClass().getResourceAsStream("/schema.sql")) {
            String sql = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            for (String item : sql.split(";")) if (!item.isBlank()) st.execute(item.trim());
        }
    }
    @AfterEach void limpiar() throws Exception { if (archivo != null) Files.deleteIfExists(archivo); }
    int cliente() { return clientes.insertar(new Cliente(null,"Ana","ana@example.com","600111222")); }
    int mecanico() { return mecanicos.insertar(new Mecanico(null,"Luis","Motor",new BigDecimal("40.00"))); }
    String vehiculo() { int id=cliente(); assertTrue(id > 0); assertEquals(1,vehiculos.insertar(new Vehiculo("1234ABC","Seat","Ibiza",2018,id))); return "1234ABC"; }
    int reparacion() { String m=vehiculo(); int id=mecanico(); return reparaciones.insertar(new Reparacion(null,m,id,"Revision","2026-01-10",new BigDecimal("2.0"),"PENDIENTE")); }
    int pieza() { int id=reparacion(); return piezas.insertar(new PiezaReparacion(null,id,"Filtro",2,new BigDecimal("10.00"))); }
}
