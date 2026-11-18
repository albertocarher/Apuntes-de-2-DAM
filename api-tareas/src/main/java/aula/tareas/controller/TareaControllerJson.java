package aula.tareas.controller;

import aula.tareas.modelo.Tarea;
import aula.tareas.service.ITareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/json/tareas")
@Tag(name="Tareas", description = "Operaciones crud de tareas en json")
public class TareaControllerJson {

    private final ITareaService serviceJson;

    public TareaControllerJson(@Qualifier("tareaServiceJson")  ITareaService serviceJson) {
        this.serviceJson = serviceJson;
    }

    @GetMapping
    @Operation(summary = "Obtener todos las tareas desde el fichero json")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<Tarea> listar() {
        return serviceJson.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    ResponseEntity<Tarea> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(serviceJson.buscar(id));
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "Tarea existe")
    })
    ResponseEntity<Tarea> crear(@Valid @RequestBody NuevaTarea peticion) {
        Tarea tarea = serviceJson.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/json/tareas/" + tarea.id())).body(tarea);
    }

    public record NuevaTarea(@NotBlank String titulo) {}

}