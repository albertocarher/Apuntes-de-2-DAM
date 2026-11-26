package ies.puesto.peliculas.controller.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record PeliculaRequest(
        @Schema(example = "PEL003") @NotNull @Pattern(regexp = "PEL[0-9]{3}") String codigo,
        @Schema(example = "La isla azul") @NotBlank @Size(max = 120) String titulo,
        @Schema(example = "2021") @NotNull @Min(1888) @Max(2100) Integer anioEstreno,
        @Schema(example = "90") @NotNull @Min(1) @Max(1000) Integer duracionMinutos) {
}
