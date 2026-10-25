package es.ejemplo.clinica;

import es.ejemplo.clinica.dao.*;
import es.ejemplo.clinica.modelo.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de MascotaDao. Cada test comienza con una base de datos SQLite vacía
 * y termina eliminando ese fichero temporal.
 */
class MascotaDaoTest extends BaseDaoTest {

    /** Comprueba: buscar todos devuelve lista vacia. */
    @Test
    void buscarTodosDevuelveListaVaciaTest() {
        assertTrue(mascotaDao.listar().isEmpty());
    }

    /** Comprueba: insertar retorna id. */
    @Test
    void insertarRetornaIdTest() {
        assertTrue(insertarMascotaDePrueba()>0);
    }

    /** Comprueba: buscar por id existente. */
    @Test
    void buscarPorIdExistenteTest() {
        int id=insertarMascotaDePrueba();
        assertEquals("Luna",mascotaDao.buscarPorId(id).orElseThrow().getNombre());
    }

    /** Comprueba: buscar por id ausente. */
    @Test
    void buscarPorIdAusenteTest() {
        assertTrue(mascotaDao.buscarPorId(8888).isEmpty());
    }

    /** Comprueba: buscar por id nulo. */
    @Test
    void buscarPorIdNuloTest() {
        assertTrue(mascotaDao.buscarPorId(null).isEmpty());
    }

    /** Comprueba: buscar todos devuelve dos. */
    @Test
    void buscarTodosDevuelveDosTest() {
        int id=insertarMascotaDePrueba();
        mascotaDao.insertar(new Mascota(null,"Milo","Gato",null,"2022-01-01",1));
        assertEquals(2,mascotaDao.listar().size());
    }

    /** Comprueba: buscar por cliente coincide. */
    @Test
    void buscarPorClienteCoincideTest() {
        int id=insertarMascotaDePrueba();
        assertEquals(id,mascotaDao.buscarPorCliente(1).get(0).getId());
    }

    /** Comprueba: buscar por cliente sin coincidencia. */
    @Test
    void buscarPorClienteSinCoincidenciaTest() {
        insertarMascotaDePrueba();
        assertTrue(mascotaDao.buscarPorCliente(999).isEmpty());
    }

    /** Comprueba: listar por especie coincide. */
    @Test
    void listarPorEspecieCoincideTest() {
        insertarMascotaDePrueba();
        assertEquals(1,mascotaDao.listarPorEspecie("Perro").size());
    }

    /** Comprueba: listar por especie no coincide. */
    @Test
    void listarPorEspecieNoCoincideTest() {
        insertarMascotaDePrueba();
        assertTrue(mascotaDao.listarPorEspecie("Gato").isEmpty());
    }

    /** Comprueba: actualizar raza. */
    @Test
    void actualizarRazaTest() {
        int id=insertarMascotaDePrueba();
        mascotaDao.actualizar(new Mascota(id,"Luna","Perro","Beagle","2020-01-01",1));
        assertEquals("Beagle",mascotaDao.buscarPorId(id).orElseThrow().getRaza());
    }

    /** Comprueba: actualizar ausente. */
    @Test
    void actualizarAusenteTest() {
        assertEquals(0,mascotaDao.actualizar(new Mascota(55,"A","Gato",null,"2020-01-01",1)));
    }

    /** Comprueba: eliminar existente. */
    @Test
    void eliminarExistenteTest() {
        assertEquals(1,mascotaDao.eliminar(insertarMascotaDePrueba()));
    }

    /** Comprueba: eliminar ausente. */
    @Test
    void eliminarAusenteTest() {
        assertEquals(0,mascotaDao.eliminar(55));
    }

    /** Comprueba: propietario inexistente rechazado. */
    @Test
    void propietarioInexistenteRechazadoTest() {
        assertThrows(DaoException.class,()->mascotaDao.insertar(new Mascota(null,"N","Gato",null,"2020-01-01",300)));
    }

    /** Comprueba: interfaz dao permite filtrar. */
    @Test
    void interfazDAOPermiteFiltrarTest() {
        IMascotaDao dao=mascotaDao;
        insertarMascotaDePrueba();
        assertEquals(1,dao.buscarPorCliente(1).size());
    }
}
