# Requirements Quality Checklist: Gestión de usuarios y recursos

**Purpose**: Revisar que la especificación de usuarios y recursos sea completa, clara, consistente y verificable antes de implementar.
**Created**: 2026-10-25
**Feature**: [spec.md](../spec.md)

## Completitud

- [ ] CHK001 Están definidos todos los campos obligatorios de Usuario y Recurso.
- [ ] CHK002 Están definidos los estados válidos y sus transiciones permitidas.
- [ ] CHK003 Están cubiertos los endpoints de creación, consulta, listado, modificación y cambio de estado.
- [ ] CHK004 Están definidos los valores por defecto de estado al crear.
- [ ] CHK005 Están cubiertos los casos de documento y correo duplicados.
- [ ] CHK006 Está definido qué ocurre al cambiar el estado de un usuario o recurso con reservas existentes.

## Claridad

- [ ] CHK007 La regla de unicidad de documento y correo no es ambigua.
- [ ] CHK008 El formato de error está especificado con campos y códigos HTTP.
- [ ] CHK009 La diferencia entre modificar un recurso y cambiar su estado es explícita.
- [ ] CHK010 El tipo de recurso está definido como texto libre y no como catálogo cerrado.
- [ ] CHK011 La capacidad se define como informativa y no como restricción de reserva.

## Consistencia

- [ ] CHK012 Los estados del enunciado coinciden exactamente con los de la especificación.
- [ ] CHK013 Los endpoints y DTOs usan los mismos nombres de campo.
- [ ] CHK014 Las reglas de esta feature no contradicen las de la feature 002.
- [ ] CHK015 Los supuestos sobre autenticación y alcance están documentados.

## Verificabilidad

- [ ] CHK016 Cada requisito funcional tiene al menos un escenario de aceptación.
- [ ] CHK017 Cada escenario de aceptación indica resultado observable.
- [ ] CHK018 Los criterios de éxito son medibles.
- [ ] CHK019 Es posible probar la feature sin depender de la UI.
- [ ] CHK020 Los errores esperados tienen código y HTTP status definidos.

## Notes

- Marcar `[x]` solo cuando el revisor confirme que el criterio de calidad está satisfecho.
- Los ítems sin marcar indican ambigüedad, corrección pendiente o evaluación pendiente.
