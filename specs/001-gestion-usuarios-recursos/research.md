# Research: Gestión de usuarios y recursos

## Decisiones

### D1 — Mappers manuales en lugar de MapStruct

**Decisión**: clases utilitarias estáticas en el paquete `mapper`.

**Motivo**: el proyecto es pequeño y los mapeos son directos. Los mappers manuales son transparentes, no requieren annotation processor adicional y facilitan la depuración. MapStruct se puede incorporar después si el modelo crece.

**Alternativas descartadas**: MapStruct (más configuración inicial), mapeo dentro de controllers (rompe la separación de capas).

### D2 — DTOs como `record`

**Decisión**: usar `record` para requests y responses.

**Motivo**: Java 21, inmutabilidad, menos boilerplate y validación con Bean Validation directamente en los componentes del record.

### D3 — Entidades con Lombok, sin `@Data`

**Decisión**: `@Getter`, `@Setter`, `@NoArgsConstructor` en entidades; no usar `@Data` ni `@EqualsAndHashCode`.

**Motivo**: evitar `equals/hashCode` generados sobre campos mutables y relaciones JPA; prevenir problemas de proxies y colecciones.

### D4 — Unicidad de documento y correo

**Decisión**: restricciones únicas en base de datos y validación en servicio.

**Motivo**: el enunciado no las exige explícitamente, pero son reglas de consistencia razonables para un catálogo de usuarios. Se documentan como supuesto. El servicio traduce `DataIntegrityViolationException` a 409 `DATOS_DUPLICADOS`.

### D5 — H2 en memoria

**Decisión**: `jdbc:h2:mem:reservas` con `ddl-auto=create-drop`.

**Motivo**: es la dependencia ya incluida y simplifica la ejecución del curso. Los datos se reinician al reiniciar la aplicación. PostgreSQL queda como evolución.

### D6 — Sin paginación en v1

**Decisión**: `GET /usuarios` y `GET /recursos` devuelven listas completas.

**Motivo**: el enunciado no pide paginación y el volumen esperado es bajo. Se puede agregar `Pageable` sin romper el contrato si se diseña con parámetros opcionales.

### D7 — Sin autenticación

**Decisión**: la API es abierta en v1.

**Motivo**: el enunciado no incluye login ni roles. Los actores se representan como datos (`Usuario`), no como identidades autenticadas. Se documenta como supuesto y se deja preparado para incorporar Spring Security después.

### D8 — Tipo de recurso como texto libre

**Decisión**: `tipo` es `String` con longitud máxima 50.

**Motivo**: el enunciado menciona laboratorios, auditorios, salas, equipos y "otros elementos"; no define un catálogo cerrado. Un enum restrictivo obligaría a inventar valores.

## Riesgos

- H2 en memoria no persiste entre reinicios; aceptable para el curso.
- Sin autenticación, cualquier cliente puede administrar el catálogo; aceptado como alcance v1.
- La unicidad de documento/correo es un supuesto; si el curso no la quiere, se retira la restricción.
