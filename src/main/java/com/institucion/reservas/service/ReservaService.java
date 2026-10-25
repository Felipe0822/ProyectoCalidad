package com.institucion.reservas.service;

import com.institucion.reservas.dto.reserva.ReservaRequest;
import com.institucion.reservas.dto.reserva.ReservaResponse;
import com.institucion.reservas.entity.Recurso;
import com.institucion.reservas.entity.Reserva;
import com.institucion.reservas.entity.Usuario;
import com.institucion.reservas.entity.enums.EstadoRecurso;
import com.institucion.reservas.entity.enums.EstadoReserva;
import com.institucion.reservas.entity.enums.EstadoUsuario;
import com.institucion.reservas.exception.BadRequestException;
import com.institucion.reservas.exception.ConflictException;
import com.institucion.reservas.exception.NotFoundException;
import com.institucion.reservas.mapper.ReservaMapper;
import com.institucion.reservas.repository.RecursoRepository;
import com.institucion.reservas.repository.ReservaRepository;
import com.institucion.reservas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final int MAX_RESERVAS_ACTIVAS = 3;

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;
    private final Clock clock;

    @Transactional
    public ReservaResponse crear(ReservaRequest request) {
        LocalDateTime now = LocalDateTime.now(clock);
        validarFechas(request.fechaInicio(), request.fechaFin(), now);

        // Orden de bloqueo obligatorio: usuario y luego recurso, para evitar deadlocks.
        Usuario usuario = usuarioRepository.findByIdForUpdate(request.usuarioId())
                .orElseThrow(() -> new NotFoundException(
                        "USUARIO_NO_ENCONTRADO",
                        "No existe un usuario con id " + request.usuarioId()
                ));

        Recurso recurso = recursoRepository.findByIdForUpdate(request.recursoId())
                .orElseThrow(() -> new NotFoundException(
                        "RECURSO_NO_ENCONTRADO",
                        "No existe un recurso con id " + request.recursoId()
                ));

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new ConflictException(
                    "USUARIO_INACTIVO",
                    "El usuario no está activo para realizar reservas"
            );
        }

        if (recurso.getEstado() != EstadoRecurso.DISPONIBLE) {
            throw new ConflictException(
                    "RECURSO_NO_DISPONIBLE",
                    "El recurso no está disponible para reservas"
            );
        }

        long reservasActivas = reservaRepository.countActivasVigentes(
                usuario.getId(),
                EstadoReserva.ACTIVA,
                now
        );
        if (reservasActivas >= MAX_RESERVAS_ACTIVAS) {
            throw new ConflictException(
                    "LIMITE_RESERVAS_ACTIVAS",
                    "El usuario ya tiene el máximo de 3 reservas activas"
            );
        }

        long solapamientos = reservaRepository.countOverlaps(
                recurso.getId(),
                EstadoReserva.ACTIVA,
                request.fechaInicio(),
                request.fechaFin()
        );
        if (solapamientos > 0) {
            throw new ConflictException(
                    "RESERVA_SUPERPUESTA",
                    "El recurso ya se encuentra reservado para este horario"
            );
        }

        Reserva reserva = ReservaMapper.toEntity(request, usuario, recurso, now);
        reserva = reservaRepository.saveAndFlush(reserva);
        return ReservaMapper.toResponse(reserva, now);
    }

    @Transactional
    public void cancelar(Long id) {
        LocalDateTime now = LocalDateTime.now(clock);
        Reserva reserva = reservaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException(
                        "RESERVA_NO_ENCONTRADA",
                        "No existe una reserva con id " + id
                ));

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            return;
        }

        if (reserva.getEstado() == EstadoReserva.FINALIZADA || !reserva.getFechaFin().isAfter(now)) {
            throw new ConflictException(
                    "RESERVA_FINALIZADA",
                    "La reserva ya está finalizada y no puede cancelarse"
            );
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtener(Long id) {
        LocalDateTime now = LocalDateTime.now(clock);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "RESERVA_NO_ENCONTRADA",
                        "No existe una reserva con id " + id
                ));
        return ReservaMapper.toResponse(reserva, now);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new NotFoundException(
                    "USUARIO_NO_ENCONTRADO",
                    "No existe un usuario con id " + usuarioId
            );
        }
        LocalDateTime now = LocalDateTime.now(clock);
        return reservaRepository.findByUsuarioIdOrderByFechaInicioDesc(usuarioId)
                .stream()
                .map(reserva -> ReservaMapper.toResponse(reserva, now))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorRecurso(Long recursoId) {
        if (!recursoRepository.existsById(recursoId)) {
            throw new NotFoundException(
                    "RECURSO_NO_ENCONTRADO",
                    "No existe un recurso con id " + recursoId
            );
        }
        LocalDateTime now = LocalDateTime.now(clock);
        return reservaRepository.findByRecursoIdOrderByFechaInicioDesc(recursoId)
                .stream()
                .map(reserva -> ReservaMapper.toResponse(reserva, now))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorFecha(LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = inicioDelDia.plusDays(1);
        LocalDateTime now = LocalDateTime.now(clock);
        return reservaRepository.findByFecha(inicioDelDia, finDelDia)
                .stream()
                .map(reserva -> ReservaMapper.toResponse(reserva, now))
                .toList();
    }

    @Transactional
    public int finalizarVencidas() {
        return reservaRepository.finalizarVencidas(
                EstadoReserva.ACTIVA,
                EstadoReserva.FINALIZADA,
                LocalDateTime.now(clock)
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
