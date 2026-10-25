package com.institucion.reservas.dto.recurso;

import com.institucion.reservas.entity.enums.EstadoRecurso;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRecursoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoRecurso estado
) {
}
