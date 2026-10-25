package com.institucion.reservas.mapper;

import com.institucion.reservas.dto.reserva.ConflictoReservaResponse;
import com.institucion.reservas.dto.reserva.RecursoResumenResponse;
import com.institucion.reservas.dto.reserva.ReservaRequest;
import com.institucion.reservas.dto.reserva.ReservaResponse;
import com.institucion.reservas.dto.reserva.UsuarioResumenResponse;
import com.institucion.reservas.entity.Recurso;
import com.institucion.reservas.entity.Reserva;
import com.institucion.reservas.entity.Usuario;
import com.institucion.reservas.entity.enums.EstadoReserva;

import java.time.LocalDateTime;

public final class ReservaMapper {

    private ReservaMapper() {
    }

    public static Reserva toEntity(
            ReservaRequest request,
            Usuario usuario,
            Recurso recurso,
            LocalDateTime fechaCreacion
    ) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setRecurso(recurso);
        reserva.setFechaInicio(request.fechaInicio());
        reserva.setFechaFin(request.fechaFin());
        reserva.setEstado(EstadoReserva.ACTIVA);
        reserva.setFechaCreacion(fechaCreacion);
        return reserva;
    }

    public static ReservaResponse toResponse(Reserva reserva, LocalDateTime now) {
        Usuario usuario = reserva.getUsuario();
        Recurso recurso = reserva.getRecurso();

        return new ReservaResponse(
                reserva.getId(),
                new UsuarioResumenResponse(usuario.getId(), usuario.getNombre(), usuario.getDocumento()),
                new RecursoResumenResponse(recurso.getId(), recurso.getNombre(), recurso.getTipo()),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                estadoEfectivo(reserva, now),
                reserva.getFechaCreacion()
        );
    }

    public static ConflictoReservaResponse toConflicto(Reserva reserva) {
        return new ConflictoReservaResponse(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin()
        );
    }

    public static EstadoReserva estadoEfectivo(Reserva reserva, LocalDateTime now) {
        if (reserva.getEstado() == EstadoReserva.ACTIVA && !reserva.getFechaFin().isAfter(now)) {
            return EstadoReserva.FINALIZADA;
        }
        return reserva.getEstado();
    }
}
