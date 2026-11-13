package es.examen.sportrent.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "alquileres")
public class AlquilerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fechaInicio")
    private String fechaInicio;

    @Column(name = "fechaFin")
    private String fechaFin;

    @Column(name = "fechaDevolucion")
    private String fechaDevolucion;
  
    private java.math.BigDecimal importeTotal;

}
