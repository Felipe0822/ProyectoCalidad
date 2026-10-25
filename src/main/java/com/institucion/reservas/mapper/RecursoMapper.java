package com.institucion.reservas.mapper;

import com.institucion.reservas.dto.recurso.RecursoRequest;
import com.institucion.reservas.dto.recurso.RecursoResponse;
import com.institucion.reservas.dto.reserva.RecursoResumenResponse;
import com.institucion.reservas.entity.Recurso;
import com.institucion.reservas.entity.enums.EstadoRecurso;

public final class RecursoMapper {

    private RecursoMapper() {
    }

    public static Recurso toEntity(RecursoRequest request) {
        Recurso recurso = new Recurso();
        recurso.setNombre(request.nombre());
        recurso.setDescripcion(request.descripcion());
        recurso.setTipo(request.tipo());
        recurso.setCapacidad(request.capacidad());
        recurso.setEstado(EstadoRecurso.DISPONIBLE);
        return recurso;
    }

    public static void updateEntity(Recurso recurso, RecursoRequest request) {
        recurso.setNombre(request.nombre());
        recurso.setDescripcion(request.descripcion());
        recurso.setTipo(request.tipo());
        recurso.setCapacidad(request.capacidad());
    }

    public static RecursoResponse toResponse(Recurso recurso) {
        return new RecursoResponse(
                recurso.getId(),
                recurso.getNombre(),
                recurso.getDescripcion(),
                recurso.getTipo(),
                recurso.getCapacidad(),
                recurso.getEstado()
        );
    }

    public static RecursoResumenResponse toResumen(Recurso recurso) {
        return new RecursoResumenResponse(
                recurso.getId(),
                recurso.getNombre(),
                recurso.getTipo()
        );
    }
}
