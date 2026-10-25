package com.institucion.reservas.dto.reserva;

import java.time.LocalDateTime;

public record ConflictoReservaResponse(
        Long id,
        Long usuarioId,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
) {
}
