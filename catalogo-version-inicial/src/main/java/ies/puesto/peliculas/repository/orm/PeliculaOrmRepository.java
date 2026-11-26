package ies.puesto.peliculas.repository.orm;

import ies.puesto.peliculas.repository.IPeliculaRepository;
import ies.puesto.peliculas.model.*;
import ies.puesto.peliculas.model.error.RecursoAusente;
import ies.puesto.peliculas.entity.PeliculaEntity;
import ies.puesto.peliculas.mapper.PeliculaMapper;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class PeliculaOrmRepository implements IPeliculaRepository {
    private final PeliculaJpaRepository jpa;
    PeliculaOrmRepository(PeliculaJpaRepository jpa) {
        this.jpa = jpa;
    }

    private PeliculaEntity entidad(String codigo) {
        return jpa.findById(codigo).orElseThrow(() -> new RecursoAusente("Pelicula", codigo));
    }

    @Override
    public List<Pelicula> buscarTodos() {
        return jpa.findAll(Sort.by("codigoFila")).stream().map(PeliculaMapper::toDomain).toList();
    }

    @Override
    public Pelicula obtener(String codigo) {
        return PeliculaMapper.toDomain(entidad(codigo));
    }
}
