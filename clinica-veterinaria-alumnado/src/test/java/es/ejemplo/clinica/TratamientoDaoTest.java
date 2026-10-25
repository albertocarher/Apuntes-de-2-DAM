package es.ejemplo.clinica;

import es.ejemplo.clinica.dao.*;
import es.ejemplo.clinica.modelo.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de TratamientoDao. Cada test comienza con una base de datos SQLite vacía
 * y termina eliminando ese fichero temporal.
 */
class TratamientoDaoTest extends BaseDaoTest {

    /** Comprueba: buscar todos devuelve lista vacia. */
    @Test
    void buscarTodosDevuelveListaVaciaTest() {
        assertTrue(tratamientoDao.listar().isEmpty());
    }

    /** Comprueba: insertar retorna id. */
    @Test
    void insertarRetornaIdTest() {
        int c=insertarConsultaDePrueba();
        assertTrue(tratamientoDao.insertar(new Tratamiento(null,c,"Vacuna",1,new BigDecimal("20")))>0);
    }

    /** Comprueba: buscar por id existente. */
    @Test
    void buscarPorIdExistenteTest() {
        int c=insertarConsultaDePrueba();
        int id=tratamientoDao.insertar(new Tratamiento(null,c,"Vacuna",1,new BigDecimal("20")));
        assertEquals("Vacuna",tratamientoDao.buscarPorId(id).orElseThrow().getDescripcion());
    }

    /** Comprueba: buscar por id ausente. */
    @Test
    void buscarPorIdAusenteTest() {
        assertTrue(tratamientoDao.buscarPorId(1000).isEmpty());
    }

    /** Comprueba: buscar por id nulo. */
    @Test
    void buscarPorIdNuloTest() {
        assertTrue(tratamientoDao.buscarPorId(null).isEmpty());
    }

    /** Comprueba: buscar todos devuelve dos. */
    @Test
    void buscarTodosDevuelveDosTest() {
        int c=insertarConsultaDePrueba();
        tratamientoDao.insertar(new Tratamiento(null,c,"A",1,BigDecimal.TEN));
        tratamientoDao.insertar(new Tratamiento(null,c,"B",1,BigDecimal.ONE));
        assertEquals(2,tratamientoDao.listar().size());
    }

    /** Comprueba: listar por consulta coincide. */
    @Test
    void listarPorConsultaCoincideTest() {
        int c=insertarConsultaDePrueba();
        tratamientoDao.insertar(new Tratamiento(null,c,"A",1,BigDecimal.TEN));
        assertEquals(1,tratamientoDao.listarPorConsulta(c).size());
    }

    /** Comprueba: listar por consulta sin coincidencia. */
    @Test
    void listarPorConsultaSinCoincidenciaTest() {
        insertarConsultaDePrueba();
        assertTrue(tratamientoDao.listarPorConsulta(999).isEmpty());
    }

    /** Comprueba: coste cero sin tratamientoDao. */
    @Test
    void costeCeroSinTratamientosTest() {
        assertEquals(0,tratamientoDao.calcularCosteTratamientos(123).compareTo(BigDecimal.ZERO));
    }

    /** Comprueba: coste una linea. */
    @Test
    void costeUnaLineaTest() {
        int c=insertarConsultaDePrueba();
        tratamientoDao.insertar(new Tratamiento(null,c,"A",3,new BigDecimal("12.50")));
        assertEquals(0,tratamientoDao.calcularCosteTratamientos(c).compareTo(new BigDecimal("37.5")));
    }

    /** Comprueba: coste dos lineas. */
    @Test
    void costeDosLineasTest() {
        int c=insertarConsultaDePrueba();
        tratamientoDao.insertar(new Tratamiento(null,c,"A",2,BigDecimal.TEN));
        tratamientoDao.insertar(new Tratamiento(null,c,"B",1,new BigDecimal("5")));
        assertEquals(0,tratamientoDao.calcularCosteTratamientos(c).compareTo(new BigDecimal("25")));
    }

    /** Comprueba: actualizar cantidad. */
    @Test
    void actualizarCantidadTest() {
        int c=insertarConsultaDePrueba();
        int id=tratamientoDao.insertar(new Tratamiento(null,c,"A",1,BigDecimal.TEN));
        tratamientoDao.actualizar(new Tratamiento(id,c,"A",4,BigDecimal.TEN));
        assertEquals(4,tratamientoDao.buscarPorId(id).orElseThrow().getCantidad());
    }

    /** Comprueba: actualizar ausente. */
    @Test
    void actualizarAusenteTest() {
        assertEquals(0,tratamientoDao.actualizar(new Tratamiento(444,1,"A",1,BigDecimal.TEN)));
    }

    /** Comprueba: eliminar existente. */
    @Test
    void eliminarExistenteTest() {
        int c=insertarConsultaDePrueba();
        int id=tratamientoDao.insertar(new Tratamiento(null,c,"A",1,BigDecimal.TEN));
        assertEquals(1,tratamientoDao.eliminar(id));
    }

    /** Comprueba: cantidad cero rechazada. */
    @Test
    void cantidadCeroRechazadaTest() {
        int c=insertarConsultaDePrueba();
        assertThrows(DaoException.class,()->tratamientoDao.insertar(new Tratamiento(null,c,"A",0,BigDecimal.TEN)));
    }

    /** Comprueba: interfaz dao permite calcular. */
    @Test
    void interfazDAOPermiteCalcularTest() {
        ITratamientoDao dao=tratamientoDao;
        assertEquals(0,dao.calcularCosteTratamientos(100).compareTo(BigDecimal.ZERO));
    }
}
