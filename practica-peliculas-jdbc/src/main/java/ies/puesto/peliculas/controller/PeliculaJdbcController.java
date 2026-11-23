package ies.puesto.peliculas.controller;

import ies.puesto.peliculas.controller.api.PeliculaEdicionRequest;
import ies.puesto.peliculas.controller.api.PeliculaRequest;
import ies.puesto.peliculas.controller.api.PeliculaResponse;
import ies.puesto.peliculas.controller.mapper.PeliculaApiMapper;
import ies.puesto.peliculas.service.PeliculaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Profile("jdbc")
@RequestMapping("/api/v1/jdbc/peliculas")
public class PeliculaJdbcController {

    private final PeliculaService servicio;
    private final PeliculaApiMapper mapper;

    public PeliculaJdbcController(PeliculaService servicio, PeliculaApiMapper mapper) {
        this.servicio = servicio;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PeliculaResponse> listar() {
        return mapper.aRespuestas(servicio.listar());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> obtener(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo) {
        return servicio.obtener(codigo)
                .<ResponseEntity<?>>map(pelicula -> ResponseEntity.ok(mapper.aRespuesta(pelicula)))
                .orElseGet(() -> noExiste(codigo));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PeliculaRequest peticion) {
        return servicio.crear(mapper.aDatosCreacion(peticion))
                .<ResponseEntity<?>>map(pelicula -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(mapper.aRespuesta(pelicula)))
                .orElseGet(() -> duplicada(peticion.codigo()));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> actualizar(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo,
                                        @Valid @RequestBody PeliculaEdicionRequest peticion) {
        return servicio.actualizar(codigo, mapper.aDatosEdicion(peticion))
                .<ResponseEntity<?>>map(pelicula -> ResponseEntity.ok(mapper.aRespuesta(pelicula)))
                .orElseGet(() -> noExiste(codigo));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminar(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo) {
        if (servicio.eliminar(codigo)) {
            return ResponseEntity.noContent().build();
        }
        return noExiste(codigo);
    }

    private ResponseEntity<?> noExiste(String codigo) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "No existe la película " + codigo);
        problema.setTitle("Película no encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problema);
    }

    private ResponseEntity<?> duplicada(String codigo) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Ya existe la película " + codigo);
        problema.setTitle("Película duplicada");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problema);
    }
}
