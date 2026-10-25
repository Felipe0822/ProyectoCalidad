package com.institucion.reservas.dto.reserva;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ReservaRequest(
        @NotNull(message = "El usuario es obligatorio")
        @Positive(message = "El id del usuario debe ser mayor que cero")
        Long usuarioId,

        @NotNull(message = "El recurso es obligatorio")
        @Positive(message = "El id del recurso debe ser mayor que cero")
        Long recursoId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDateTime fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDateTime fechaFin
) {
}
