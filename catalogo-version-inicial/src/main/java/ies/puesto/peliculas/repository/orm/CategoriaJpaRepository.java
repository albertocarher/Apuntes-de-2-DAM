package ies.puesto.peliculas.repository.orm;

import ies.puesto.peliculas.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {
}
