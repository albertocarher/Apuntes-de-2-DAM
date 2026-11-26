package ies.puesto.peliculas.controller.api;

import java.util.List;

public record PeliculaResponse(String codigo, String titulo, int anioEstreno, int duracionMinutos,
                              List<String> categorias, List<String> reparto) {
}
