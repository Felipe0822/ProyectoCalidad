package com.institucion.reservas.dto.reserva;

import com.institucion.reservas.entity.enums.EstadoReserva;

import java.time.LocalDateTime;

public record ReservaResponse(
        Long id,
        UsuarioResumenResponse usuario,
        RecursoResumenResponse recurso,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        EstadoReserva estado,
        LocalDateTime fechaCreacion
) {
}
