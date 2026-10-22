package es.ejemplo.taller.modelo;
import java.math.BigDecimal;
public class Reparacion {
    private Integer idReparacion;
    private String matricula;
    private Integer idMecanico;
    private String descripcion;
    private String fecha;
    private BigDecimal horasTrabajo;
    private String estado;
    public Reparacion() {}
    public Reparacion(Integer idReparacion, String matricula, Integer idMecanico, String descripcion, String fecha, BigDecimal horasTrabajo, String estado) {
        this.idReparacion = idReparacion;
        this.matricula = matricula;
        this.idMecanico = idMecanico;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.horasTrabajo = horasTrabajo;
        this.estado = estado;
    }
    public Integer getIdReparacion() { return idReparacion; }
    public void setIdReparacion(Integer value) { this.idReparacion = value; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String value) { this.matricula = value; }
    public Integer getIdMecanico() { return idMecanico; }
    public void setIdMecanico(Integer value) { this.idMecanico = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion = value; }
    public String getFecha() { return fecha; }
    public void setFecha(String value) { this.fecha = value; }
    public BigDecimal getHorasTrabajo() { return horasTrabajo; }
    public void setHorasTrabajo(BigDecimal value) { this.horasTrabajo = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
}
