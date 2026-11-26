package ies.puesto.peliculas.controller;

import ies.puesto.peliculas.service.PeliculaService;
import ies.puesto.peliculas.controller.api.*;
import ies.puesto.peliculas.mapper.PeliculaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import jakarta.validation.constraints.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Películas")
@RestController
@RequestMapping("/api/v1/peliculas")
public class PeliculaController {
    private final PeliculaService servicio;

    public PeliculaController(PeliculaService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar películas")
    @ApiResponse(responseCode = "200", description = "Consulta completada", useReturnTypeSchema = true)
    @GetMapping
    public List<PeliculaResponse> listar() {
        return servicio.listar().stream().map(PeliculaMapper::toApi).toList();
    }

    @Operation(summary = "Consultar película")
    @ApiResponse(responseCode = "200", description = "Consulta completada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Petición inválida", content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    @ApiResponse(responseCode = "404", description = "Recurso inexistente", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{codigo}")
    public PeliculaResponse obtener(@PathVariable @Pattern(regexp = "PEL[0-9]{3}") String codigo) {
        return PeliculaMapper.toApi(servicio.obtener(codigo));
    }
}
