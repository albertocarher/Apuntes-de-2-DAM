package ies.puesto.peliculas.service;

import ies.puesto.peliculas.repository.IActorRepository;
import ies.puesto.peliculas.model.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("actorService")
@Transactional(readOnly = true)
public class ActorService implements IActorService{
    private final IActorRepository repositorio;

    public ActorService(IActorRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Actor> listar() {
        return repositorio.buscarTodos();
    }

    public Actor obtener(String idActor) {
        return repositorio.obtener(idActor);
    }

    @Override
    public Actor crear(String idActor, DatosActor datos) {
        return repositorio.crear(idActor,datos);
    }

    @Override
    public Actor editar(String idActor, DatosActor datos) {
        return repositorio.editar(idActor,datos);
    }

    @Override
    public void eliminar(String idActor) {
        repositorio.eliminar(idActor);
    }


}
