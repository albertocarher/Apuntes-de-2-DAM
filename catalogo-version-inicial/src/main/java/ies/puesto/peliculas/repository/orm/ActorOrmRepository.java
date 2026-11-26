package ies.puesto.peliculas.repository.orm;

import ies.puesto.peliculas.repository.IActorRepository;
import ies.puesto.peliculas.model.*;
import ies.puesto.peliculas.model.error.RecursoAusente;
import ies.puesto.peliculas.entity.ActorEntity;
import ies.puesto.peliculas.mapper.ActorMapper;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository("actorOrmRepository")
class ActorOrmRepository implements IActorRepository {
    private final ActorJpaRepository jpa;

    ActorOrmRepository(ActorJpaRepository jpa) {
        this.jpa = jpa;
    }

    private ActorEntity entidad(String idActor) {
        return jpa.findById(idActor).orElseThrow(() -> new RecursoAusente("Actor", idActor));
    }

    @Override
    public List<Actor> buscarTodos() {
        return jpa.findAll(Sort.by("idActor")).stream().map(ActorMapper::toDomain).toList();
    }

    @Override
    public Actor obtener(String idActor) {
        return ActorMapper.toDomain(entidad(idActor));
    }

    @Override
    public void eliminar(String idActor) {
        jpa.delete(entidad(idActor));//deleteById(ActorId)
    }

    @Override
    public Actor editar(String idActor, DatosActor datos) {
        if(!jpa.existsById(idActor)){
            throw new RecursoAusente("Actor", idActor);
        }
        ActorEntity entity = ActorMapper.toEntity(idActor, datos);
        return ActorMapper.toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Actor crear(String idActor, DatosActor datos) {
        if(jpa.existsById(idActor)){
            throw new Conflicto("Ya existe el actor con ese identificador: " + idActor);
        }
        //ActorEntity actorEntity = entidad(idActor);
        ActorEntity entity = ActorMapper.toEntity(idActor,datos);
        jpa.saveAndFlush(entity);
        return ActorMapper.toDomain(entity);

    }
}
