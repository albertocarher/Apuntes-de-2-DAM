package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import aula.tareas.entity.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositoryOrm")
public class TareaRepositoryOrm implements ITareaRepository{

    private final TareaRepositoryJpa tareaRepositoryJpa;

    public TareaRepositoryOrm(TareaRepositoryJpa tareaRepositoryJpa) {
        this.tareaRepositoryJpa = tareaRepositoryJpa;
    }

    @Override
    public TareaRequest create(String titulo) {
        TareaEntity tareaEntitySave = tareaRepositoryJpa.save(new TareaEntity(null,titulo));
        return null;
    }

    @Override
    public boolean delete(long identificador) {
        tareaRepositoryJpa.deleteById(identificador);
        return false;
    }

    @Override
    public List<TareaRequest> findAll() {
        return null;
    }

    @Override
    public TareaRequest findById(long identificador) {
        return null;
    }

    @Override
    public TareaRequest update(TareaRequest tareaRequest) {
        return null;
    }
}
