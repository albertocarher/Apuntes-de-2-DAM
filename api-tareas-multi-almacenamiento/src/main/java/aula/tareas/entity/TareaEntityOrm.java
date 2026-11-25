package aula.tareas.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class TareaEntityOrm {

    @Id
    private Long idTarea;

    @Column
    private String nombreTarea;

    
}
