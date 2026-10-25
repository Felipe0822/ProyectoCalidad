package com.institucion.reservas.dto.recurso;

import com.institucion.reservas.entity.enums.EstadoRecurso;

public record RecursoResponse(
        Long id,
        String nombre,
        String descripcion,
        String tipo,
        Integer capacidad,
        EstadoRecurso estado
) {
}
