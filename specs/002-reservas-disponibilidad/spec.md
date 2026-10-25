# Feature Specification: Reservas y disponibilidad

**Feature Branch**: `002-reservas-disponibilidad`  
**Created**: 2026-10-25  
**Status**: Draft  
**Input**: User description: "API REST para crear, cancelar y consultar reservas de recursos, aplicando reglas de disponibilidad, estados y límite de reservas activas."

## User Scenarios & Testing

### User Story 1 - Crear una reserva válida (Priority: P1)

Como usuario, quiero reservar un recurso en una franja horaria disponible para asegurar su uso sin conflictos.

**Why this priority**: Es el objetivo central del sistema.

**Independent Test**: Se puede probar creando un usuario `ACTIVO` y un recurso `DISPONIBLE`, y luego creando una reserva en una franja libre.

**Acceptance Scenarios**:

1. **Given** un usuario `ACTIVO`, un recurso `DISPONIBLE` y una franja futura válida, **When** se crea la reserva, **Then** se registra con estado `ACTIVA` y `fechaCreacion`.
2. **Given** un usuario `INACTIVO`, **When** intenta reservar, **Then** se rechaza con 409 `USUARIO_INACTIVO`.
3. **Given** un recurso en `MANTENIMIENTO` o `FUERA_DE_SERVICIO`, **When** se intenta reservar, **Then** se rechaza con 409 `RECURSO_NO_DISPONIBLE`.
4. **Given** una `fechaFin` anterior o igual a `fechaInicio`, **When** se intenta reservar, **Then** se rechaza con 400 `FECHAS_INVALIDAS`.
5. **Given** una `fechaInicio` anterior al momento actual, **When** se intenta reservar, **Then** se rechaza con 400 `FECHA_INICIO_EN_PASADO`.
6. **Given** una reserva `ACTIVA` que se solapa con la franja solicitada, **When** se intenta reservar, **Then** se rechaza con 409 `RESERVA_SUPERPUESTA`.
7. **Given** un usuario con 3 reservas `ACTIVA` vigentes, **When** intenta crear una cuarta, **Then** se rechaza con 409 `LIMITE_RESERVAS_ACTIVAS`.
8. **Given** dos solicitudes concurrentes para la misma franja y recurso, **When** se procesan, **Then** solo una se confirma y la otra recibe 409.
9. **Given** una reserva `CANCELADA` o `FINALIZADA` que se solapa, **When** se intenta reservar, **Then** la nueva reserva se permite.

---

### User Story 2 - Cancelar una reserva (Priority: P2)

Como usuario, quiero cancelar una reserva activa para liberar el recurso.

**Why this priority**: Permite corregir errores y liberar disponibilidad.

**Independent Test**: Se puede probar creando una reserva `ACTIVA` y cancelándola; luego se verifica que el recurso queda disponible.

**Acceptance Scenarios**:

1. **Given** una reserva `ACTIVA`, **When** se cancela, **Then** pasa a `CANCELADA` y libera la franja.
2. **Given** una reserva `FINALIZADA`, **When** se intenta cancelar, **Then** se rechaza con 409 `RESERVA_FINALIZADA`.
3. **Given** una reserva ya `CANCELADA`, **When** se intenta cancelar de nuevo, **Then** se responde 204 de forma idempotente.
4. **Given** un ID inexistente, **When** se intenta cancelar, **Then** se devuelve 404.
5. **Given** una reserva `ACTIVA` cuya `fechaFin` ya pasó, **When** se intenta cancelar, **Then** se considera `FINALIZADA` y se rechaza con 409.

---

### User Story 3 - Consultar reservas (Priority: P3)

Como administrador o usuario, quiero consultar reservas por usuario, por recurso y por fecha para auditar la ocupación.

**Why this priority**: Permite verificar el estado del sistema y alimentar reportes.

**Independent Test**: Se pueden crear varias reservas y consultarlas por cada criterio.

**Acceptance Scenarios**:

1. **Given** un usuario con reservas, **When** se consultan por su ID, **Then** se devuelven sus reservas.
2. **Given** un recurso con reservas, **When** se consultan por su ID, **Then** se devuelven sus reservas.
3. **Given** una fecha, **When** se consultan las reservas, **Then** se devuelven las que intersectan ese día.
4. **Given** un usuario o recurso inexistente, **When** se consulta, **Then** se devuelve 404.
5. **Given** un usuario o recurso sin reservas, **When** se consulta, **Then** se devuelve una lista vacía.

---

### User Story 4 - Consultar disponibilidad (Priority: P4)

Como usuario, quiero consultar si un recurso está disponible en una franja antes de reservar.

**Why this priority**: Mejora la experiencia y reduce intentos fallidos.

**Independent Test**: Se puede consultar la disponibilidad de un recurso libre y de uno ocupado.

**Acceptance Scenarios**:

1. **Given** un recurso `DISPONIBLE` sin solapamientos, **When** se consulta una franja futura válida, **Then** se devuelve `disponible=true`.
2. **Given** una reserva `ACTIVA` que se solapa, **When** se consulta, **Then** se devuelve `disponible=false` con las reservas conflictivas.
3. **Given** un recurso en `MANTENIMIENTO` o `FUERA_DE_SERVICIO`, **When** se consulta, **Then** se devuelve `disponible=false` con el motivo.
4. **Given** una `fechaFin` anterior o igual a `fechaInicio`, **When** se consulta, **Then** se rechaza con 400 `FECHAS_INVALIDAS`.
5. **Given** un recurso inexistente, **When** se consulta, **Then** se devuelve 404.

### Edge Cases

- Reservas consecutivas: si una termina a las 10:00 y otra empieza a las 10:00, no hay solapamiento (intervalos semiabiertos).
- Reserva con inicio y fin iguales: inválida.
- Reserva que empieza exactamente ahora: permitida si `fechaInicio >= now`.
- Reserva con `fechaFin` exactamente ahora: se considera finalizada.
- Reserva cancelada: no bloquea nuevas reservas.
- Reserva finalizada: no bloquea nuevas reservas.
- Usuario con 3 reservas activas: no puede crear la cuarta hasta cancelar o finalizar una.
- Usuario inactivo: no puede crear reservas, pero sus reservas existentes no se cancelan automáticamente.
- Recurso en mantenimiento: no puede recibir nuevas reservas, pero las existentes no se cancelan automáticamente.
- Solicitudes concurrentes para el mismo recurso: solo una debe confirmarse.
- Solicitudes concurrentes del mismo usuario para distintos recursos: el límite de 3 se respeta.
- Zona horaria: todas las fechas se interpretan en la zona configurada de la institución.
- Reintento de la misma solicitud: no se exige idempotencia en esta versión; el solapamiento evita duplicados exactos.

## Requirements

### Functional Requirements

- **FR-001**: El sistema DEBE permitir crear una reserva con usuario, recurso, `fechaInicio` y `fechaFin`.
- **FR-002**: El sistema DEBE asignar estado `ACTIVA` y `fechaCreacion` al crear una reserva.
- **FR-003**: El sistema DEBE rechazar reservas de usuarios `INACTIVOS`.
- **FR-004**: El sistema DEBE rechazar reservas de recursos en `MANTENIMIENTO` o `FUERA_DE_SERVICIO`.
- **FR-005**: El sistema DEBE validar que `fechaFin` sea posterior a `fechaInicio`.
- **FR-006**: El sistema DEBE rechazar reservas con `fechaInicio` anterior al momento actual.
- **FR-007**: El sistema DEBE impedir solapamientos con reservas `ACTIVAS` del mismo recurso, usando intervalos semiabiertos.
- **FR-008**: El sistema DEBE permitir como máximo 3 reservas `ACTIVAS` vigentes por usuario.
- **FR-009**: El sistema DEBE permitir cancelar una reserva `ACTIVA` y cambiarla a `CANCELADA`.
- **FR-010**: El sistema DEBE rechazar la cancelación de una reserva `FINALIZADA`.
- **FR-011**: El sistema DEBE finalizar automáticamente las reservas `ACTIVAS` cuya `fechaFin` ya pasó.
- **FR-012**: El sistema DEBE permitir consultar reservas por usuario, por recurso y por fecha.
- **FR-013**: El sistema DEBE permitir consultar la disponibilidad de un recurso para una franja.
- **FR-014**: El sistema DEBE devolver las reservas conflictivas al consultar disponibilidad.
- **FR-015**: El sistema DEBE garantizar la consistencia ante solicitudes concurrentes mediante transacciones y bloqueos.
- **FR-016**: El sistema DEBE devolver errores uniformes y códigos HTTP coherentes.
- **FR-017**: El sistema DEBE registrar la fecha de creación de la reserva.
- **FR-018**: El sistema DEBE usar DTOs y no exponer entidades JPA.

### Key Entities

- **Reserva**: id, usuario, recurso, `fechaInicio`, `fechaFin`, estado (`ACTIVA`/`CANCELADA`/`FINALIZADA`), `fechaCreacion`.
- **Usuario**: referencia por ID; debe estar `ACTIVO` para reservar.
- **Recurso**: referencia por ID; debe estar `DISPONIBLE` para reservar.

## Success Criteria

- **SC-001**: El 100% de las reglas de negocio obligatorias tiene al menos una prueba automatizada.
- **SC-002**: Bajo 500 solicitudes concurrentes sobre un conjunto limitado de recursos, no se crea ninguna reserva duplicada ni superpuesta.
- **SC-003**: Las solicitudes que pierden la carrera por una franja reciben 409, no 500.
- **SC-004**: Las pruebas unitarias, de integración y de concurrencia pasan con `./gradlew test`.
- **SC-005**: La consulta de disponibilidad devuelve una respuesta consistente con la creación de reservas.
- **SC-006**: El sistema finaliza automáticamente las reservas vencidas en un intervalo máximo de 1 minuto.

## Assumptions

- "Más de 3 reservas activas simultáneamente" se interpreta como máximo 3 reservas en estado `ACTIVA` vigentes por usuario, no como máximo 3 reservas que se solapen entre sí.
- Solo las reservas `ACTIVAS` bloquean nuevas reservas; `CANCELADA` y `FINALIZADA` no bloquean.
- Los intervalos son semiabiertos: `[fechaInicio, fechaFin)`.
- La zona horaria es única y configurable; por defecto `America/Bogota`.
- No hay autenticación; el usuario se identifica por ID en la solicitud.
- No hay aprobación, lista de espera, recurrencia ni notificaciones en esta versión.
- La capacidad del recurso es informativa; la reserva no registra número de asistentes.
- Cambiar el estado de un usuario o recurso no cancela reservas existentes.
- La finalización automática se implementa con un job periódico y estado efectivo en las respuestas; puede existir una diferencia máxima de 1 minuto en la base de datos.
- No se exige idempotencia explícita en la creación; el solapamiento impide duplicados exactos.
