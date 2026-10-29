# Tasks: Gestión de usuarios y recursos

**Input**: Design documents from `/specs/001-gestion-usuarios-recursos/`
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/openapi.yaml`

**Tests**: Diferidas por decisión del proyecto. Se incorporan después de estabilizar la arquitectura. La sección final lista las pruebas pendientes.

## Phase 1: Setup

- [x] T001 Configurar `src/main/resources/application.yaml` con H2, JPA, consola H2 y `app.timezone`.
- [x] T002 Crear `src/main/java/com/institucion/reservas/config/ClockConfig.java` con un bean `Clock` basado en `app.timezone`.
- [x] T003 Verificar que `./gradlew compileJava` pase con JDK 21.

---

## Phase 2: Foundational

- [x] T004 [P] Crear `entity/enums/EstadoUsuario.java`.
- [x] T005 [P] Crear `entity/enums/EstadoRecurso.java`.
- [x] T006 Crear `entity/Usuario.java` con id, nombre, documento, correo y estado.
- [x] T007 Crear `entity/Recurso.java` con id, nombre, descripción, tipo, capacidad y estado.
- [x] T008 [P] Crear `repository/UsuarioRepository.java`.
- [x] T009 [P] Crear `repository/RecursoRepository.java`.
- [x] T010 [P] Crear `dto/usuario/UsuarioRequest.java`.
- [x] T011 [P] Crear `dto/usuario/UsuarioResponse.java`.
- [x] T012 [P] Crear `dto/usuario/CambioEstadoUsuarioRequest.java`.
- [x] T013 [P] Crear `dto/recurso/RecursoRequest.java`.
- [x] T014 [P] Crear `dto/recurso/RecursoResponse.java`.
- [x] T015 [P] Crear `dto/recurso/CambioEstadoRecursoRequest.java`.
- [x] T016 [P] Crear `dto/error/ErrorResponse.java`.
- [x] T017 [P] Crear `exception/ApiException.java`.
- [x] T018 [P] Crear `exception/NotFoundException.java`.
- [x] T019 [P] Crear `exception/ConflictException.java`.
- [x] T020 [P] Crear `exception/BadRequestException.java`.
- [x] T021 Crear `exception/GlobalExceptionHandler.java` con `@RestControllerAdvice`.
- [x] T022 [P] Crear `mapper/UsuarioMapper.java`.
- [x] T023 [P] Crear `mapper/RecursoMapper.java`.

**Checkpoint**: entidades, repositorios, DTOs, mappers y manejo de errores listos.

---

## Phase 3: User Story 1 — Registrar y consultar usuarios (P1)

**Goal**: crear usuarios, consultarlos y listarlos.
**Independent Test**: POST /usuarios, GET /usuarios/{id}, GET /usuarios.

- [x] T024 [US1] Implementar `service/UsuarioService.java` con `crear`, `listar` y `obtener`.
- [x] T025 [US1] Implementar `controller/UsuarioController.java` con POST, GET por ID y GET lista.
- [x] T026 [US1] Validar documento/correo duplicados y mapear a 409 `DATOS_DUPLICADOS`.
- [x] T027 [US1] Agregar `Location` en la respuesta 201.

**Checkpoint**: usuarios operativos por API.

---

## Phase 4: User Story 2 — Activar e inactivar usuarios (P2)

**Goal**: cambiar el estado de un usuario.
**Independent Test**: PATCH /usuarios/{id}/estado y GET posterior.

- [x] T028 [US2] Agregar `cambiarEstado` a `UsuarioService`.
- [x] T029 [US2] Agregar PATCH `/usuarios/{id}/estado` a `UsuarioController`.
- [x] T030 [US2] Validar usuario inexistente con 404.

**Checkpoint**: estado de usuario operativo.

---

## Phase 5: User Story 3 — Registrar, modificar y consultar recursos (P3)

**Goal**: CRUD de recursos.
**Independent Test**: POST, GET, PUT y GET lista de recursos.

- [x] T031 [US3] Implementar `service/RecursoService.java` con `crear`, `listar`, `obtener` y `actualizar`.
- [x] T032 [US3] Implementar `controller/RecursoController.java` con POST, GET por ID, GET lista y PUT.
- [x] T033 [US3] Validar capacidad mayor que cero y campos obligatorios.
- [x] T034 [US3] Asegurar que `actualizar` no modifique el estado.

**Checkpoint**: recursos operativos por API.

---

## Phase 6: User Story 4 — Cambiar estado de recurso (P4)

**Goal**: cambiar el estado de un recurso.
**Independent Test**: PATCH /recursos/{id}/estado y GET posterior.

- [x] T035 [US4] Agregar `cambiarEstado` a `RecursoService`.
- [x] T036 [US4] Agregar PATCH `/recursos/{id}/estado` a `RecursoController`.
- [x] T037 [US4] Validar recurso inexistente con 404.

**Checkpoint**: estado de recurso operativo.

---

## Phase 7: Polish

- [x] T038 Revisar que ningún controller importe repositorios.
- [x] T039 Revisar que ninguna entidad JPA se exponga en respuestas.
- [x] T040 Verificar el formato uniforme de errores en 400, 404 y 409.
- [x] T041 Ejecutar la aplicación y probar los endpoints de `quickstart.md`.
- [x] T042 Actualizar `README.md` con la descripción de la feature.
- [x] T043 Compilar con `./gradlew compileJava` y ejecutar con `./gradlew bootRun`.

---

## Phase 8: Pruebas diferidas (posteriores a la estabilización)

> No implementar todavía. Se agregan cuando la arquitectura esté cerrada.
> Gate de entrega final: no se puede cerrar el proyecto hasta completar todas estas tareas y ejecutar la cobertura.

- [ ] T044 Unitarias de `UsuarioService` con Mockito.
- [ ] T045 Unitarias de `RecursoService` con Mockito.
- [ ] T046 Integración de `UsuarioController` con MockMvc.
- [ ] T047 Integración de `RecursoController` con MockMvc.
- [ ] T048 Pruebas de repositorio para unicidad y estados.
- [ ] T049 Pruebas de contrato para el formato de error.

## Dependencies & Execution Order

- Phase 1 no depende de nada.
- Phase 2 depende de Phase 1.
- Phases 3–6 dependen de Phase 2.
- US1 y US2 comparten `UsuarioService` y `UsuarioController`; hacer US1 antes de US2.
- US3 y US4 comparten `RecursoService` y `RecursoController`; hacer US3 antes de US4.
- Phase 7 depende de Phases 3–6.
- Phase 8 se ejecuta después de la estabilización.

## MVP

US1 + US3 permiten administrar el catálogo mínimo. US2 y US4 completan los estados.
