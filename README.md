# Sistema de Reservas de Espacios y Recursos

API REST para administrar usuarios, recursos institucionales y reservas, con control de disponibilidad, estados y concurrencia.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- H2 en memoria
- Lombok
- Gradle 9.8.2

## Arquitectura

La aplicación respeta la dependencia obligatoria entre capas:

```text
Controller → Service → Repository → Base de datos
```

- `controller`: expone los endpoints REST y valida las solicitudes.
- `service`: concentra las reglas de negocio y las transacciones.
- `repository`: acceso a datos con Spring Data JPA.
- `entity`: modelo persistente.
- `dto`: contratos de entrada y salida.
- `mapper`: conversión entre entidades y DTOs.
- `exception`: excepciones de negocio y manejo global de errores.
- `config`: configuración de reloj y aplicación.
- `scheduler`: finalización automática de reservas vencidas.

Los controladores no acceden a repositorios ni exponen entidades JPA.

## Estructura

```text
src/main/java/com/institucion/reservas/
├── ReservasApplication.java
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── mapper/
├── repository/
├── scheduler/
└── service/
```

## Ejecución

Requisitos: JDK 21.

```bash
export JAVA_HOME=/ruta/al/jdk-21
./gradlew bootRun
```

La API queda disponible en `http://localhost:8080`.

Consola H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:reservas`
- Usuario: `sa`
- Contraseña: vacía

## Configuración

`src/main/resources/application.yaml` define:

- H2 en memoria con `LOCK_TIMEOUT=10000`.
- `ddl-auto=create-drop`.
- `open-in-view=false`.
- Zona horaria `America/Bogota` mediante `app.timezone`.
- Finalización automática cada 60 segundos mediante `app.scheduling.finalizacion-ms`.

## Endpoints

### Usuarios

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/usuarios` | Crear usuario |
| GET | `/usuarios` | Listar usuarios |
| GET | `/usuarios/{id}` | Consultar usuario |
| PATCH | `/usuarios/{id}/estado` | Activar o inactivar usuario |

### Recursos

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/recursos` | Crear recurso |
| GET | `/recursos` | Listar recursos |
| GET | `/recursos/{id}` | Consultar recurso |
| PUT | `/recursos/{id}` | Modificar recurso sin cambiar estado |
| PATCH | `/recursos/{id}/estado` | Cambiar estado del recurso |
| GET | `/recursos/{id}/disponibilidad` | Consultar disponibilidad por franja |

### Reservas

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/reservas` | Crear reserva |
| GET | `/reservas/{id}` | Consultar reserva |
| DELETE | `/reservas/{id}` | Cancelar reserva |
| GET | `/reservas/usuario/{usuarioId}` | Reservas por usuario |
| GET | `/reservas/recurso/{recursoId}` | Reservas por recurso |
| GET | `/reservas?fecha=YYYY-MM-DD` | Reservas que intersectan el día |

## Reglas de negocio

1. Un usuario `INACTIVO` no puede reservar.
2. Un recurso en `MANTENIMIENTO` o `FUERA_DE_SERVICIO` no puede reservarse.
3. `fechaFin` debe ser posterior a `fechaInicio`.
4. No se permiten reservas superpuestas para el mismo recurso.
5. Una reserva `FINALIZADA` no puede cancelarse.
6. No se permiten reservas con inicio en el pasado.
7. Un usuario puede tener como máximo 3 reservas `ACTIVA` vigentes.
8. Las solicitudes concurrentes no deben generar reservas duplicadas.

### Intervalos

Las reservas usan intervalos semiabiertos `[fechaInicio, fechaFin)`. Una reserva que termina a las 10:00 y otra que empieza a las 10:00 no se consideran superpuestas.

### Estados

- Usuario: `ACTIVO`, `INACTIVO`.
- Recurso: `DISPONIBLE`, `MANTENIMIENTO`, `FUERA_DE_SERVICIO`.
- Reserva: `ACTIVA`, `CANCELADA`, `FINALIZADA`.

Solo las reservas `ACTIVA` bloquean nuevas reservas. La cancelación es lógica: cambia el estado a `CANCELADA` y conserva el historial.

## Concurrencia

La creación de reservas se ejecuta dentro de una transacción y sigue este orden:

1. Validar fechas.
2. Bloquear el usuario con `PESSIMISTIC_WRITE`.
3. Bloquear el recurso con `PESSIMISTIC_WRITE`.
4. Validar estados y límite de reservas activas.
5. Verificar solapamientos.
6. Guardar la reserva.

El orden fijo **usuario → recurso** evita deadlocks. El bloqueo del recurso serializa las reservas de una misma franja y garantiza que no se creen duplicados. Si el bloqueo expira, la API responde `409 CONFLICTO_CONCURRENCIA`.

## Errores

Todas las excepciones se centralizan con `@RestControllerAdvice`. El formato es:

```json
{
  "timestamp": "2026-10-25T16:20:07.855892218",
  "status": 409,
  "error": "RESERVA_SUPERPUESTA",
  "message": "El recurso ya se encuentra reservado para este horario",
  "path": "/reservas"
}
```

Los errores de validación incluyen un mapa `details` con los campos inválidos.

## Documentación de especificación

Los artefactos de SpecKit están en:

- `specs/001-gestion-usuarios-recursos/`
- `specs/002-reservas-disponibilidad/`
- `.specify/memory/constitution.md`

Cada feature incluye especificación, plan, modelo de datos, contratos OpenAPI, quickstart y tareas.

## Pruebas

En esta etapa se prioriza la arquitectura. Las pruebas automatizadas (JUnit, Mockito, MockMvc, concurrencia), la cobertura con JaCoCo, PIT, SonarQube, Postman y JMeter se incorporan después de estabilizar la estructura, antes de la entrega final. La deuda de pruebas está registrada en las tareas de cada feature.
