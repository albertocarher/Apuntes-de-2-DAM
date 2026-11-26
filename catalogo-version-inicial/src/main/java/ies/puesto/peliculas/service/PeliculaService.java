package ies.puesto.peliculas.service;

import ies.puesto.peliculas.repository.IPeliculaRepository;
import ies.puesto.peliculas.model.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PeliculaService {
    private final IPeliculaRepository repositorio;

    public PeliculaService(IPeliculaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Pelicula> listar() {
        return repositorio.buscarTodos();
    }

    public Pelicula obtener(String codigo) {
        return repositorio.obtener(codigo);
    }
}
