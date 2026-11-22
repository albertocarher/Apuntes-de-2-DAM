package ies.puesto.peliculas.controller.api;

public record PeliculaRequest(
        String codigo,
        String titulo,
        Integer anioEstreno,
        Integer duracionMinutos) {
}
