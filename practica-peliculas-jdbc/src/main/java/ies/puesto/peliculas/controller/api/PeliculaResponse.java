package ies.puesto.peliculas.controller.api;

public record PeliculaResponse(
        String codigo,
        String titulo,
        int anioEstreno,
        int duracionMinutos) {
}