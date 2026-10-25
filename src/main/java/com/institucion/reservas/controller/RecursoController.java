package com.institucion.reservas.controller;

import com.institucion.reservas.dto.recurso.CambioEstadoRecursoRequest;
import com.institucion.reservas.dto.recurso.RecursoRequest;
import com.institucion.reservas.dto.recurso.RecursoResponse;
import com.institucion.reservas.dto.reserva.DisponibilidadResponse;
import com.institucion.reservas.service.DisponibilidadService;
import com.institucion.reservas.service.RecursoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/recursos")
@RequiredArgsConstructor
@Validated
public class RecursoController {

    private final RecursoService recursoService;
    private final DisponibilidadService disponibilidadService;

    @PostMapping
    public ResponseEntity<RecursoResponse> crear(@Valid @RequestBody RecursoRequest request) {
        RecursoResponse response = recursoService.crear(request);
        return ResponseEntity
                .created(URI.create("/recursos/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<RecursoResponse> listar() {
        return recursoService.listar();
    }

    @GetMapping("/{id}")
    public RecursoResponse obtener(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id
    ) {
        return recursoService.obtener(id);
    }

    @PutMapping("/{id}")
    public RecursoResponse actualizar(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id,
            @Valid @RequestBody RecursoRequest request
    ) {
        return recursoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public RecursoResponse cambiarEstado(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id,
            @Valid @RequestBody CambioEstadoRecursoRequest request
    ) {
        return recursoService.cambiarEstado(id, request);
    }

    @GetMapping("/{id}/disponibilidad")
    public DisponibilidadResponse consultarDisponibilidad(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin
    ) {
        return disponibilidadService.consultar(id, fechaInicio, fechaFin);
    }
}
