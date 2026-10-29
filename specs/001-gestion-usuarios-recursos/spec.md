# Feature Specification: Gestión de usuarios y recursos

**Feature Branch**: `001-gestion-usuarios-recursos`  
**Created**: 2026-10-25  
**Status**: Approved for architecture iteration
**Approved**: 2026-10-29
**Input**: User description: "API REST para administrar usuarios y recursos de una institución: crear, consultar, modificar y cambiar estados, con validaciones y manejo uniforme de errores."

## User Scenarios & Testing

### User Story 1 - Registrar y consultar usuarios (Priority: P1)

Como administrador, quiero registrar usuarios y consultar su información para que puedan ser usados en el sistema de reservas.

**Why this priority**: Sin usuarios no se pueden crear reservas; es la base del dominio.

**Independent Test**: Se puede probar creando un usuario, consultándolo por ID y listando todos los usuarios.

**Acceptance Scenarios**:

1. **Given** un usuario con nombre, documento y correo válidos, **When** se registra, **Then** se crea con estado `ACTIVO` y se devuelve su ID.
2. **Given** un usuario existente, **When** se consulta por ID, **Then** se devuelven sus datos.
3. **Given** varios usuarios, **When** se listan, **Then** se devuelven todos.
4. **Given** un documento o correo ya registrado, **When** se intenta crear otro usuario, **Then** se rechaza con 409.
5. **Given** un correo con formato inválido, **When** se registra, **Then** se rechaza con 400.

---

### User Story 2 - Activar e inactivar usuarios (Priority: P2)

Como administrador, quiero cambiar el estado de un usuario para impedir que usuarios inactivos realicen reservas.

**Why this priority**: El estado del usuario es una regla de negocio de las reservas.

**Independent Test**: Se puede probar creando un usuario, cambiando su estado a `INACTIVO` y consultando el estado actualizado.

**Acceptance Scenarios**:

1. **Given** un usuario `ACTIVO`, **When** se cambia a `INACTIVO`, **Then** el cambio se persiste.
2. **Given** un usuario `INACTIVO`, **When** se cambia a `ACTIVO`, **Then** el cambio se persiste.
3. **Given** un usuario inexistente, **When** se cambia su estado, **Then** se devuelve 404.

---

### User Story 3 - Registrar, modificar y consultar recursos (Priority: P3)

Como administrador, quiero registrar y mantener los recursos reservables para que los usuarios puedan reservarlos.

**Why this priority**: Los recursos son el objeto de las reservas.

**Independent Test**: Se puede probar creando un recurso, consultándolo, modificando sus datos y listándolo.

**Acceptance Scenarios**:

1. **Given** un recurso con nombre, tipo y capacidad válidos, **When** se registra, **Then** se crea con estado `DISPONIBLE`.
2. **Given** un recurso existente, **When** se modifican sus datos, **Then** se actualizan sin cambiar su estado.
3. **Given** un recurso existente, **When** se consulta por ID, **Then** se devuelven sus datos.
4. **Given** varios recursos, **When** se listan, **Then** se devuelven todos.
5. **Given** una capacidad menor o igual a cero, **When** se registra, **Then** se rechaza con 400.

---

### User Story 4 - Cambiar el estado de un recurso (Priority: P4)

Como administrador, quiero cambiar el estado de un recurso para reflejar mantenimiento o fuera de servicio.

**Why this priority**: El estado del recurso determina si puede ser reservado.

**Independent Test**: Se puede probar creando un recurso y cambiando su estado a `MANTENIMIENTO`, `FUERA_DE_SERVICIO` y `DISPONIBLE`.

**Acceptance Scenarios**:

1. **Given** un recurso `DISPONIBLE`, **When** se cambia a `MANTENIMIENTO`, **Then** el cambio se persiste.
2. **Given** un recurso en `MANTENIMIENTO`, **When** se cambia a `FUERA_DE_SERVICIO`, **Then** el cambio se persiste.
3. **Given** un recurso `FUERA_DE_SERVICIO`, **When** se cambia a `DISPONIBLE`, **Then** el cambio se persiste.
4. **Given** un recurso inexistente, **When** se cambia su estado, **Then** se devuelve 404.

### Cross-Cutting Acceptance Scenarios

1. **Given** una solicitud con datos inválidos, **When** se procesa, **Then** la respuesta contiene `status`, `error`, `message`, `timestamp`, `path` y, cuando aplica, `details` por campo.
2. **Given** una excepción de negocio o de validación, **When** llega a la API, **Then** es manejada por `@RestControllerAdvice` sin exponer stack traces.
3. **Given** cualquier endpoint, **When** responde, **Then** usa DTOs y nunca serializa entidades JPA.

### Edge Cases

- Documento o correo duplicado: 409.
- Campos vacíos o nulos: 400 con detalle por campo.
- Capacidad cero o negativa: 400.
- ID inexistente: 404.
- Estado no reconocido en el JSON: 400.
- Cambiar el estado de un usuario o recurso no cancela reservas existentes; solo afecta la creación de nuevas reservas (regla de la feature 002).
- El tipo de recurso es texto libre; no se valida contra un catálogo cerrado.
- No se permite eliminar usuarios ni recursos en esta versión.

## Requirements

### Functional Requirements

- **FR-001**: El sistema DEBE permitir crear usuarios con nombre, documento y correo.
- **FR-002**: El sistema DEBE asignar estado `ACTIVO` a todo usuario nuevo.
- **FR-003**: El sistema DEBE permitir consultar un usuario por ID y listar todos.
- **FR-004**: El sistema DEBE permitir cambiar el estado de un usuario entre `ACTIVO` e `INACTIVO`.
- **FR-005**: El sistema DEBE rechazar documentos o correos duplicados con 409.
- **FR-006**: El sistema DEBE validar el formato del correo y los campos obligatorios.
- **FR-007**: El sistema DEBE permitir crear recursos con nombre, descripción, tipo, capacidad y estado inicial `DISPONIBLE`.
- **FR-008**: El sistema DEBE permitir modificar los datos de un recurso sin alterar su estado.
- **FR-009**: El sistema DEBE permitir consultar un recurso por ID y listar todos.
- **FR-010**: El sistema DEBE permitir cambiar el estado de un recurso entre `DISPONIBLE`, `MANTENIMIENTO` y `FUERA_DE_SERVICIO`.
- **FR-011**: El sistema DEBE validar que la capacidad sea mayor que cero.
- **FR-012**: El sistema DEBE devolver errores uniformes con `status`, `error`, `message`, `timestamp` y `path`.
- **FR-013**: El sistema DEBE centralizar el manejo de excepciones con `@RestControllerAdvice`.
- **FR-014**: El sistema DEBE usar DTOs y no exponer entidades JPA.

### Key Entities

- **Usuario**: id, nombre, documento, correo, estado (`ACTIVO`/`INACTIVO`). Documento y correo únicos.
- **Recurso**: id, nombre, descripción, tipo, capacidad, estado (`DISPONIBLE`/`MANTENIMIENTO`/`FUERA_DE_SERVICIO`). Capacidad mayor que cero.

## Success Criteria

- **SC-001**: Un cliente puede crear, consultar y cambiar el estado de un usuario en menos de 2 minutos usando la API.
- **SC-002**: Un cliente puede crear, consultar y modificar un recurso en menos de 2 minutos usando la API.
- **SC-003**: El 100% de las reglas de negocio de esta feature tiene al menos una prueba automatizada.
- **SC-004**: Las pruebas unitarias y de integración pasan con `./gradlew test`.
- **SC-005**: Los errores de validación y conflicto devuelven el formato uniforme definido.

## Assumptions

- No hay autenticación ni autorización en esta versión; cualquier cliente puede administrar usuarios y recursos.
- El sistema es de una sola institución y una sola zona horaria configurable.
- No se eliminan usuarios ni recursos; solo se cambian de estado.
- El documento y el correo son únicos.
- El tipo de recurso es texto libre.
- Los estados solo toman los valores definidos en el enunciado.
- Cambiar el estado de un usuario o recurso no cancela reservas existentes.
- La capacidad es informativa; no se usa para limitar el número de asistentes porque la reserva no registra asistentes.

## Clarifications

### Session 2026-10-29

- Esta especificación se aprueba para la **iteración de arquitectura**. El cierre de entrega final queda condicionado a completar las pruebas automatizadas y los gates de calidad de la constitución.
- La validación de datos se realiza con Bean Validation y reglas de servicio. La integridad de unicidad de documento y correo también se refuerza en base de datos.
- Las restricciones de capacidad y fechas se refuerzan en base de datos con `CHECK` además de la validación de servicio.
