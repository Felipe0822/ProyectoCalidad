# Requirements Quality Checklist: Reservas y disponibilidad

**Purpose**: Revisar que la especificación de reservas sea completa, clara, consistente y verificable antes de implementar.
**Created**: 2026-10-25
**Reviewed**: 2026-10-29
**Feature**: [spec.md](../spec.md)

## Completitud

- [x] CHK001 Están cubiertas las 8 reglas de negocio obligatorias.
- [x] CHK002 Están definidos los estados de reserva y sus transiciones.
- [x] CHK003 Está definido el comportamiento de cancelación para ACTIVA, CANCELADA y FINALIZADA.
- [x] CHK004 Está definida la finalización automática de reservas vencidas.
- [x] CHK005 Están cubiertas las consultas por usuario, recurso y fecha.
- [x] CHK006 Está definido el contrato de disponibilidad y sus motivos.
- [x] CHK007 Está definida la estrategia de concurrencia para evitar duplicados.

## Claridad

- [x] CHK008 La regla de máximo 3 reservas activas está interpretada sin ambigüedad.
- [x] CHK009 El solapamiento usa intervalos semiabiertos y está explicado con un ejemplo.
- [x] CHK010 La zona horaria y el tipo de fecha están definidos.
- [x] CHK011 La diferencia entre reserva CANCELADA y FINALIZADA es explícita.
- [x] CHK012 La cancelación idempotente está especificada.
- [x] CHK013 El comportamiento de usuarios inactivos y recursos no disponibles está claro.

## Consistencia

- [x] CHK014 Los códigos de error coinciden con los de la feature 001.
- [x] CHK015 Los estados de usuario y recurso usados son los de la feature 001.
- [x] CHK016 Las reglas de disponibilidad y creación usan el mismo criterio de solapamiento.
- [x] CHK017 Los supuestos sobre autenticación, capacidad y recurrencia son coherentes.
- [x] CHK018 Las respuestas de reserva incluyen usuario y recurso sin exponer entidades.

## Verificabilidad

- [x] CHK019 Cada regla de negocio tiene al menos un escenario de aceptación.
- [x] CHK020 Es posible probar la concurrencia con solicitudes simultáneas.
- [x] CHK021 Los criterios de éxito son medibles.
- [x] CHK022 Los errores esperados tienen código y HTTP status definidos.
- [x] CHK023 Es posible validar la feature solo con la API.

## Notes

- Revisado el 2026-10-29 contra `spec.md`, `plan.md`, contratos OpenAPI y código.
- Marcar `[x]` solo cuando el revisor confirme que el criterio de calidad está satisfecho.
- Los ítems sin marcar indican ambigüedad, corrección pendiente o evaluación pendiente.
