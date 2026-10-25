package es.ejemplo.clinica.modelo;
import java.math.BigDecimal;

/** Entidad Consulta. Clase Java mutable con constructores y accesores. */
public class Consulta {
    private Integer id;
    private Integer idMascota;
    private Integer idVeterinario;
    private String fecha;
    private BigDecimal horas;
    private String estado;

    public Consulta() { }

    public Consulta(Integer id, Integer idMascota, Integer idVeterinario, String fecha, BigDecimal horas, String estado) {
        this.id = id;
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.horas = horas;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdMascota() { return idMascota; }
    public void setIdMascota(Integer idMascota) { this.idMascota = idMascota; }

    public Integer getIdVeterinario() { return idVeterinario; }
    public void setIdVeterinario(Integer idVeterinario) { this.idVeterinario = idVeterinario; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public BigDecimal getHoras() { return horas; }
    public void setHoras(BigDecimal horas) { this.horas = horas; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

}
