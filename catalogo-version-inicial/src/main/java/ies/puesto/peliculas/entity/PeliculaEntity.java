package ies.puesto.peliculas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "peliculas")
public class PeliculaEntity {
    @Id
    @Column(name = "pelicula_codigo")
    private String codigoFila;

    @Column(name = "titulo_texto", nullable = false)
    private String tituloFila;

    @Column(name = "estreno_anio", nullable = false)
    private int anioFila;

    @Column(name = "duracion_minutos", nullable = false)
    private int minutosFila;

    protected PeliculaEntity() {
    }

    public PeliculaEntity(String codigoFila, String tituloFila, int anioFila, int minutosFila) {
        this.codigoFila = codigoFila;
        editar(tituloFila, anioFila, minutosFila);
    }

    public void editar(String tituloFila, int anioFila, int minutosFila) {
        this.tituloFila = tituloFila;
        this.anioFila = anioFila;
        this.minutosFila = minutosFila;
    }

    public String getCodigoFila() {
        return codigoFila;
    }

    public String getTituloFila() {
        return tituloFila;
    }

    public int getAnioFila() {
        return anioFila;
    }

    public int getMinutosFila() {
        return minutosFila;
    }
}
