# Constitución del Proyecto: Sistema de Reservas de Espacios y Recursos

## Principios Fundamentales

### I. Especificación primero y trazabilidad

Toda funcionalidad nace de una especificación aprobada. Cada requisito funcional (`FR-xxx`) debe tener al menos un escenario de aceptación. Antes de la entrega final, cada requisito debe poder rastrearse hasta una tarea, una prueba automatizada y el código que lo implementa.

### II. Arquitectura primero, pruebas después (iteración actual)

En esta etapa se prioriza una arquitectura limpia, compilable y con límites claros entre capas. Las pruebas automatizadas se incorporarán cuando la estructura de paquetes, el modelo de datos y los contratos REST estén estabilizados. Esto no elimina la obligación de probar: antes de la entrega final, cada regla de negocio debe tener pruebas unitarias, de integración y, donde aplique, de concurrencia. La deuda de pruebas debe quedar registrada y saldarse antes de cerrar el proyecto.

### III. Arquitectura en capas y límites claros

La dependencia permitida es:

```text
Controller → Service → Repository → Base de datos
```

Los controladores no acceden a repositorios ni a entidades JPA. La lógica de negocio vive en la capa de servicio. Se usan DTOs para solicitudes y respuestas; las entidades JPA no se exponen en la API. Los mappers convierten entre capas.

### IV. Contratos REST explícitos y errores uniformes

Todos los endpoints usan JSON, códigos HTTP coherentes y un formato único de error:

```json
{
  "timestamp": "2026-10-25T10:15:30",
  "status": 409,
  "error": "RESERVA_SUPERPUESTA",
  "message": "El recurso ya se encuentra reservado para este horario",
  "path": "/reservas"
}
```

Las excepciones se centralizan con `@RestControllerAdvice`. Los errores de validación se devuelven como 400; los conflictos de negocio como 409; los recursos inexistentes como 404. Nunca se exponen stack traces.

### V. Integridad y concurrencia

Las operaciones que crean o cancelan reservas son transaccionales. La prevención de solapamientos y el límite de reservas activas se garantizan con bloqueos pesimistas sobre usuario y recurso, en ese orden, además de consultas dentro de la misma transacción. No se confía únicamente en validaciones previas en memoria. El sistema debe mantener la consistencia ante solicitudes concurrentes.

### VI. Calidad verificable y automatizable

El build debe quedar preparado para ejecutar pruebas unitarias, de integración y de concurrencia. La configuración de JaCoCo, PIT, SonarQube, Postman y JMeter debe quedar preparada. Las pruebas deben ser deterministas: el tiempo se controla con un `Clock` inyectable y los datos de prueba se crean de forma explícita. La versión final debe cumplir la cobertura mínima que se defina al estabilizar la arquitectura.

### VII. Simplicidad y evolución incremental

Se implementa la solución más simple que cumpla los requisitos. No se agregan autenticación, interfaz gráfica, recurrencia, lista de espera ni integraciones externas en la primera versión. Cada feature se entrega como un incremento independiente y verificable. La complejidad adicional debe justificarse en el plan.

## Restricciones Técnicas

- Java 21, Spring Boot 4.1.1, Gradle 9.8.2, H2, Spring Data JPA, Bean Validation y Lombok.
- Arquitectura de un solo proyecto desplegable; sin microservicios.
- Zona horaria de la institución configurable (`app.timezone`, por defecto `America/Bogota`).
- Fechas y horas con `LocalDateTime` interpretadas en la zona de la institución.
- API REST sin autenticación en la primera versión; los actores se modelan como datos, no como usuarios autenticados.
- Base de datos H2 en memoria para desarrollo y pruebas; PostgreSQL queda como evolución futura.

## Flujo de Desarrollo y Puertas de Calidad

1. Especificación revisada y aprobada.
2. Plan técnico revisado; incluye modelo de datos, contratos y estrategia de concurrencia.
3. Tareas generadas y analizadas; cada tarea indica archivo y dependencia.
4. Implementación por historia de usuario, con compilación verde en cada checkpoint.
5. Al estabilizar la arquitectura, se escriben las pruebas de cada regla de negocio.
6. Cobertura y análisis estático antes de la entrega final.
7. Revisión final contra la especificación y validación con Postman/JMeter cuando aplique.

## Gobernanza

Esta constitución prevalece sobre cualquier práctica o preferencia individual. Cualquier excepción debe documentarse en la sección `Complexity Tracking` del plan y aprobarse en la revisión. Las enmiendas requieren actualizar este archivo, registrar la versión y justificar el cambio. Todas las revisiones deben verificar el cumplimiento de los principios.

**Version**: 1.0.0 | **Ratified**: 2026-10-25 | **Last Amended**: 2026-10-25
