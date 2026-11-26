package ies.puesto.peliculas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "actores")
public class ActorEntity {
    @Id
    @Column(name = "id_actor")
    private String idActor;

    @Column(name = "nombre", nullable = false)
    private String nombreFila;

    protected ActorEntity() {
    }

    public ActorEntity(String idActor, String nombreFila) {
        this.idActor = idActor;
        editar(nombreFila);
    }

    public void editar(String nombreFila) {
        this.nombreFila = nombreFila;
    }

    public String getIdActor() {
        return idActor;
    }

    public String getNombreFila() {
        return nombreFila;
    }
}
