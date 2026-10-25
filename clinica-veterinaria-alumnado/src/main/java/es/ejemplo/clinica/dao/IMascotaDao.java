package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Mascota;
import java.util.List;
import java.util.Optional;

public interface IMascotaDao  {
    /** Lista todas las entidades. */
    List<Mascota> listar();
    /** Busca una entidad por su identificador. */
    Optional<Mascota> buscarPorId(Integer id);
    /** Inserta una entidad y devuelve su identificador generado. */
    int insertar(Mascota entidad);
    /** Actualiza y devuelve el número de filas afectadas. */
    int actualizar(Mascota entidad);
    /** Elimina por ID y devuelve el número de filas afectadas. */
    int eliminar(Integer id);

    List<Mascota> buscarPorCliente(Integer idCliente);
    List<Mascota> listarPorEspecie(String especie);
}
