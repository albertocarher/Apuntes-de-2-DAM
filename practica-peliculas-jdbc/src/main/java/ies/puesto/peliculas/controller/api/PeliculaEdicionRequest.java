package ies.puesto.peliculas.controller.api;

import jakarta.validation.constraints.*;

public record PeliculaEdicionRequest(
        @NotBlank @Size(max = 120) String titulo,
        @NotNull @Min(1888) @Max(2100) Integer anioEstreno,
        @NotNull @Min(1) @Max(1000) Integer duracionMinutos) {
}
