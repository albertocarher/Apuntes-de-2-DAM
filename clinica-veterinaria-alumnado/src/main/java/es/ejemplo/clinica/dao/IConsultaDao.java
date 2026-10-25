package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Consulta;
import java.util.List;
import java.util.Optional;

public interface IConsultaDao  {
    /** Lista todas las entidades. */
    List<Consulta> listar();
    /** Busca una entidad por su identificador. */
    Optional<Consulta> buscarPorId(Integer id);
    /** Inserta una entidad y devuelve su identificador generado. */
    int insertar(Consulta entidad);
    /** Actualiza y devuelve el número de filas afectadas. */
    int actualizar(Consulta entidad);
    /** Elimina por ID y devuelve el número de filas afectadas. */
    int eliminar(Integer id);

    List<Consulta> buscarPorMascota(Integer idMascota);
    List<Consulta> buscarPorVeterinario(Integer idVeterinario);
    List<Consulta> listarProgramadas();
}
