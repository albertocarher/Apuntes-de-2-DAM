package aula.tareas.controller;

import aula.tareas.api.TareaRequest;
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
import java.util.List;

@RestController
@RequestMapping("/api/jdbc/tareas")
@Tag(name="TareasJdbc", description = "Operaciones crud de tareas en jdbc")
public class TareaControllerJdbc {

    private final ITareaService servicejdbc;

    public TareaControllerJdbc(@Qualifier("tareaServiceJdbc")  ITareaService servicejdbc) {
        this.servicejdbc = servicejdbc;
    }

    @GetMapping
    @Operation(summary = "Obtener todos las tareas")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<TareaRequest> listar() {
        return servicejdbc.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    ResponseEntity<TareaRequest> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(servicejdbc.buscar(id));
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "Tarea existe")
    })
    ResponseEntity<TareaRequest> crear(@Valid @RequestBody TareaControllerJdbc.NuevaTarea peticion) {
        TareaRequest tareaRequest = servicejdbc.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/jdbc/tareas/" + tareaRequest.id())).body(tareaRequest);
    }

    @PutMapping("/{id}")
    ResponseEntity <TareaRequest> update(@PathVariable long id, @Valid @RequestBody TareaRequest tarea){
        return null;
    }

    public record NuevaTarea(@NotBlank String titulo) {}

}