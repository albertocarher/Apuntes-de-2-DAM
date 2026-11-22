package ies.puesto.peliculas.repository.jdbc;

import ies.puesto.peliculas.model.Pelicula;
import org.springframework.stereotype.Component;

@Component
class PeliculaEntityMapper {

    PeliculaEntity aEntity(Pelicula pelicula) {
        return new PeliculaEntity(
                pelicula.getIdentificador(),
                pelicula.getNombre(),
                pelicula.getAnioLanzamiento(),
                pelicula.getMinutosProyeccion()
        );
    }

    Pelicula aPelicula(PeliculaEntity fila) {
        return new Pelicula(
                fila.getCodigoFila(),
                fila.getTituloFila(),
                fila.getAnioFila(),
                fila.getMinutosFila()
        );
    }
}
