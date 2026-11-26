package ies.puesto.peliculas.model;

import java.util.List;

public record Pelicula(String identificador, String nombre, int anioLanzamiento,
                       int minutosProyeccion, List<Actor> actores, List<Categoria> categorias) {
}
