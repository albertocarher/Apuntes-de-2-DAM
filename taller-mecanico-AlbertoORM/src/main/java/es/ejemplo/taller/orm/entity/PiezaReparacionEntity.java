package es.ejemplo.taller.orm.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "piezas_reparacion")
public class PiezaReparacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pieza")
    private Integer idPieza;

    @Column(name = "id_reparacion")
    private Integer idReparacion;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reparacion", insertable = false, updatable = false)
    private ReparacionEntity reparacion;

    protected PiezaReparacionEntity() {}
    public PiezaReparacionEntity(Integer idPieza, Integer idReparacion, String nombre, Integer cantidad, BigDecimal precioUnitario) {
        this.idPieza = idPieza;
        this.idReparacion = idReparacion;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Integer getIdPieza() {
        return idPieza;
    }

    public void setIdPieza(Integer value) {
        this.idPieza = value;
    }

    public Integer getIdReparacion() {
        return idReparacion;
    }

    public void setIdReparacion(Integer value) {
        this.idReparacion = value;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        this.nombre = value;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer value) {
        this.cantidad = value;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal value) {
        this.precioUnitario = value;
    }

    public ReparacionEntity getReparacion() {
        return reparacion;
    }
}