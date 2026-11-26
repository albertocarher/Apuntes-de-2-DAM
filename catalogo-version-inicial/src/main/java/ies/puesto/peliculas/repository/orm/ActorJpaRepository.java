package ies.puesto.peliculas.repository.orm;

import ies.puesto.peliculas.entity.ActorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorJpaRepository extends JpaRepository<ActorEntity, String> {
}
