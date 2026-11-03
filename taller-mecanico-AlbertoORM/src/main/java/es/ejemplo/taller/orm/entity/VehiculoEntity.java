package es.ejemplo.taller.orm.entity;

import es.ejemplo.taller.orm.entity.ClienteEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "vehiculos")

public class VehiculoEntity {
    @Id
    @Column(name = "matricula")
    private String matricula;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private ClienteEntity cliente;

    protected VehiculoEntity() {}
    // Añade constructor, getters y setters como en el código entregado.
}