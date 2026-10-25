package com.institucion.reservas.dto.reserva;

import java.time.LocalDateTime;
import java.util.List;

public record DisponibilidadResponse(
        Long recursoId,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        boolean disponible,
        String motivo,
        List<ConflictoReservaResponse> reservasConflictivas
) {
}
