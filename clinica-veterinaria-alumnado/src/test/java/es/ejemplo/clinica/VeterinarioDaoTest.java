package es.ejemplo.clinica;

import es.ejemplo.clinica.dao.*;
import es.ejemplo.clinica.modelo.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de VeterinarioDao. Cada test comienza con una base de datos SQLite vacía
 * y termina eliminando ese fichero temporal.
 */
class VeterinarioDaoTest extends BaseDaoTest {

    /** Comprueba: buscar todos devuelve lista vacia. */
    @Test
    void buscarTodosDevuelveListaVaciaTest() {
        assertTrue(veterinarioDao.listar().isEmpty());
    }

    /** Comprueba: insertar retorna id. */
    @Test
    void insertarRetornaIdTest() {
        assertTrue(insertarVeterinarioDePrueba()>0);
    }

    /** Comprueba: buscar por id existente. */
    @Test
    void buscarPorIdExistenteTest() {
        int id=insertarVeterinarioDePrueba();
        assertEquals("Elena",veterinarioDao.buscarPorId(id).orElseThrow().getNombre());
    }

    /** Comprueba: buscar por id ausente. */
    @Test
    void buscarPorIdAusenteTest() {
        assertTrue(veterinarioDao.buscarPorId(8888).isEmpty());
    }

    /** Comprueba: buscar por id nulo. */
    @Test
    void buscarPorIdNuloTest() {
        assertTrue(veterinarioDao.buscarPorId(null).isEmpty());
    }

    /** Comprueba: buscar todos devuelve dos. */
    @Test
    void buscarTodosDevuelveDosTest() {
        insertarVeterinarioDePrueba();
        veterinarioDao.insertar(new Veterinario(null,"B","Cirugía",new BigDecimal("70")));
        assertEquals(2,veterinarioDao.listar().size());
    }

    /** Comprueba: especialidad coincide. */
    @Test
    void especialidadCoincideTest() {
        insertarVeterinarioDePrueba();
        assertEquals(1,veterinarioDao.buscarPorEspecialidad("Medicina general").size());
    }

    /** Comprueba: especialidad sin coincidencia. */
    @Test
    void especialidadSinCoincidenciaTest() {
        insertarVeterinarioDePrueba();
        assertTrue(veterinarioDao.buscarPorEspecialidad("Odontología").isEmpty());
    }

    /** Comprueba: filtrar orden tarifa. */
    @Test
    void filtrarOrdenTarifaTest() {
        insertarVeterinarioDePrueba();
        veterinarioDao.insertar(new Veterinario(null,"B","Medicina general",new BigDecimal("20")));
        assertEquals("B",veterinarioDao.buscarPorEspecialidad("Medicina general").get(0).getNombre());
    }

    /** Comprueba: actualizar especialidad. */
    @Test
    void actualizarEspecialidadTest() {
        int id=insertarVeterinarioDePrueba();
        veterinarioDao.actualizar(new Veterinario(id,"Elena","Cirugía",new BigDecimal("70")));
        assertEquals("Cirugía",veterinarioDao.buscarPorId(id).orElseThrow().getEspecialidad());
    }

    /** Comprueba: actualizar ausente. */
    @Test
    void actualizarAusenteTest() {
        assertEquals(0,veterinarioDao.actualizar(new Veterinario(789,"B","Cirugía",new BigDecimal("70"))));
    }

    /** Comprueba: eliminar existente. */
    @Test
    void eliminarExistenteTest() {
        assertEquals(1,veterinarioDao.eliminar(insertarVeterinarioDePrueba()));
    }

    /** Comprueba: eliminar ausente. */
    @Test
    void eliminarAusenteTest() {
        assertEquals(0,veterinarioDao.eliminar(500));
    }

    /** Comprueba: tarifa negativa rechazada. */
    @Test
    void tarifaNegativaRechazadaTest() {
        assertThrows(DaoException.class,()->veterinarioDao.insertar(new Veterinario(null,"B","Cirugía",new BigDecimal("-2"))));
    }

    /** Comprueba: tarifa cero permitida. */
    @Test
    void tarifaCeroPermitidaTest() {
        int id=veterinarioDao.insertar(new Veterinario(null,"B","Cirugía",BigDecimal.ZERO));
        assertEquals(0,veterinarioDao.buscarPorId(id).orElseThrow().getTarifaHora().compareTo(BigDecimal.ZERO));
    }

    /** Comprueba: interfaz dao permite filtrar. */
    @Test
    void interfazDAOPermiteFiltrarTest() {
        IVeterinarioDao dao=veterinarioDao;
        insertarVeterinarioDePrueba();
        assertEquals(1,dao.buscarPorEspecialidad("Medicina general").size());
    }
}
