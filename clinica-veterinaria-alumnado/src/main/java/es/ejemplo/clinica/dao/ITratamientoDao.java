package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Tratamiento;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface ITratamientoDao  {
    /** Lista todas las entidades. */
    List<Tratamiento> listar();
    /** Busca una entidad por su identificador. */
    Optional<Tratamiento> buscarPorId(Integer id);
    /** Inserta una entidad y devuelve su identificador generado. */
    int insertar(Tratamiento entidad);
    /** Actualiza y devuelve el número de filas afectadas. */
    int actualizar(Tratamiento entidad);
    /** Elimina por ID y devuelve el número de filas afectadas. */
    int eliminar(Integer id);

    List<Tratamiento> listarPorConsulta(Integer idConsulta);
    BigDecimal calcularCosteTratamientos(Integer idConsulta);
}
