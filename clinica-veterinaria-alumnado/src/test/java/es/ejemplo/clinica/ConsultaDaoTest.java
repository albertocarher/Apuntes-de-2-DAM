package es.ejemplo.clinica;

import es.ejemplo.clinica.dao.*;
import es.ejemplo.clinica.modelo.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de ConsultaDao. Cada test comienza con una base de datos SQLite vacía
 * y termina eliminando ese fichero temporal.
 */
class ConsultaDaoTest extends BaseDaoTest {

    /** Comprueba: buscar todos devuelve lista vacia. */
    @Test
    void buscarTodosDevuelveListaVaciaTest() {
        assertTrue(consultaDao.listar().isEmpty());
    }

    /** Comprueba: insertar retorna id. */
    @Test
    void insertarRetornaIdTest() {
        assertTrue(insertarConsultaDePrueba()>0);
    }

    /** Comprueba: buscar por id existente. */
    @Test
    void buscarPorIdExistenteTest() {
        int id=insertarConsultaDePrueba();
        assertEquals("PROGRAMADA",consultaDao.buscarPorId(id).orElseThrow().getEstado());
    }

    /** Comprueba: buscar por id ausente. */
    @Test
    void buscarPorIdAusenteTest() {
        assertTrue(consultaDao.buscarPorId(9999).isEmpty());
    }

    /** Comprueba: buscar por id nulo. */
    @Test
    void buscarPorIdNuloTest() {
        assertTrue(consultaDao.buscarPorId(null).isEmpty());
    }

    /** Comprueba: buscar todos devuelve dos. */
    @Test
    void buscarTodosDevuelveDosTest() {
        insertarConsultaDePrueba();
        consultaDao.insertar(new Consulta(null,1,1,"2026-11-02 09:00",new BigDecimal("1"),"EN_CURSO"));
        assertEquals(2,consultaDao.listar().size());
    }

    /** Comprueba: buscar por mascota coincide. */
    @Test
    void buscarPorMascotaCoincideTest() {
        int id=insertarConsultaDePrueba();
        assertEquals(id,consultaDao.buscarPorMascota(1).get(0).getId());
    }

    /** Comprueba: buscar por mascota sin coincidencia. */
    @Test
    void buscarPorMascotaSinCoincidenciaTest() {
        insertarConsultaDePrueba();
        assertTrue(consultaDao.buscarPorMascota(999).isEmpty());
    }

    /** Comprueba: buscar por veterinario coincide. */
    @Test
    void buscarPorVeterinarioCoincideTest() {
        int id=insertarConsultaDePrueba();
        assertEquals(id,consultaDao.buscarPorVeterinario(1).get(0).getId());
    }

    /** Comprueba: buscar por veterinario sin coincidencia. */
    @Test
    void buscarPorVeterinarioSinCoincidenciaTest() {
        insertarConsultaDePrueba();
        assertTrue(consultaDao.buscarPorVeterinario(999).isEmpty());
    }

    /** Comprueba: programadas coinciden. */
    @Test
    void programadasCoincidenTest() {
        insertarConsultaDePrueba();
        assertEquals(1,consultaDao.listarProgramadas().size());
    }

    /** Comprueba: finalizadas no son programadas. */
    @Test
    void finalizadasNoSonProgramadasTest() {
        int id=insertarConsultaDePrueba();
        consultaDao.actualizar(new Consulta(id,1,1,"2026-11-01 09:00",new BigDecimal("1"),"FINALIZADA"));
        assertTrue(consultaDao.listarProgramadas().isEmpty());
    }

    /** Comprueba: actualizar ausente. */
    @Test
    void actualizarAusenteTest() {
        assertEquals(0,consultaDao.actualizar(new Consulta(123,1,1,"2026-11-01",BigDecimal.ONE,"PROGRAMADA")));
    }

    /** Comprueba: eliminar existente. */
    @Test
    void eliminarExistenteTest() {
        assertEquals(1,consultaDao.eliminar(insertarConsultaDePrueba()));
    }

    /** Comprueba: estado invalido rechazado. */
    @Test
    void estadoInvalidoRechazadoTest() {
        insertarMascotaDePrueba();
        insertarVeterinarioDePrueba();
        assertThrows(DaoException.class,()->consultaDao.insertar(new Consulta(null,1,1,"2026-11-01",BigDecimal.ONE,"CANCELADA")));
    }

    /** Comprueba: interfaz dao permite filtrar. */
    @Test
    void interfazDAOPermiteFiltrarTest() {
        IConsultaDao dao=consultaDao;
        insertarConsultaDePrueba();
        assertEquals(1,dao.listarProgramadas().size());
    }
}
