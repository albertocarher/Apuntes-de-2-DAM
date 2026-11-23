package ies.puesto.peliculas.service;

import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.repository.PeliculaRepository;
import ies.puesto.peliculas.service.mapper.PeliculaDominioMapper;
import ies.puesto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puesto.peliculas.service.modelo.DatosEdicionPelicula;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeliculaService {

    private final PeliculaRepository repositorio;
    private final PeliculaDominioMapper mapper;

    public PeliculaService(PeliculaRepository repositorio, PeliculaDominioMapper mapper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    public List<Pelicula> listar() {
        return repositorio.buscarTodas();
    }

    public Optional<Pelicula> obtener(String codigo) {
        return repositorio.buscarPorCodigo(codigo);
    }

    public Optional<Pelicula> crear(DatosCreacionPelicula datos) {
        Pelicula pelicula = mapper.aPelicula(datos);
        boolean creada = repositorio.insertarSiAusente(pelicula);
        return creada ? Optional.of(pelicula) : Optional.empty();
    }

    public Optional<Pelicula> actualizar(String codigo, DatosEdicionPelicula datos) {
        Pelicula pelicula = mapper.aPelicula(codigo, datos);
        boolean actualizada = repositorio.actualizarSiExiste(pelicula);
        return actualizada ? Optional.of(pelicula) : Optional.empty();
    }

    public boolean eliminar(String codigo) {
        return repositorio.eliminar(codigo);
    }
}
