package ies.puesto.peliculas.controller;

import ies.puesto.peliculas.service.ActorService;
import ies.puesto.peliculas.controller.api.*;
import ies.puesto.peliculas.mapper.ActorMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import jakarta.validation.constraints.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Actores - V1")
@RestController
@RequestMapping("/api/v1/actores")
public class ActorV1Controller {
    private final ActorService servicio;

    public ActorV1Controller(ActorService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar actores")
    @ApiResponse(responseCode = "200", description = "Consulta completada", useReturnTypeSchema = true)
    @GetMapping
    public List<ActorResponse> listar() {
        return servicio.listar().stream().map(ActorMapper::toApi).toList();
    }

    @Operation(summary = "Consultar actor")
    @ApiResponse(responseCode = "200", description = "Consulta completada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Petición inválida", content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    @ApiResponse(responseCode = "404", description = "Recurso inexistente", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{idActor}")
    public ActorResponse obtener(@PathVariable @Pattern(regexp = "[A-Za-z0-9-]{1,32}") String idActor) {
        return ActorMapper.toApi(servicio.obtener(idActor));
    }
}
