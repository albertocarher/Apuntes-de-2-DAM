package ies.puesto.peliculas.controller.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ActorRequest(
        @Schema(example = "PAS000003") @NotNull @Pattern(regexp = "[A-Za-z0-9-]{1,32}") String idActor,
        @Schema(example = "Luna Mar") @NotBlank @Size(max = 120) String nombre) {
}
