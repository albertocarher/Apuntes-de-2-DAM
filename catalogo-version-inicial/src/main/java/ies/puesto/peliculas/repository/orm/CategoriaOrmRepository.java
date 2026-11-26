package ies.puesto.peliculas.repository.orm;

import ies.puesto.peliculas.repository.ICategoriaRepository;
import ies.puesto.peliculas.model.*;
import ies.puesto.peliculas.model.error.RecursoAusente;
import ies.puesto.peliculas.entity.CategoriaEntity;
import ies.puesto.peliculas.mapper.CategoriaMapper;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class CategoriaOrmRepository implements ICategoriaRepository {
    private final CategoriaJpaRepository jpa;

    CategoriaOrmRepository(CategoriaJpaRepository jpa) {
        this.jpa = jpa;
    }

    private CategoriaEntity entidad(Long idCategoria) {
        return jpa.findById(idCategoria).orElseThrow(() -> new RecursoAusente("Categoria", idCategoria));
    }

    @Override
    public List<Categoria> buscarTodos() {
        return jpa.findAll(Sort.by("idCategoria")).stream().map(CategoriaMapper::toDomain).toList();
    }

    @Override
    public Categoria obtener(Long idCategoria) {
        return CategoriaMapper.toDomain(entidad(idCategoria));
    }
}
