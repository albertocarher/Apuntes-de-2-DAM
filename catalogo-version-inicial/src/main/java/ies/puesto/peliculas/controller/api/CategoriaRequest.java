package ies.puesto.peliculas.controller.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CategoriaRequest(
        @Schema(example = "Aventuras") @NotBlank @Size(max = 120) String nombre) {
}
