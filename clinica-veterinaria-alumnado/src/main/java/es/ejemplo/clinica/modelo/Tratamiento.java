package es.ejemplo.clinica.modelo;
import java.math.BigDecimal;

/** Entidad Tratamiento. Clase Java mutable con constructores y accesores. */
public class Tratamiento {
    private Integer id;
    private Integer idConsulta;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    public Tratamiento() { }

    public Tratamiento(Integer id, Integer idConsulta, String descripcion, Integer cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.idConsulta = idConsulta;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Integer idConsulta) { this.idConsulta = idConsulta; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

}
