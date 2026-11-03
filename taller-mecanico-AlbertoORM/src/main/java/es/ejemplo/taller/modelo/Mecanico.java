package es.ejemplo.taller.modelo;
import java.math.BigDecimal;
public class Mecanico {
    private Integer idMecanico;
    private String nombre;
    private String especialidad;
    private BigDecimal tarifaHora;
    public Mecanico() {}
    public Mecanico(Integer idMecanico, String nombre, String especialidad, BigDecimal tarifaHora) {
        this.idMecanico = idMecanico;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tarifaHora = tarifaHora;
    }
    public Integer getIdMecanico() { return idMecanico; }
    public void setIdMecanico(Integer value) { this.idMecanico = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String value) { this.especialidad = value; }
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal value) { this.tarifaHora = value; }
}
