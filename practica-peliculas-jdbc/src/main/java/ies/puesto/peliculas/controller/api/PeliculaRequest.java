package ies.puesto.peliculas.controller.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PeliculaRequest(
        @NotNull @Pattern(regexp = "^PEL[0-9]{3}$") String codigo,
        @NotBlank @Size(max = 120) String titulo,
        @NotNull @Min(1888) @Max(2100) Integer anioEstreno,
        @NotNull @Min(1) @Max(1000) Integer duracionMinutos) {
}
