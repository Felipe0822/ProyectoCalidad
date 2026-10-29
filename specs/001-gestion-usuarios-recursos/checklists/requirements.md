# Requirements Quality Checklist: Gestión de usuarios y recursos

**Purpose**: Revisar que la especificación de usuarios y recursos sea completa, clara, consistente y verificable antes de implementar.
**Created**: 2026-10-25
**Reviewed**: 2026-10-29
**Feature**: [spec.md](../spec.md)

## Completitud

- [x] CHK001 Están definidos todos los campos obligatorios de Usuario y Recurso.
- [x] CHK002 Están definidos los estados válidos y sus transiciones permitidas.
- [x] CHK003 Están cubiertos los endpoints de creación, consulta, listado, modificación y cambio de estado.
- [x] CHK004 Están definidos los valores por defecto de estado al crear.
- [x] CHK005 Están cubiertos los casos de documento y correo duplicados.
- [x] CHK006 Está definido qué ocurre al cambiar el estado de un usuario o recurso con reservas existentes.

## Claridad

- [x] CHK007 La regla de unicidad de documento y correo no es ambigua.
- [x] CHK008 El formato de error está especificado con campos y códigos HTTP.
- [x] CHK009 La diferencia entre modificar un recurso y cambiar su estado es explícita.
- [x] CHK010 El tipo de recurso está definido como texto libre y no como catálogo cerrado.
- [x] CHK011 La capacidad se define como informativa y no como restricción de reserva.

## Consistencia

- [x] CHK012 Los estados del enunciado coinciden exactamente con los de la especificación.
- [x] CHK013 Los endpoints y DTOs usan los mismos nombres de campo.
- [x] CHK014 Las reglas de esta feature no contradicen las de la feature 002.
- [x] CHK015 Los supuestos sobre autenticación y alcance están documentados.

## Verificabilidad

- [x] CHK016 Cada requisito funcional tiene al menos un escenario de aceptación.
- [x] CHK017 Cada escenario de aceptación indica resultado observable.
- [x] CHK018 Los criterios de éxito son medibles.
- [x] CHK019 Es posible probar la feature sin depender de la UI.
- [x] CHK020 Los errores esperados tienen código y HTTP status definidos.

## Notes

- Revisado el 2026-10-29 contra `spec.md`, `plan.md`, contratos OpenAPI y código.
- Marcar `[x]` solo cuando el revisor confirme que el criterio de calidad está satisfecho.
- Los ítems sin marcar indican ambigüedad, corrección pendiente o evaluación pendiente.
