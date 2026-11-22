package ies.puesto.peliculas.controller.api;

public record PeliculaEdicionRequest(
        String titulo,
        Integer anioEstreno,
        Integer duracionMinutos) {
}
