# Traceability: Gestión de usuarios y recursos

**Reviewed**: 2026-10-29  
**Scope**: Iteración de arquitectura. Las pruebas automatizadas están registradas como deuda (T044–T049) y deben completarse antes de la entrega final.

| Requisito | Historia | Tareas | Pruebas | Código |
|---|---|---|---|---|
| FR-001 | US1 | T024, T025 | T044, T046 | `UsuarioService.crear`, `UsuarioController.crear` |
| FR-002 | US1 | T024 | T044 | `UsuarioMapper.toEntity` |
| FR-003 | US1 | T024, T025 | T044, T046 | `UsuarioService.listar`, `obtener` |
| FR-004 | US2 | T028, T029 | T044, T046 | `UsuarioService.cambiarEstado` |
| FR-005 | US1 | T026 | T044, T048 | `UsuarioService.crear`, restricciones únicas de `Usuario` |
| FR-006 | US1 | T010, T025 | T046, T049 | `UsuarioRequest` |
| FR-007 | US3 | T031, T032 | T045, T047 | `RecursoService.crear` |
| FR-008 | US3 | T031, T034 | T045, T047 | `RecursoService.actualizar`, `RecursoMapper.updateEntity` |
| FR-009 | US3 | T031, T032 | T045, T047 | `RecursoService.listar`, `obtener` |
| FR-010 | US4 | T035, T036 | T045, T047 | `RecursoService.cambiarEstado` |
| FR-011 | US3 | T033 | T045, T048 | `RecursoRequest`, `@Check` de `Recurso` |
| FR-012 | Cross-cutting | T021 | T049 | `ErrorResponse`, `GlobalExceptionHandler` |
| FR-013 | Cross-cutting | T021 | T049 | `GlobalExceptionHandler` |
| FR-014 | Cross-cutting | T010–T016, T022, T023 | T046, T047 | DTOs, mappers y controllers |

## Pendientes de cierre

- Implementar T044–T049.
- Ejecutar `./gradlew test`.
- Verificar que el 100% de los FR tenga al menos una prueba automatizada (SC-003).
- Validar el formato uniforme de errores con pruebas de contrato (SC-005).
