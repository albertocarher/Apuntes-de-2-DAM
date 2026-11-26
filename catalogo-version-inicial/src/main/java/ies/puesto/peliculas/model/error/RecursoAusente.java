package ies.puesto.peliculas.model.error;


public class RecursoAusente extends RuntimeException {
    public RecursoAusente(String tipo, Object id) {
        super(tipo + " inexistente: " + id);
    }
}
