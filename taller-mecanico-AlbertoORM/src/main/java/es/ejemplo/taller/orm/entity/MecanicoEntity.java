package es.ejemplo.taller.orm.entity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "mecanicos")
public class MecanicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mecanico")
    private Integer idMecanico;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "tarifa_hora")
    private BigDecimal tarifaHora;

    @OneToMany(mappedBy = "mecanico", fetch = FetchType.LAZY)
    private List<ReparacionEntity> reparaciones = new ArrayList<>();

    protected MecanicoEntity() {}
    public MecanicoEntity(Integer idMecanico, String nombre, String especialidad, BigDecimal tarifaHora) {
        this.idMecanico = idMecanico;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tarifaHora = tarifaHora;
    }

    public Integer getIdMecanico() {
        return idMecanico;
    }

    public void setIdMecanico(Integer value) {
        this.idMecanico = value;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        this.nombre = value;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String value) {
        this.especialidad = value;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal value) {
        this.tarifaHora = value;
    }

    public List<ReparacionEntity> getReparaciones() {
        return reparaciones;
    }
}