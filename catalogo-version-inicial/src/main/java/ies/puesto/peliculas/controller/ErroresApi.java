package ies.puesto.peliculas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import ies.puesto.peliculas.model.error.RecursoAusente;

@RestControllerAdvice
class ErroresApi {
    @ExceptionHandler(RecursoAusente.class)
    ProblemDetail ausente(RecursoAusente error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }
}
