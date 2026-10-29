# Implementation Plan: Reservas y disponibilidad

**Branch**: `002-reservas-disponibilidad` | **Date**: 2026-10-25 | **Status**: Approved for architecture iteration | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-reservas-disponibilidad/spec.md`

## Summary

Implementar el núcleo del sistema: creación, cancelación y consulta de reservas, consulta de disponibilidad, finalización automática y control de concurrencia. Se aplican las 8 reglas de negocio y se garantiza que no existan reservas superpuestas ni más de 3 reservas activas por usuario.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Bean Validation, Lombok, H2

**Storage**: H2 en memoria (`jdbc:h2:mem:reservas`), con bloqueos pesimistas

**Testing**: JUnit 5, Mockito, MockMvc, AssertJ, pruebas de concurrencia con `ExecutorService`. Se incorporan después de estabilizar la arquitectura.

**Target Platform**: Servicio REST ejecutado con `./gradlew bootRun`

**Project Type**: Web service / API REST

**Performance Goals**: Sostener 500 solicitudes concurrentes sobre un conjunto limitado de recursos sin crear duplicados; las solicitudes perdedoras deben recibir 409, no 500.

**Constraints**: H2 con `LOCK_TIMEOUT=10000`; bloqueo pesimista usuario → recurso; intervalos semiabiertos; sin autenticación.

**Scale/Scope**: Una institución, un conjunto limitado de recursos, hasta 3 reservas activas por usuario.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Especificación primero: `spec.md` aprobado para la iteración de arquitectura (2026-10-29).
- [x] Arquitectura en capas: Controller → Service → Repository.
- [x] DTOs y mappers; entidades JPA no expuestas.
- [x] Manejo global de errores con `@RestControllerAdvice`.
- [x] Integridad y concurrencia: transacciones y bloqueos pesimistas.
- [x] Pruebas diferidas a la fase de estabilización, según la decisión del proyecto.
- [x] Sin autenticación en v1, documentado como supuesto.
- [x] Sin complejidad innecesaria: un solo proyecto, H2, sin microservicios.

## Project Structure

### Documentation (this feature)

```text
specs/002-reservas-disponibilidad/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
├── checklists/
│   └── requirements.md
├── traceability.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/com/institucion/reservas/
├── ReservasApplication.java
├── config/
│   └── ClockConfig.java
├── controller/
│   ├── UsuarioController.java
│   ├── RecursoController.java
│   └── ReservaController.java
├── service/
│   ├── UsuarioService.java
│   ├── RecursoService.java
│   ├── ReservaService.java
│   └── DisponibilidadService.java
├── repository/
│   ├── UsuarioRepository.java
│   ├── RecursoRepository.java
│   └── ReservaRepository.java
├── entity/
│   ├── Usuario.java
│   ├── Recurso.java
│   ├── Reserva.java
│   └── enums/
│       ├── EstadoUsuario.java
│       ├── EstadoRecurso.java
│       └── EstadoReserva.java
├── dto/
│   ├── reserva/
│   │   ├── ReservaRequest.java
│   │   ├── ReservaResponse.java
│   │   ├── UsuarioResumenResponse.java
│   │   ├── RecursoResumenResponse.java
│   │   ├── ConflictoReservaResponse.java
│   │   └── DisponibilidadResponse.java
│   └── error/
│       └── ErrorResponse.java
├── exception/
│   ├── ApiException.java
│   ├── NotFoundException.java
│   ├── ConflictException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java
├── mapper/
│   ├── UsuarioMapper.java
│   ├── RecursoMapper.java
│   └── ReservaMapper.java
└── scheduler/
    └── ReservaScheduler.java
```

**Structure Decision**: Se amplía la estructura de la feature 001 con `Reserva`, `ReservaService`, `DisponibilidadService`, `ReservaRepository` y `ReservaScheduler`.

## Data Model

### Reserva

| Campo | Tipo | Restricciones |
|---|---|---|
| id | Long | PK, autoincremental |
| usuario | `@ManyToOne` Usuario | obligatorio, LAZY |
| recurso | `@ManyToOne` Recurso | obligatorio, LAZY |
| fechaInicio | LocalDateTime | obligatorio |
| fechaFin | LocalDateTime | obligatorio, > fechaInicio |
| estado | EstadoReserva | `ACTIVA`, `CANCELADA`, `FINALIZADA` |
| fechaCreacion | LocalDateTime | obligatorio, no modificable |

### Índices

- `reservas (recurso_id, estado, fecha_inicio, fecha_fin)`.
- `reservas (usuario_id, estado)`.

## API Contracts

| Método | Ruta | Descripción | Éxito |
|---|---|---|---|
| POST | `/reservas` | Crear reserva | 201 |
| GET | `/reservas/{id}` | Consultar reserva | 200 |
| DELETE | `/reservas/{id}` | Cancelar reserva | 204 |
| GET | `/reservas/usuario/{usuarioId}` | Reservas por usuario | 200 |
| GET | `/reservas/recurso/{recursoId}` | Reservas por recurso | 200 |
| GET | `/reservas?fecha=YYYY-MM-DD` | Reservas que intersectan el día | 200 |
| GET | `/recursos/{id}/disponibilidad` | Disponibilidad por franja | 200 |

## Concurrency Strategy

### Crear reserva

Dentro de `@Transactional`:

1. Validar fechas con `LocalDateTime.now(clock)`.
2. Bloquear usuario con `PESSIMISTIC_WRITE`.
3. Bloquear recurso con `PESSIMISTIC_WRITE`.
4. Validar estados.
5. Contar reservas activas vigentes del usuario.
6. Verificar solapamiento del recurso.
7. Guardar la reserva.

El orden **usuario → recurso** es obligatorio para evitar deadlocks.

### Consultas

- `countActivasVigentes(usuarioId, ACTIVA, now)`: `estado = ACTIVA AND fechaFin > now`.
- `countOverlaps(recursoId, ACTIVA, inicio, fin)`: `estado = ACTIVA AND fechaInicio < fin AND fechaFin > inicio`.
- `findConflictos(...)`: misma condición, para disponibilidad.

### H2

- `LOCK_TIMEOUT=10000`.
- `jakarta.persistence.lock.timeout=10000`.
- Los bloqueos serializan por recurso; la prioridad es la consistencia.

## Finalization

- `ReservaRepository.finalizarVencidas(ACTIVA, FINALIZADA, now)` con `@Modifying`.
- `ReservaScheduler` con `@Scheduled(fixedRate)` cada 30 segundos, para garantizar el máximo de 1 minuto del SC-006 incluso con el tiempo de ejecución del job.
- Las respuestas calculan el estado efectivo: si `ACTIVA` y `fechaFin <= now`, se muestra `FINALIZADA`.
- La cancelación rechaza si `fechaFin <= now`, aunque el job no haya corrido.

## Implementation Phases

### Phase 1 — Configuración

- Agregar `@EnableScheduling` en `ReservasApplication`.
- Configurar `application.yaml` (`app.scheduling.enabled` y `app.scheduling.finalizacion-ms`).
- Agregar índices y `@Version` si se decide usar bloqueo optimista adicional. No se requiere una clase `SchedulingConfig` para la configuración actual.

### Phase 2 — Dominio y persistencia

- Crear `EstadoReserva`, `Reserva`, `ReservaRepository`.
- Crear DTOs de reserva y disponibilidad.
- Crear `ReservaMapper`.
- Implementar consultas de solapamiento, conteo y finalización.

### Phase 3 — Crear reserva

- Implementar `ReservaService.crear` con bloqueos y las 8 reglas.
- Implementar `ReservaController` POST.
- Validar errores y códigos.

### Phase 4 — Cancelar y consultar

- Implementar cancelación lógica.
- Implementar consultas por ID, usuario, recurso y fecha.
- Implementar disponibilidad.
- Implementar `ReservaScheduler`.

### Phase 5 — Cierre

- Revisar que las 8 reglas estén implementadas.
- Probar manualmente con curl/Postman.
- Documentar la estrategia de concurrencia.
- Compilar y ejecutar.

## Testing Note

Las pruebas se difieren por decisión del proyecto. Antes de la entrega final se deben agregar:

- Unitarias de `ReservaService` para las 8 reglas.
- Integración de `ReservaController` con MockMvc.
- Pruebas de repositorio para solapamiento y conteo.
- Prueba automatizada de concurrencia con 20–50 hilos para la iteración de arquitectura.
- Prueba de carga con 500 solicitudes mediante JMeter antes de la entrega final (SC-002).
- Prueba de contrato para disponibilidad y errores.

## Complexity Tracking

No hay violaciones a la constitución. La complejidad de los bloqueos pesimistas está justificada por el requisito de 500 usuarios concurrentes sin duplicados.

## Traceability

La matriz de trazabilidad requisito → historia → tarea → prueba → código está en [traceability.md](./traceability.md).
