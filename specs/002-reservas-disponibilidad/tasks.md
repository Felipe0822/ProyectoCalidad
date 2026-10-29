# Tasks: Reservas y disponibilidad

**Input**: Design documents from `/specs/002-reservas-disponibilidad/`
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/openapi.yaml`

**Tests**: Diferidas por decisión del proyecto. Se incorporan después de estabilizar la arquitectura. La sección final lista las pruebas pendientes.

## Phase 1: Setup

- [x] T001 Agregar `@EnableScheduling` a `ReservasApplication`.
- [x] T002 Verificar que no se requiere `config/SchedulingConfig.java`; la configuración actual vive en `ReservasApplication` y `application.yaml`.
- [x] T003 Actualizar `application.yaml` con `app.scheduling.enabled` y `finalizacion-ms`.
- [x] T004 Verificar que `./gradlew compileJava` pase.

---

## Phase 2: Foundational

- [x] T005 [P] Crear `entity/enums/EstadoReserva.java`.
- [x] T006 Crear `entity/Reserva.java` con relaciones `ManyToOne`, fechas, estado y fechaCreacion.
- [x] T007 Agregar índices de reserva por recurso/estado/fechas y por usuario/estado.
- [x] T008 Crear `repository/ReservaRepository.java`.
- [x] T009 [P] Agregar `findByIdForUpdate` con `PESSIMISTIC_WRITE` a `UsuarioRepository`.
- [x] T010 [P] Agregar `findByIdForUpdate` con `PESSIMISTIC_WRITE` a `RecursoRepository`.
- [x] T011 Implementar `countActivasVigentes` en `ReservaRepository`.
- [x] T012 Implementar `countOverlaps` en `ReservaRepository`.
- [x] T013 Implementar `findConflictos` en `ReservaRepository`.
- [x] T014 Implementar `finalizarVencidas` en `ReservaRepository`.
- [x] T015 Implementar consultas por usuario, recurso y fecha.
- [x] T016 [P] Crear `dto/reserva/ReservaRequest.java`.
- [x] T017 [P] Crear `dto/reserva/ReservaResponse.java`.
- [x] T018 [P] Crear `dto/reserva/UsuarioResumenResponse.java`.
- [x] T019 [P] Crear `dto/reserva/RecursoResumenResponse.java`.
- [x] T020 [P] Crear `dto/reserva/ConflictoReservaResponse.java`.
- [x] T021 [P] Crear `dto/reserva/DisponibilidadResponse.java`.
- [x] T022 Crear `mapper/ReservaMapper.java` con estado efectivo.
- [x] T023 Agregar códigos de error de reservas al catálogo.

**Checkpoint**: modelo, repositorio, DTOs y consultas listos.

---

## Phase 3: User Story 1 — Crear reserva (P1)

**Goal**: crear una reserva aplicando las 8 reglas y bloqueos.
**Independent Test**: POST /reservas con usuario activo y recurso disponible.

- [x] T024 [US1] Implementar `ReservaService.crear` con `@Transactional`.
- [x] T025 [US1] Validar fechas: `fechaFin > fechaInicio` y `fechaInicio >= now`.
- [x] T026 [US1] Bloquear usuario y recurso en ese orden.
- [x] T027 [US1] Validar usuario `ACTIVO`.
- [x] T028 [US1] Validar recurso `DISPONIBLE`.
- [x] T029 [US1] Validar máximo 3 reservas activas vigentes.
- [x] T030 [US1] Validar solapamiento con reservas activas.
- [x] T031 [US1] Guardar reserva con estado `ACTIVA` y `fechaCreacion`.
- [x] T032 [US1] Implementar POST `/reservas` en `ReservaController`.
- [x] T033 [US1] Devolver 201 con `Location`.

**Checkpoint**: creación de reservas operativa.

---

## Phase 4: User Story 2 — Cancelar reserva (P2)

**Goal**: cancelar reservas activas y respetar la regla de finalizadas.
**Independent Test**: DELETE /reservas/{id}.

- [x] T034 [US2] Implementar `ReservaService.cancelar` con bloqueo de la reserva.
- [x] T035 [US2] Si ya está `CANCELADA`, responder 204 idempotente.
- [x] T036 [US2] Si está `FINALIZADA` o `fechaFin <= now`, responder 409.
- [x] T037 [US2] Cambiar estado a `CANCELADA`.
- [x] T038 [US2] Implementar DELETE `/reservas/{id}` en `ReservaController`.

**Checkpoint**: cancelación operativa.

---

## Phase 5: User Story 3 — Consultar reservas (P3)

**Goal**: consultar por ID, usuario, recurso y fecha.
**Independent Test**: GET de cada endpoint.

- [x] T039 [US3] Implementar `obtener` por ID.
- [x] T040 [US3] Implementar `listarPorUsuario` con validación de usuario existente.
- [x] T041 [US3] Implementar `listarPorRecurso` con validación de recurso existente.
- [x] T042 [US3] Implementar `listarPorFecha` con intersección del día.
- [x] T043 [US3] Implementar GET `/reservas/{id}`, `/reservas/usuario/{id}`, `/reservas/recurso/{id}` y `/reservas?fecha=`.
- [x] T044 [US3] Calcular estado efectivo en las respuestas.

**Checkpoint**: consultas operativas.

---

## Phase 6: User Story 4 — Consultar disponibilidad (P4)

**Goal**: consultar disponibilidad por franja.
**Independent Test**: GET /recursos/{id}/disponibilidad.

- [x] T045 [US4] Implementar `DisponibilidadService.consultar`.
- [x] T046 [US4] Validar fechas.
- [x] T047 [US4] Validar recurso existente.
- [x] T048 [US4] Devolver `disponible=false` si el recurso no está `DISPONIBLE`.
- [x] T049 [US4] Devolver conflictos si hay solapamiento.
- [x] T050 [US4] Implementar GET `/recursos/{id}/disponibilidad` en `RecursoController`.

**Checkpoint**: disponibilidad operativa.

---

## Phase 7: Polish

- [x] T051 Crear `scheduler/ReservaScheduler.java` con `@Scheduled`.
- [x] T052 Invocar `finalizarVencidas` desde el scheduler.
- [x] T053 Verificar que la cancelación rechace reservas vencidas aunque el job no haya corrido.
- [x] T054 Revisar que las 8 reglas estén implementadas.
- [x] T055 Revisar que ningún controller acceda a repositorios.
- [x] T056 Revisar que las entidades no se expongan.
- [x] T057 Probar los endpoints de `quickstart.md`.
- [x] T058 Actualizar `README.md`.
- [x] T059 Compilar y ejecutar.

---

## Phase 8: Pruebas diferidas (posteriores a la estabilización)

> No implementar todavía. Se agregan cuando la arquitectura esté cerrada.
> Gate de entrega final: no se puede cerrar el proyecto hasta completar todas estas tareas, la prueba JMeter de 500 solicitudes y la cobertura.

- [ ] T060 Unitarias de `ReservaService` para las 8 reglas.
- [ ] T061 Integración de `ReservaController` con MockMvc.
- [ ] T062 Pruebas de repositorio para solapamiento y conteo.
- [ ] T063 Prueba automatizada de concurrencia con 20–50 hilos. La carga de 500 solicitudes se valida con JMeter antes de la entrega final.
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
