package ies.puesto.peliculas.service;

import ies.puesto.peliculas.repository.ICategoriaRepository;
import ies.puesto.peliculas.model.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final ICategoriaRepository repositorio;

    public CategoriaService(ICategoriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Categoria> listar() {
        return repositorio.buscarTodos();
    }

    public Categoria obtener(Long idCategoria) {
        return repositorio.obtener(idCategoria);
    }
}
