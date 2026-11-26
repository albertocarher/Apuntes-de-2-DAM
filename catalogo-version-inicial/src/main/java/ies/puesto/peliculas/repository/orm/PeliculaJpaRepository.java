package ies.puesto.peliculas.repository.orm;

import ies.puesto.peliculas.entity.PeliculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeliculaJpaRepository extends JpaRepository<PeliculaEntity, String> {
}
