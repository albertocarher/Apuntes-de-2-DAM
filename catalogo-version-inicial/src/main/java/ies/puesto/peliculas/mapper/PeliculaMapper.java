package ies.puesto.peliculas.mapper;

import ies.puesto.peliculas.controller.api.*;
import ies.puesto.peliculas.entity.PeliculaEntity;
import ies.puesto.peliculas.model.*;

import java.util.List;

public final class PeliculaMapper {
    private PeliculaMapper() {
    }

    public static Pelicula toDomain(PeliculaEntity fila) {
        return new Pelicula(fila.getCodigoFila(), fila.getTituloFila(), fila.getAnioFila(),
                fila.getMinutosFila(), List.of(), List.of());
    }

    public static PeliculaResponse toApi(Pelicula modelo) {
        return new PeliculaResponse(modelo.identificador(), modelo.nombre(), modelo.anioLanzamiento(),
                modelo.minutosProyeccion(),
                modelo.categorias().stream().map(Categoria::nombre).sorted().toList(),
                modelo.actores().stream().map(Actor::nombre).sorted().toList());
    }
}
