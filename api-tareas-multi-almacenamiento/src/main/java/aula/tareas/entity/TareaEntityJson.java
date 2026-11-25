package aula.tareas.entity;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TareaEntityJson {
    @JsonAlias("id")
    private Long idTarea;
    @JsonAlias("titulo")
    private String nombreTarea;

   
}
