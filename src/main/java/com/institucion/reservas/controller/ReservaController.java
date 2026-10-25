package com.institucion.reservas.controller;

import com.institucion.reservas.dto.reserva.ReservaRequest;
import com.institucion.reservas.dto.reserva.ReservaResponse;
import com.institucion.reservas.service.ReservaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
@Validated
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.crear(request);
        return ResponseEntity
                .created(URI.create("/reservas/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ReservaResponse obtener(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id
    ) {
        return reservaService.obtener(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @PathVariable @Positive(message = "El id debe ser mayor que cero") Long id
    ) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ReservaResponse> listarPorUsuario(
            @PathVariable @Positive(message = "El id del usuario debe ser mayor que cero") Long usuarioId
    ) {
        return reservaService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/recurso/{recursoId}")
    public List<ReservaResponse> listarPorRecurso(
            @PathVariable @Positive(message = "El id del recurso debe ser mayor que cero") Long recursoId
    ) {
        return reservaService.listarPorRecurso(recursoId);
    }

    @GetMapping
    public List<ReservaResponse> listarPorFecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha
    ) {
        return reservaService.listarPorFecha(fecha);
    }
}
