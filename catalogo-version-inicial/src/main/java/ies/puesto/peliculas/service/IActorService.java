package ies.puesto.peliculas.service;

import ies.puesto.peliculas.model.Actor;
import ies.puesto.peliculas.model.DatosActor;

import java.util.List;

public interface IActorService {
    public List<Actor> listar();
    public Actor obtener(String idActor);
    public Actor crear (String idActor, DatosActor datos);
    public Actor editar(String idActor, DatosActor datos);
    public void eliminar(String idActor);
}
