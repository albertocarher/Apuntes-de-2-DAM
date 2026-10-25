package es.ejemplo.clinica.modelo;
import java.math.BigDecimal;

/** Entidad Veterinario. Clase Java mutable con constructores y accesores. */
public class Veterinario {
    private Integer id;
    private String nombre;
    private String especialidad;
    private BigDecimal tarifaHora;

    public Veterinario() { }

    public Veterinario(Integer id, String nombre, String especialidad, BigDecimal tarifaHora) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tarifaHora = tarifaHora;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal tarifaHora) { this.tarifaHora = tarifaHora; }

}
