package es.ejemplo.taller.modelo;
import java.math.BigDecimal;
public class PiezaReparacion {
    private Integer idPieza;
    private Integer idReparacion;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    public PiezaReparacion() {}
    public PiezaReparacion(Integer idPieza, Integer idReparacion, String nombre, Integer cantidad, BigDecimal precioUnitario) {
        this.idPieza = idPieza;
        this.idReparacion = idReparacion;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    public Integer getIdPieza() { return idPieza; }
    public void setIdPieza(Integer value) { this.idPieza = value; }
    public Integer getIdReparacion() { return idReparacion; }
    public void setIdReparacion(Integer value) { this.idReparacion = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer value) { this.cantidad = value; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal value) { this.precioUnitario = value; }
}
