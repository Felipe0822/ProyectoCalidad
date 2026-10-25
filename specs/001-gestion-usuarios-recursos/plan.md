# Implementation Plan: Gestión de usuarios y recursos

**Branch**: `001-gestion-usuarios-recursos` | **Date**: 2026-10-25 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-gestion-usuarios-recursos/spec.md`

## Summary

Implementar la base del sistema: gestión de usuarios y recursos con validaciones, estados, unicidad de documento/correo, DTOs, mappers, manejo global de errores y arquitectura en capas. Esta feature no incluye reservas; prepara el catálogo que consumirá la feature 002.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Bean Validation, Lombok, H2

**Storage**: H2 en memoria (`jdbc:h2:mem:reservas`)

**Testing**: JUnit 5, Mockito, MockMvc, AssertJ. En esta iteración se prioriza la arquitectura; las pruebas se incorporan al estabilizar el código.

**Target Platform**: Servicio REST ejecutado con `./gradlew bootRun`

**Project Type**: Web service / API REST

**Performance Goals**: CRUD de baja latencia; no hay objetivo crítico en esta feature.

**Constraints**: Sin autenticación en v1. Documento y correo únicos. Capacidad mayor que cero. Errores uniformes.

**Scale/Scope**: Catálogo de usuarios y recursos de una institución; listados sin paginación en v1.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Especificación primero: `spec.md` aprobado.
- [x] Arquitectura en capas: Controller → Service → Repository.
- [x] DTOs y mappers; entidades JPA no expuestas.
- [x] Manejo global de errores con `@RestControllerAdvice`.
- [x] Pruebas diferidas a la fase de estabilización, según la decisión del proyecto.
- [x] Sin autenticación en v1, documentado como supuesto.
- [x] Sin complejidad innecesaria: un solo proyecto, H2, sin microservicios.

## Project Structure

### Documentation (this feature)

```text
specs/001-gestion-usuarios-recursos/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
├── checklists/
│   └── requirements.md
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
│   └── RecursoController.java
├── service/
│   ├── UsuarioService.java
│   └── RecursoService.java
├── repository/
│   ├── UsuarioRepository.java
│   └── RecursoRepository.java
├── entity/
│   ├── Usuario.java
│   ├── Recurso.java
│   └── enums/
│       ├── EstadoUsuario.java
│       └── EstadoRecurso.java
├── dto/
│   ├── usuario/
│   │   ├── UsuarioRequest.java
│   │   ├── UsuarioResponse.java
│   │   └── CambioEstadoUsuarioRequest.java
│   ├── recurso/
│   │   ├── RecursoRequest.java
│   │   ├── RecursoResponse.java
│   │   └── CambioEstadoRecursoRequest.java
│   └── error/
│       └── ErrorResponse.java
├── exception/
│   ├── ApiException.java
│   ├── NotFoundException.java
│   ├── ConflictException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java
└── mapper/
    ├── UsuarioMapper.java
    └── RecursoMapper.java
```

**Structure Decision**: Un solo proyecto Spring Boot con paquetes por responsabilidad. Se sigue la estructura recomendada por el enunciado (`controller`, `service`, `repository`, `entity`, `dto`, `exception`, `mapper`) y se agrega `config` para el `Clock`.

## Implementation Phases

### Phase 1 — Configuración base

- Configurar `application.yaml` con H2, JPA, consola H2 y zona horaria.
- Crear `ClockConfig`.
- Verificar que `./gradlew compileJava` pase.

### Phase 2 — Dominio y persistencia

- Crear enums `EstadoUsuario` y `EstadoRecurso`.
- Crear entidades `Usuario` y `Recurso` con restricciones e índices.
- Crear repositorios Spring Data JPA.
- Crear DTOs de request/response y cambio de estado.
- Crear mappers.

### Phase 3 — Servicios y errores

- Crear excepciones de negocio y `GlobalExceptionHandler`.
- Implementar `UsuarioService`: crear, listar, obtener, cambiar estado.
- Implementar `RecursoService`: crear, listar, obtener, actualizar, cambiar estado.
- Validar duplicados y capacidad.

### Phase 4 — API

- Implementar `UsuarioController` y `RecursoController`.
- Verificar códigos HTTP, `Location` en POST y formato de error.
- Probar manualmente con curl/Postman.

### Phase 5 — Cierre de la feature

- Revisar que no haya acceso a repositorios desde controladores.
- Revisar que las entidades no se expongan.
- Actualizar `quickstart.md` con ejemplos.
- Compilar y ejecutar la aplicación.

## Testing Note

Las pruebas se difieren por decisión del proyecto. Antes de la entrega final se deben agregar:

- Unitarias de `UsuarioService` y `RecursoService`.
- Integración de ambos controladores con MockMvc.
- Pruebas de repositorio para unicidad y estados.
- Pruebas de contrato para el formato de error.

## Complexity Tracking

No hay violaciones a la constitución en esta feature. No se requiere justificación adicional.
