package ies.puesto.peliculas.repository;

import ies.puesto.peliculas.model.Categoria;
import java.util.List;

public interface ICategoriaRepository {
    List<Categoria> buscarTodos();
    Categoria obtener(Long idCategoria);
}
