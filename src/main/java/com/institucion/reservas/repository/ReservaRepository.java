package com.institucion.reservas.repository;

import com.institucion.reservas.entity.Reserva;
import com.institucion.reservas.entity.enums.EstadoReserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reserva r where r.id = :id")
    Optional<Reserva> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select count(r)
            from Reserva r
            where r.usuario.id = :usuarioId
              and r.estado = :estado
              and r.fechaFin > :now
            """)
    long countActivasVigentes(
            @Param("usuarioId") Long usuarioId,
            @Param("estado") EstadoReserva estado,
            @Param("now") LocalDateTime now
    );

    @Query("""
            select count(r)
            from Reserva r
            where r.recurso.id = :recursoId
              and r.estado = :estado
              and r.fechaInicio < :fechaFin
              and r.fechaFin > :fechaInicio
            """)
    long countOverlaps(
            @Param("recursoId") Long recursoId,
            @Param("estado") EstadoReserva estado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @EntityGraph(attributePaths = {"usuario", "recurso"})
    @Query("""
            select r
            from Reserva r
            where r.recurso.id = :recursoId
              and r.estado = :estado
              and r.fechaInicio < :fechaFin
              and r.fechaFin > :fechaInicio
            order by r.fechaInicio asc
            """)
    List<Reserva> findConflictos(
            @Param("recursoId") Long recursoId,
            @Param("estado") EstadoReserva estado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @EntityGraph(attributePaths = {"usuario", "recurso"})
    List<Reserva> findByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);

    @EntityGraph(attributePaths = {"usuario", "recurso"})
    List<Reserva> findByRecursoIdOrderByFechaInicioDesc(Long recursoId);

    @EntityGraph(attributePaths = {"usuario", "recurso"})
    @Query("""
            select r
            from Reserva r
            where r.fechaInicio < :finDelDia
              and r.fechaFin > :inicioDelDia
            order by r.fechaInicio asc
            """)
    List<Reserva> findByFecha(
            @Param("inicioDelDia") LocalDateTime inicioDelDia,
            @Param("finDelDia") LocalDateTime finDelDia
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Reserva r
            set r.estado = :finalizada
            where r.estado = :activa
              and r.fechaFin <= :now
            """)
    int finalizarVencidas(
            @Param("activa") EstadoReserva activa,
            @Param("finalizada") EstadoReserva finalizada,
            @Param("now") LocalDateTime now
    );
}
