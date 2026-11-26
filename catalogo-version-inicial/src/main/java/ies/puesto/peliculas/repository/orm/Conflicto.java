package ies.puesto.peliculas.repository.orm;

public class Conflicto extends RuntimeException {
    public Conflicto(String mensaje) {
        super(mensaje);
    }
}
