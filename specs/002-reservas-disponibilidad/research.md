# Research: Reservas y disponibilidad

## Decisiones

### D1 — Bloqueo pesimista usuario → recurso

**Decisión**: dentro de la transacción de creación, bloquear primero el usuario y luego el recurso con `PESSIMISTIC_WRITE`.

**Motivo**:
- El bloqueo del usuario serializa el conteo de reservas activas y evita que dos solicitudes del mismo usuario creen la cuarta.
- El bloqueo del recurso serializa la verificación de solapamiento y evita duplicados.
- El orden fijo usuario → recurso evita ciclos de espera y deadlocks.

**Alternativas descartadas**:
- **Validar y luego insertar sin bloqueo**: dos transacciones pueden pasar la validación antes de que la otra confirme; se producen duplicados.
- **Bloqueo optimista con reintentos**: correcto, pero requiere lógica de reintento y puede ser más complejo de explicar y depurar.
- **Restricción de exclusión de PostgreSQL**: ideal para producción, pero H2 no la soporta. Se documenta como evolución.

### D2 — Intervalos semiabiertos

**Decisión**: `[fechaInicio, fechaFin)`.

**Motivo**: permite reservas consecutivas. Si una termina a las 10:00 y otra empieza a las 10:00, no hay conflicto. La consulta es:

```text
existente.fechaInicio < nueva.fechaFin
AND existente.fechaFin > nueva.fechaInicio
```

### D3 — Solo ACTIVA bloquea

**Decisión**: las reservas `CANCELADA` y `FINALIZADA` no participan en la verificación de solapamiento.

**Motivo**: una reserva cancelada libera la franja; una finalizada ya ocurrió y no puede solaparse con una reserva futura válida.

### D4 — Límite de 3 reservas activas

**Decisión**: máximo 3 reservas en estado `ACTIVA` vigentes por usuario, contando las que tienen `fechaFin > now`.

**Motivo**: interpretación literal de "no podrá tener más de 3 reservas activas simultáneamente". Se documenta como supuesto. Si el curso espera "máximo 3 solapadas", se cambia la consulta.

### D5 — Finalización automática

**Decisión**: job periódico cada 30 segundos + estado efectivo en las respuestas.

**Motivo**:
- El enunciado exige el estado `FINALIZADA`, pero no un endpoint para finalizar.
- Un job mantiene la base actualizada sin intervención manual.
- El estado efectivo evita que una reserva vencida se muestre como `ACTIVA` entre ejecuciones del job.
- La cancelación verifica `fechaFin <= now` para rechazar aunque el job no haya corrido.

**Alternativa descartada**: calcular el estado solo al leer, sin persistirlo. El enunciado pide el campo `estado`; persistirlo es más consistente.

### D6 — Cancelación lógica e idempotente

**Decisión**: `DELETE /reservas/{id}` cambia `ACTIVA` a `CANCELADA`. Si ya está `CANCELADA`, responde 204. Si está `FINALIZADA`, responde 409.

**Motivo**: se conserva el historial y se respeta la regla "una reserva finalizada no puede cancelarse". DELETE es idempotente.

### D7 — Fechas con `LocalDateTime`

**Decisión**: `LocalDateTime` en la API y en la base, interpretado en `app.timezone`.

**Motivo**: una sola institución y una sola zona horaria. Se evita la complejidad de offsets múltiples. Si el sistema se multi-institución, se migra a `OffsetDateTime` o `Instant`.

### D8 — Consulta por fecha

**Decisión**: `GET /reservas?fecha=YYYY-MM-DD` devuelve las reservas que intersectan el día completo `[00:00, 24:00)`.

**Motivo**: es más útil que filtrar solo por fecha de inicio; incluye reservas que cruzan el día.

### D9 — Disponibilidad

**Decisión**: `GET /recursos/{id}/disponibilidad?fechaInicio=...&fechaFin=...` devuelve `disponible`, `motivo` y `reservasConflictivas`.

**Motivo**: el cliente puede decidir antes de reservar y mostrar los conflictos. No se lanza 409 en una consulta; se devuelve 200 con `disponible=false`.

## Riesgos

- H2 puede limitar el throughput bajo 500 solicitudes; la prioridad es la consistencia.
- La interpretación del límite de 3 debe confirmarse.
- La diferencia máxima de 1 minuto entre el estado persistido y el efectivo es aceptable.
- Sin autenticación, cualquiera puede cancelar una reserva si conoce el ID; es parte del alcance v1.
