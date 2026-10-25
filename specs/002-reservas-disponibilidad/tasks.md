# Tasks: Reservas y disponibilidad

**Input**: Design documents from `/specs/002-reservas-disponibilidad/`
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/openapi.yaml`

**Tests**: Diferidas por decisión del proyecto. Se incorporan después de estabilizar la arquitectura. La sección final lista las pruebas pendientes.

## Phase 1: Setup

- [ ] T001 Agregar `@EnableScheduling` a `ReservasApplication`.
- [ ] T002 Crear `config/SchedulingConfig.java` si se requiere configuración adicional.
- [ ] T003 Actualizar `application.yaml` con `app.scheduling.enabled` y `finalizacion-ms`.
- [ ] T004 Verificar que `./gradlew compileJava` pase.

---

## Phase 2: Foundational

- [ ] T005 [P] Crear `entity/enums/EstadoReserva.java`.
- [ ] T006 Crear `entity/Reserva.java` con relaciones `ManyToOne`, fechas, estado y fechaCreacion.
- [ ] T007 Agregar índices de reserva por recurso/estado/fechas y por usuario/estado.
- [ ] T008 Crear `repository/ReservaRepository.java`.
- [ ] T009 [P] Agregar `findByIdForUpdate` con `PESSIMISTIC_WRITE` a `UsuarioRepository`.
- [ ] T010 [P] Agregar `findByIdForUpdate` con `PESSIMISTIC_WRITE` a `RecursoRepository`.
- [ ] T011 Implementar `countActivasVigentes` en `ReservaRepository`.
- [ ] T012 Implementar `existsOverlap` en `ReservaRepository`.
- [ ] T013 Implementar `findConflictos` en `ReservaRepository`.
- [ ] T014 Implementar `finalizarVencidas` en `ReservaRepository`.
- [ ] T015 Implementar consultas por usuario, recurso y fecha.
- [ ] T016 [P] Crear `dto/reserva/ReservaRequest.java`.
- [ ] T017 [P] Crear `dto/reserva/ReservaResponse.java`.
- [ ] T018 [P] Crear `dto/reserva/UsuarioResumenResponse.java`.
- [ ] T019 [P] Crear `dto/reserva/RecursoResumenResponse.java`.
- [ ] T020 [P] Crear `dto/reserva/ConflictoReservaResponse.java`.
- [ ] T021 [P] Crear `dto/reserva/DisponibilidadResponse.java`.
- [ ] T022 Crear `mapper/ReservaMapper.java` con estado efectivo.
- [ ] T023 Agregar códigos de error de reservas al catálogo.

**Checkpoint**: modelo, repositorio, DTOs y consultas listos.

---

## Phase 3: User Story 1 — Crear reserva (P1)

**Goal**: crear una reserva aplicando las 8 reglas y bloqueos.
**Independent Test**: POST /reservas con usuario activo y recurso disponible.

- [ ] T024 [US1] Implementar `ReservaService.crear` con `@Transactional`.
- [ ] T025 [US1] Validar fechas: `fechaFin > fechaInicio` y `fechaInicio >= now`.
- [ ] T026 [US1] Bloquear usuario y recurso en ese orden.
- [ ] T027 [US1] Validar usuario `ACTIVO`.
- [ ] T028 [US1] Validar recurso `DISPONIBLE`.
- [ ] T029 [US1] Validar máximo 3 reservas activas vigentes.
- [ ] T030 [US1] Validar solapamiento con reservas activas.
- [ ] T031 [US1] Guardar reserva con estado `ACTIVA` y `fechaCreacion`.
- [ ] T032 [US1] Implementar POST `/reservas` en `ReservaController`.
- [ ] T033 [US1] Devolver 201 con `Location`.

**Checkpoint**: creación de reservas operativa.

---

## Phase 4: User Story 2 — Cancelar reserva (P2)

**Goal**: cancelar reservas activas y respetar la regla de finalizadas.
**Independent Test**: DELETE /reservas/{id}.

- [ ] T034 [US2] Implementar `ReservaService.cancelar` con bloqueo de la reserva.
- [ ] T035 [US2] Si ya está `CANCELADA`, responder 204 idempotente.
- [ ] T036 [US2] Si está `FINALIZADA` o `fechaFin <= now`, responder 409.
- [ ] T037 [US2] Cambiar estado a `CANCELADA`.
- [ ] T038 [US2] Implementar DELETE `/reservas/{id}` en `ReservaController`.

**Checkpoint**: cancelación operativa.

---

## Phase 5: User Story 3 — Consultar reservas (P3)

**Goal**: consultar por ID, usuario, recurso y fecha.
**Independent Test**: GET de cada endpoint.

- [ ] T039 [US3] Implementar `obtener` por ID.
- [ ] T040 [US3] Implementar `listarPorUsuario` con validación de usuario existente.
- [ ] T041 [US3] Implementar `listarPorRecurso` con validación de recurso existente.
- [ ] T042 [US3] Implementar `listarPorFecha` con intersección del día.
- [ ] T043 [US3] Implementar GET `/reservas/{id}`, `/reservas/usuario/{id}`, `/reservas/recurso/{id}` y `/reservas?fecha=`.
- [ ] T044 [US3] Calcular estado efectivo en las respuestas.

**Checkpoint**: consultas operativas.

---

## Phase 6: User Story 4 — Consultar disponibilidad (P4)

**Goal**: consultar disponibilidad por franja.
**Independent Test**: GET /recursos/{id}/disponibilidad.

- [ ] T045 [US4] Implementar `DisponibilidadService.consultar`.
- [ ] T046 [US4] Validar fechas.
- [ ] T047 [US4] Validar recurso existente.
- [ ] T048 [US4] Devolver `disponible=false` si el recurso no está `DISPONIBLE`.
- [ ] T049 [US4] Devolver conflictos si hay solapamiento.
- [ ] T050 [US4] Implementar GET `/recursos/{id}/disponibilidad` en `RecursoController`.

**Checkpoint**: disponibilidad operativa.

---

## Phase 7: Polish

- [ ] T051 Crear `scheduler/ReservaScheduler.java` con `@Scheduled`.
- [ ] T052 Invocar `finalizarVencidas` desde el scheduler.
- [ ] T053 Verificar que la cancelación rechace reservas vencidas aunque el job no haya corrido.
- [ ] T054 Revisar que las 8 reglas estén implementadas.
- [ ] T055 Revisar que ningún controller acceda a repositorios.
- [ ] T056 Revisar que las entidades no se expongan.
- [ ] T057 Probar los endpoints de `quickstart.md`.
- [ ] T058 Actualizar `README.md`.
- [ ] T059 Compilar y ejecutar.

---

## Phase 8: Pruebas diferidas (posteriores a la estabilización)

> No implementar todavía. Se agregan cuando la arquitectura esté cerrada.

- [ ] T060 Unitarias de `ReservaService` para las 8 reglas.
- [ ] T061 Integración de `ReservaController` con MockMvc.
- [ ] T062 Pruebas de repositorio para solapamiento y conteo.
- [ ] T063 Prueba de concurrencia con 20–50 hilos.
- [ ] T064 Prueba de contrato para disponibilidad y errores.
- [ ] T065 Prueba de finalización automática con `Clock` fijo.

## Dependencies & Execution Order

- Phase 1 depende de la feature 001.
- Phase 2 depende de Phase 1.
- Phase 3 depende de Phase 2.
- Phase 4 depende de Phase 3.
- Phase 5 y Phase 6 dependen de Phase 2; pueden hacerse después de Phase 3.
- Phase 7 depende de Phases 3–6.
- Phase 8 se ejecuta después de la estabilización.

## MVP

US1 (crear reserva) es el MVP del núcleo. US2–US4 completan la funcionalidad.
