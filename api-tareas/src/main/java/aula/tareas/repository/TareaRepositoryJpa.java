package aula.tareas.repository;

import aula.tareas.entity.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepositoryJpa extends JpaRepository<TareaEntity, Long> {

}
