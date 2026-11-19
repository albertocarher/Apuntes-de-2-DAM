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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/xml/tareas", produces = MediaType.APPLICATION_XML_VALUE)
@Tag(name="Tareas", description = "Operaciones crud de tareas en xml")
public class TareaControllerXml {

    private final ITareaService serviceXml;

    public TareaControllerXml(@Qualifier("tareaServiceXml")  ITareaService serviceXml) {
        this.serviceXml = serviceXml;
    }

    @GetMapping
    @Operation(summary = "Obtener todos las tareas desde el fichero xml")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<TareaRequest> listar() {
        return serviceXml.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    ResponseEntity<TareaRequest> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(serviceXml.buscar(id));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Crear una tarea en el fichero xml")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "Tarea no valida")
    })
    ResponseEntity<TareaRequest> crear(@Valid @RequestBody NuevaTarea peticion) {
        TareaRequest tareaRequest = serviceXml.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/xml/tareas/" + tareaRequest.id())).body(tareaRequest);
    }

    public record NuevaTarea(@NotBlank String titulo) {}

}