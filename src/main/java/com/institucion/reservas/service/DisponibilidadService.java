package com.institucion.reservas.service;

import com.institucion.reservas.dto.reserva.ConflictoReservaResponse;
import com.institucion.reservas.dto.reserva.DisponibilidadResponse;
import com.institucion.reservas.entity.Recurso;
import com.institucion.reservas.entity.Reserva;
import com.institucion.reservas.entity.enums.EstadoRecurso;
import com.institucion.reservas.entity.enums.EstadoReserva;
import com.institucion.reservas.exception.BadRequestException;
import com.institucion.reservas.exception.NotFoundException;
import com.institucion.reservas.mapper.ReservaMapper;
import com.institucion.reservas.repository.RecursoRepository;
import com.institucion.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilidadService {

    private final RecursoRepository recursoRepository;
    private final ReservaRepository reservaRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public DisponibilidadResponse consultar(Long recursoId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        LocalDateTime now = LocalDateTime.now(clock);
        validarFechas(fechaInicio, fechaFin, now);

        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new NotFoundException(
                        "RECURSO_NO_ENCONTRADO",
                        "No existe un recurso con id " + recursoId
                ));

        if (recurso.getEstado() != EstadoRecurso.DISPONIBLE) {
            return new DisponibilidadResponse(
                    recursoId,
                    fechaInicio,
                    fechaFin,
                    false,
                    "RECURSO_NO_DISPONIBLE",
                    List.of()
            );
        }

        List<Reserva> conflictos = reservaRepository.findConflictos(
                recursoId,
                EstadoReserva.ACTIVA,
                fechaInicio,
                fechaFin
        );

        if (!conflictos.isEmpty()) {
            List<ConflictoReservaResponse> reservasConflictivas = conflictos.stream()
                    .map(ReservaMapper::toConflicto)
                    .toList();
            return new DisponibilidadResponse(
                    recursoId,
                    fechaInicio,
                    fechaFin,
                    false,
                    "RESERVA_SUPERPUESTA",
                    reservasConflictivas
            );
        }

        return new DisponibilidadResponse(
                recursoId,
                fechaInicio,
                fechaFin,
                true,
                null,
                List.of()
        );
    }

    private void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, LocalDateTime now) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException(
                    "FECHAS_INVALIDAS",
                    "Las fechas de inicio y fin son obligatorias"
            );
        }
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new BadRequestException(
                    "FECHAS_INVALIDAS",
                    "La fecha de fin debe ser posterior a la fecha de inicio"
            );
        }
        if (fechaInicio.isBefore(now)) {
            throw new BadRequestException(
                    "FECHA_INICIO_EN_PASADO",
                    "La fecha de inicio no puede ser anterior al momento actual"
            );
        }
    }
}
