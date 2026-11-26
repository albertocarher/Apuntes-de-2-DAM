package ies.puesto.peliculas.mapper;

import ies.puesto.peliculas.controller.api.*;
import ies.puesto.peliculas.entity.CategoriaEntity;
import ies.puesto.peliculas.model.*;

public final class CategoriaMapper {
    private CategoriaMapper() {
    }

    public static Categoria toDomain(CategoriaEntity fila) {
        return new Categoria(fila.getIdCategoria(), fila.getNombreFila());
    }

    public static CategoriaResponse toApi(Categoria modelo) {
        return new CategoriaResponse(modelo.identificador(), modelo.nombre());
    }
}
