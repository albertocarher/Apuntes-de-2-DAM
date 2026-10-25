package es.ejemplo.clinica;

import es.ejemplo.clinica.dao.DaoException;
import es.ejemplo.clinica.modelo.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de integración: cada método verifica un solo comportamiento. */
class IntegracionDaoTest extends BaseDaoTest {

    /** Una mascota debe poder localizarse desde el identificador de su propietario. */
    @Test
    void mascotaSeRelacionaConClienteTest() {
        insertarMascotaDePrueba();
        assertEquals("Luna", mascotaDao.buscarPorCliente(1).get(0).getNombre());
    }

    /** Una consulta debe poder localizarse desde su mascota. */
    @Test
    void consultaSeRelacionaConMascotaTest() {
        insertarConsultaDePrueba();
        assertEquals(1, consultaDao.buscarPorMascota(1).size());
    }

    /** Una consulta debe poder localizarse desde su veterinario. */
    @Test
    void consultaSeRelacionaConVeterinarioTest() {
        insertarConsultaDePrueba();
        assertEquals(1, consultaDao.buscarPorVeterinario(1).size());
    }

    /** Los tratamientos de una consulta deben poder recuperarse. */
    @Test
    void tratamientoSeRelacionaConConsultaTest() {
        int idConsulta = insertarConsultaDePrueba();
        tratamientoDao.insertar(new Tratamiento(null, idConsulta, "Vacuna", 1, BigDecimal.TEN));
        assertEquals(1, tratamientoDao.listarPorConsulta(idConsulta).size());
    }

    /** El coste de los tratamientos debe depender de su cantidad y precio. */
    @Test
    void tratamientoCalculaCosteDeConsultaTest() {
        int idConsulta = insertarConsultaDePrueba();
        tratamientoDao.insertar(new Tratamiento(null, idConsulta, "Vacuna", 2, BigDecimal.TEN));
        assertEquals(0, tratamientoDao.calcularCosteTratamientos(idConsulta).compareTo(new BigDecimal("20")));
    }

    /** No debe poder eliminarse un cliente que todavía tiene mascotaDao. */
    @Test
    void eliminarClienteConMascotaLanzaExcepcionTest() {
        insertarMascotaDePrueba();
        assertThrows(DaoException.class, () -> clienteDao.eliminar(1));
    }

    /** No se admite crear una mascota con un propietario inexistente. */
    @Test
    void insertarMascotaSinPropietarioLanzaExcepcionTest() {
        assertThrows(DaoException.class,
                () -> mascotaDao.insertar(new Mascota(null, "Max", "Perro", null, "2020-01-01", 9999)));
    }

    /** No debe eliminarse una consulta que todavía tiene tratamientoDao. */
    @Test
    void eliminarConsultaConTratamientoLanzaExcepcionTest() {
        int idConsulta = insertarConsultaDePrueba();
        tratamientoDao.insertar(new Tratamiento(null, idConsulta, "Vacuna", 1, BigDecimal.TEN));
        assertThrows(DaoException.class, () -> consultaDao.eliminar(idConsulta));
    }

    /** La cadena introducida como criterio se trata como dato, no como SQL. */
    @Test
    void filtrarEspecieConTextoSqlNoDevuelveMascotasTest() {
        insertarMascotaDePrueba();
        assertTrue(mascotaDao.listarPorEspecie("Perro' OR 1=1 --").isEmpty());
    }

    /** Una consulta sin tratamientoDao tiene coste cero. */
    @Test
    void consultaSinTratamientosDevuelveCosteCeroTest() {
        int idConsulta = insertarConsultaDePrueba();
        assertEquals(0, tratamientoDao.calcularCosteTratamientos(idConsulta).compareTo(BigDecimal.ZERO));
    }
}
