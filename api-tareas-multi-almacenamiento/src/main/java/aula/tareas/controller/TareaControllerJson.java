package aula.tareas.controller;

import aula.tareas.api.TareaRequest;
import aula.tareas.api.TareaResponse;
import aula.tareas.service.ITareaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TareaControllerJson {
    private final ITareaService servicio;

    public TareaControllerJson(@Qualifier("tareaServiceJson") ITareaService servicio) {
        this.servicio = servicio;
    }

    // TODO: Definir la ruta base /api/json/tareas.

    public List<TareaResponse> listar() {
        // TODO: Exponer el listado y convertir las tareas en respuestas.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public ResponseEntity<TareaResponse> buscar(long id) {
        // TODO: Exponer la consulta por ID y gestionar la ausencia de la tarea.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public ResponseEntity<TareaResponse> crear(TareaRequest request) {
        // TODO: Exponer el alta, validar el cuerpo y componer la respuesta HTTP.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public ResponseEntity<TareaResponse> actualizar(long id, TareaRequest request) {
        // TODO: Exponer la actualización y validar la petición.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public ResponseEntity<Void> eliminar(long id) {
        // TODO: Exponer el borrado y comunicar el resultado.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
