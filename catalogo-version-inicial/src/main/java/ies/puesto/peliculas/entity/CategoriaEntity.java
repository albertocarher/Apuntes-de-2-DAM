package ies.puesto.peliculas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre", nullable = false)
    private String nombreFila;

    protected CategoriaEntity() {
    }

    public CategoriaEntity(String nombreFila) {
        editar(nombreFila);
    }

    public void editar(String nombreFila) {
        this.nombreFila = nombreFila;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public String getNombreFila() {
        return nombreFila;
    }
}
