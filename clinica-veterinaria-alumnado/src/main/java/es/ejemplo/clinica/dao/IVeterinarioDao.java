package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Veterinario;
import java.util.List;
import java.util.Optional;

public interface IVeterinarioDao  {
    /** Lista todas las entidades. */
    List<Veterinario> listar();
    /** Busca una entidad por su identificador. */
    Optional<Veterinario> buscarPorId(Integer id);
    /** Inserta una entidad y devuelve su identificador generado. */
    int insertar(Veterinario entidad);
    /** Actualiza y devuelve el número de filas afectadas. */
    int actualizar(Veterinario entidad);
    /** Elimina por ID y devuelve el número de filas afectadas. */
    int eliminar(Integer id);

    List<Veterinario> buscarPorEspecialidad(String especialidad);
}
