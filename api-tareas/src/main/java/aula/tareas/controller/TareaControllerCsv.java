package aula.tareas.controller;

import java.net.URI;
import java.util.List;


import aula.tareas.modelo.Tarea;
import aula.tareas.service.TareaServiceCsv;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/csv/tareas")
class TareaControllerCsv {
    private final TareaServiceCsv servicio;

    TareaControllerCsv(@Qualifier("tareaServiceCsv") TareaServiceCsv servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    List<Tarea> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    ResponseEntity<Tarea> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(servicio.buscar(id));
    }

    @PostMapping
    ResponseEntity<Tarea> crear(@Valid @RequestBody NuevaTarea peticion) {
        Tarea tarea = servicio.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/csv/tareas/" + tarea.id())).body(tarea);
    }

    public record NuevaTarea(@NotBlank String titulo) {}
}