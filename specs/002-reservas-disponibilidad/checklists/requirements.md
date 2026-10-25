# Requirements Quality Checklist: Reservas y disponibilidad

**Purpose**: Revisar que la especificación de reservas sea completa, clara, consistente y verificable antes de implementar.
**Created**: 2026-10-25
**Feature**: [spec.md](../spec.md)

## Completitud

- [ ] CHK001 Están cubiertas las 8 reglas de negocio obligatorias.
- [ ] CHK002 Están definidos los estados de reserva y sus transiciones.
- [ ] CHK003 Está definido el comportamiento de cancelación para ACTIVA, CANCELADA y FINALIZADA.
- [ ] CHK004 Está definida la finalización automática de reservas vencidas.
- [ ] CHK005 Están cubiertas las consultas por usuario, recurso y fecha.
- [ ] CHK006 Está definido el contrato de disponibilidad y sus motivos.
- [ ] CHK007 Está definida la estrategia de concurrencia para evitar duplicados.

## Claridad

- [ ] CHK008 La regla de máximo 3 reservas activas está interpretada sin ambigüedad.
- [ ] CHK009 El solapamiento usa intervalos semiabiertos y está explicado con un ejemplo.
- [ ] CHK010 La zona horaria y el tipo de fecha están definidos.
- [ ] CHK011 La diferencia entre reserva CANCELADA y FINALIZADA es explícita.
- [ ] CHK012 La cancelación idempotente está especificada.
- [ ] CHK013 El comportamiento de usuarios inactivos y recursos no disponibles está claro.

## Consistencia

- [ ] CHK014 Los códigos de error coinciden con los de la feature 001.
- [ ] CHK015 Los estados de usuario y recurso usados son los de la feature 001.
- [ ] CHK016 Las reglas de disponibilidad y creación usan el mismo criterio de solapamiento.
- [ ] CHK017 Los supuestos sobre autenticación, capacidad y recurrencia son coherentes.
- [ ] CHK018 Las respuestas de reserva incluyen usuario y recurso sin exponer entidades.

## Verificabilidad

- [ ] CHK019 Cada regla de negocio tiene al menos un escenario de aceptación.
- [ ] CHK020 Es posible probar la concurrencia con solicitudes simultáneas.
- [ ] CHK021 Los criterios de éxito son medibles.
- [ ] CHK022 Los errores esperados tienen código y HTTP status definidos.
- [ ] CHK023 Es posible validar la feature solo con la API.

## Notes

- Marcar `[x]` solo cuando el revisor confirme que el criterio de calidad está satisfecho.
- Los ítems sin marcar indican ambigüedad, corrección pendiente o evaluación pendiente.
