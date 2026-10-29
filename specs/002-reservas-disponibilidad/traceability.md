# Traceability: Reservas y disponibilidad

**Reviewed**: 2026-10-29  
**Scope**: Iteración de arquitectura. Las pruebas automatizadas están registradas como deuda (T060–T065) y deben completarse antes de la entrega final.

| Requisito | Historia | Tareas | Pruebas | Código |
|---|---|---|---|---|
| FR-001 | US1 | T024, T032, T033 | T060, T061 | `ReservaService.crear`, `ReservaController.crear` |
| FR-002 | US1 | T031 | T060 | `ReservaMapper.toEntity` |
| FR-003 | US1 | T027 | T060, T061 | `ReservaService.crear` |
| FR-004 | US1 | T028 | T060, T061 | `ReservaService.crear` |
| FR-005 | US1 | T025 | T060 | `ReservaService.validarFechas` |
| FR-006 | US1 | T025 | T060 | `ReservaService.validarFechas` |
| FR-007 | US1 | T030 | T060, T062 | `ReservaRepository.countOverlaps` |
| FR-008 | US1 | T029 | T060 | `ReservaRepository.countActivasVigentes` |
| FR-009 | US2 | T034, T037, T038 | T060, T061 | `ReservaService.cancelar` |
| FR-010 | US2 | T036 | T060, T061 | `ReservaService.cancelar` |
| FR-011 | Cross-cutting | T051, T052, T053 | T065 | `ReservaScheduler`, `ReservaRepository.finalizarVencidas`, `ReservaMapper.estadoEfectivo` |
| FR-012 | US3 | T039–T043 | T061, T062 | `ReservaService.listarPorUsuario/Recurso/Fecha` |
| FR-013 | US4 | T045–T050 | T064 | `DisponibilidadService.consultar` |
| FR-014 | US4 | T049 | T064 | `DisponibilidadService.consultar`, `ReservaRepository.findConflictos` |
| FR-015 | US1 | T026 | T063 | Bloqueos pesimistas usuario → recurso |
| FR-016 | Cross-cutting | T021, T023 | T064 | `GlobalExceptionHandler`, `ErrorResponse` |
| FR-017 | US1 | T031 | T060 | `ReservaMapper.toEntity` |
| FR-018 | Cross-cutting | T016–T022 | T061 | DTOs, mappers y controllers |

## Pendientes de cierre

- Implementar T060–T065.
- Ejecutar `./gradlew test`.
- Ejecutar la prueba automatizada de concurrencia con 20–50 hilos.
- Ejecutar JMeter con 500 solicitudes antes de la entrega final (SC-002).
- Validar disponibilidad y errores con pruebas de contrato (SC-005).
