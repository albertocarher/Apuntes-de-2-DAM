package ies.puesto.peliculas.controller;

import ies.puesto.peliculas.service.CategoriaService;
import ies.puesto.peliculas.controller.api.*;
import ies.puesto.peliculas.mapper.CategoriaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import jakarta.validation.constraints.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Categorías")
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {
    private final CategoriaService servicio;

    public CategoriaController(CategoriaService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar categorías")
    @ApiResponse(responseCode = "200", description = "Consulta completada", useReturnTypeSchema = true)
    @GetMapping
    public List<CategoriaResponse> listar() {
        return servicio.listar().stream().map(CategoriaMapper::toApi).toList();
    }

    @Operation(summary = "Consultar categoría")
    @ApiResponse(responseCode = "200", description = "Consulta completada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Petición inválida", content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    @ApiResponse(responseCode = "404", description = "Recurso inexistente", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{idCategoria}")
    public CategoriaResponse obtener(@PathVariable @Positive Long idCategoria) {
        return CategoriaMapper.toApi(servicio.obtener(idCategoria));
    }
}
