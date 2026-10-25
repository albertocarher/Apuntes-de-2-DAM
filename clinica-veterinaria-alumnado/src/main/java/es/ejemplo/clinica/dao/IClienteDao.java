package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Cliente;
import java.util.List;
import java.util.Optional;

public interface IClienteDao  {
    /** Lista todas las entidades. */
    List<Cliente> listar();
    /** Busca una entidad por su identificador. */
    Optional<Cliente> buscarPorId(Integer id);
    /** Inserta una entidad y devuelve su identificador generado. */
    int insertar(Cliente entidad);
    /** Actualiza y devuelve el número de filas afectadas. */
    int actualizar(Cliente entidad);
    /** Elimina por ID y devuelve el número de filas afectadas. */
    int eliminar(Integer id);

}
