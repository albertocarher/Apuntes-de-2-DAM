package es.ejemplo.clinica;

import es.ejemplo.clinica.dao.DaoException;
import es.ejemplo.clinica.dao.IClienteDao;
import es.ejemplo.clinica.modelo.Cliente;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de ClienteDao: un comportamiento comprobado por cada método @Test. */
class ClienteDaoTest extends BaseDaoTest {

    /** Comprueba que una tabla sin registros devuelve una lista vacía. */
    @Test
    void buscarTodosDevuelveListaVaciaTest() {
        assertTrue(clienteDao.listar().isEmpty());
    }

    /** Comprueba que insertar devuelve un ID positivo. */
    @Test
    void insertarDevuelveIdPositivoTest() {
        Cliente nuevoCliente = new Cliente(null, "Ana", "ana@ejemplo.es", "600123123");
        int idCliente = clienteDao.insertar(nuevoCliente);
        assertTrue(idCliente > 0);
    }

    /** Comprueba que dos inserciones generan identificadores diferentes. */
    @Test
    void insertarDosClientesGeneraIdsDistintosTest() {
        int idPrimerCliente = insertarClienteDePrueba();
        int idSegundoCliente = clienteDao.insertar(new Cliente(null, "Bea", "bea@ejemplo.es", "600200200"));
        assertNotEquals(idPrimerCliente, idSegundoCliente);
    }

    /** Comprueba que se puede recuperar el nombre de un cliente existente. */
    @Test
    void buscarPorIdDevuelveClienteExistenteTest() {
        int idCliente = insertarClienteDePrueba();
        assertEquals("Ana", clienteDao.buscarPorId(idCliente).orElseThrow().getNombre());
    }

    /** Comprueba que un identificador desconocido devuelve Optional vacío. */
    @Test
    void buscarPorIdInexistenteDevuelveVacioTest() {
        assertTrue(clienteDao.buscarPorId(99999).isEmpty());
    }

    /** Comprueba que buscar un ID nulo no provoca una excepción. */
    @Test
    void buscarPorIdNuloDevuelveVacioTest() {
        assertTrue(clienteDao.buscarPorId(null).isEmpty());
    }

    /** Comprueba que listar devuelve los clientes ordenados por identificador. */
    @Test
    void buscarTodosDevuelveClientesOrdenadosTest() {
        int idPrimerCliente = insertarClienteDePrueba();
        int idSegundoCliente = clienteDao.insertar(new Cliente(null, "Bea", "bea@ejemplo.es", "600200200"));
        assertEquals(List.of(idPrimerCliente, idSegundoCliente),
                     clienteDao.listar().stream().map(Cliente::getId).toList());
    }

    /** Comprueba que actualizar modifica el nombre almacenado. */
    @Test
    void actualizarModificaNombreTest() {
        int idCliente = insertarClienteDePrueba();
        clienteDao.actualizar(new Cliente(idCliente, "Ana R.", "ana@ejemplo.es", "600123123"));
        assertEquals("Ana R.", clienteDao.buscarPorId(idCliente).orElseThrow().getNombre());
    }

    /** Comprueba que actualizar modifica el teléfono almacenado. */
    @Test
    void actualizarModificaTelefonoTest() {
        int idCliente = insertarClienteDePrueba();
        clienteDao.actualizar(new Cliente(idCliente, "Ana", "ana@ejemplo.es", "600999999"));
        assertEquals("600999999", clienteDao.buscarPorId(idCliente).orElseThrow().getTelefono());
    }

    /** Comprueba que actualizar un ID desconocido no modifica filas. */
    @Test
    void actualizarClienteInexistenteDevuelveCeroTest() {
        assertEquals(0, clienteDao.actualizar(new Cliente(999, "Eva", "eva@ejemplo.es", "600000000")));
    }

    /** Comprueba que actualizar exige un identificador. */
    @Test
    void actualizarSinIdLanzaExcepcionTest() {
        assertThrows(IllegalArgumentException.class,
                () -> clienteDao.actualizar(new Cliente(null, "Eva", "eva@ejemplo.es", "600000000")));
    }

    /** Comprueba que eliminar un cliente existente afecta a una fila. */
    @Test
    void eliminarClienteExistenteDevuelveUnoTest() {
        int idCliente = insertarClienteDePrueba();
        assertEquals(1, clienteDao.eliminar(idCliente));
    }

    /** Comprueba que eliminar un ID ya eliminado devuelve cero. */
    @Test
    void eliminarClienteRepetidoDevuelveCeroTest() {
        int idCliente = insertarClienteDePrueba();
        clienteDao.eliminar(idCliente);
        assertEquals(0, clienteDao.eliminar(idCliente));
    }

    /** Comprueba que eliminar requiere un identificador no nulo. */
    @Test
    void eliminarIdNuloLanzaExcepcionTest() {
        assertThrows(NullPointerException.class, () -> clienteDao.eliminar(null));
    }

    /** Comprueba que la restricción UNIQUE impide emails duplicados. */
    @Test
    void insertarEmailDuplicadoLanzaExcepcionTest() {
        insertarClienteDePrueba();
        assertThrows(DaoException.class,
                () -> clienteDao.insertar(new Cliente(null, "Bea", "ana@ejemplo.es", "600200200")));
    }

    /** Comprueba que el DAO se puede utilizar mediante su interfaz. */
    @Test
    void interfazClienteDaoPermiteInsertarTest() {
        IClienteDao accesoClientes = clienteDao;
        int idCliente = accesoClientes.insertar(new Cliente(null, "Bea", "bea@ejemplo.es", "600200200"));
        assertTrue(idCliente > 0);
    }
}
