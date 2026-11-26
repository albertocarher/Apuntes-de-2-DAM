package ies.puesto.peliculas.repository;

import ies.puesto.peliculas.model.Actor;
import ies.puesto.peliculas.model.DatosActor;

import java.util.List;

public interface IActorRepository {
    /**
     * Funcion que devuelve todos los actores
     * @return Lista de actores
     */
    List<Actor> buscarTodos();

    /**
     * Funcion para obtener un actor según su id
     * @param idActor identificador del actor
     * @return Devuelve un actor
     */
    Actor obtener(String idActor);

    /**
     * Funcion para eliminar un actor según su id
     * @param idActor identificador del actor
     */
    void eliminar(String idActor);

    /**
     *
     * @param idActor
     * @param datos
     * @return
     */
    Actor editar(String idActor, DatosActor datos);

    /**
     *
     * @param idActor
     * @param datos
     * @return
     */
    Actor crear(String idActor, DatosActor datos);
}
