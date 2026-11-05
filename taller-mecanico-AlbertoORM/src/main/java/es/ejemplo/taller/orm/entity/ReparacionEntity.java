package es.ejemplo.taller.orm.entity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "reparaciones")
public class ReparacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reparacion")
    private Integer idReparacion;

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "id_mecanico")
    private Integer idMecanico;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "horas_trabajo")
    private BigDecimal horasTrabajo;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula", insertable = false, updatable = false)
    private VehiculoEntity vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mecanico", insertable = false, updatable = false)
    private MecanicoEntity mecanico;

    @OneToMany(mappedBy = "reparacion", fetch = FetchType.LAZY)
    private List<PiezaReparacionEntity> piezas = new ArrayList<>();

    protected ReparacionEntity() {}
    public ReparacionEntity(Integer idReparacion, String matricula, Integer idMecanico, String descripcion, String fecha, BigDecimal horasTrabajo, String estado) {
        this.idReparacion = idReparacion;
        this.matricula = matricula;
        this.idMecanico = idMecanico;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.horasTrabajo = horasTrabajo;
        this.estado = estado;
    }

    public Integer getIdReparacion() {
        return idReparacion;
    }

    public void setIdReparacion(Integer value) {
        this.idReparacion = value;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String value) {
        this.matricula = value;
    }

    public Integer getIdMecanico() {
        return idMecanico;
    }

    public void setIdMecanico(Integer value) {
        this.idMecanico = value;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String value) {
        this.fecha = value;
    }

    public BigDecimal getHorasTrabajo() {
        return horasTrabajo;
    }

    public void setHorasTrabajo(BigDecimal value) {
        this.horasTrabajo = value;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String value) {
        this.estado = value;
    }

    public VehiculoEntity getVehiculo() {
        return vehiculo;
    }

    public MecanicoEntity getMecanico() {
        return mecanico;
    }

    public List<PiezaReparacionEntity> getPiezas() {
        return piezas;
    }
}