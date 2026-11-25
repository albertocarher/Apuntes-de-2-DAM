package aula.tareas.repository;

import aula.tareas.entity.TareaEntityOrm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITareaRepositoryJpa extends JpaRepository<TareaEntityOrm, Long> {
}
