package aula.tareas.api;

import jakarta.validation.constraints.NotBlank;

public record TareaRequest(Long id, @NotBlank String titulo ) {

    public TareaRequest{
        if(titulo != null)
            titulo = titulo.strip();
    }
}
