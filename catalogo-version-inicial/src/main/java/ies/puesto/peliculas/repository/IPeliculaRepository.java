package ies.puesto.peliculas.repository;

import ies.puesto.peliculas.model.Pelicula;
import java.util.List;

public interface IPeliculaRepository {
    List<Pelicula> buscarTodos();
    Pelicula obtener(String codigo);
}
