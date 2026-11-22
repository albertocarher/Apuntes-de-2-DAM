package ies.puesto.peliculas.service.mapper;

import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puesto.peliculas.service.modelo.DatosEdicionPelicula;
import org.springframework.stereotype.Component;

@Component
public class PeliculaDominioMapper {

    public Pelicula aPelicula(DatosCreacionPelicula datos) {
        return new Pelicula(
                datos.referenciaSolicitada(),
                datos.nombreSolicitado(),
                datos.anioSolicitado(),
                datos.minutosSolicitados()
        );
    }

    public Pelicula aPelicula(String identificador, DatosEdicionPelicula datos) {
        return new Pelicula(
                identificador,
                datos.nombreSolicitado(),
                datos.anioSolicitado(),
                datos.minutosSolicitados()
        );
    }
}
