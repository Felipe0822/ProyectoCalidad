# Quickstart: Reservas y disponibilidad

## Requisitos

- Feature 001 implementada.
- JDK 21.

## Ejecutar

```bash
export JAVA_HOME=/ruta/al/jdk-21
./gradlew bootRun
```

## Preparar datos

```bash
curl -s -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Pérez","documento":"12345678","correo":"ana@institucion.edu"}'

curl -s -X POST http://localhost:8080/recursos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laboratorio de Redes","descripcion":"20 equipos","tipo":"LABORATORIO","capacidad":20}'
```

## Crear reserva

```bash
curl -i -X POST http://localhost:8080/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "recursoId": 1,
    "fechaInicio": "2026-11-10T09:00:00",
    "fechaFin": "2026-11-10T11:00:00"
  }'
```

## Consultar disponibilidad

```bash
curl "http://localhost:8080/recursos/1/disponibilidad?fechaInicio=2026-11-10T09:00:00&fechaFin=2026-11-10T11:00:00"
```

## Consultar reserva

```bash
curl http://localhost:8080/reservas/1
```

## Consultar por usuario

```bash
curl http://localhost:8080/reservas/usuario/1
```

## Consultar por recurso

```bash
curl http://localhost:8080/reservas/recurso/1
```

## Consultar por fecha

```bash
curl "http://localhost:8080/reservas?fecha=2026-11-10"
```

## Cancelar reserva

```bash
curl -i -X DELETE http://localhost:8080/reservas/1
```

## Casos de error esperados

### Solapamiento

```bash
curl -i -X POST http://localhost:8080/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "recursoId": 1,
    "fechaInicio": "2026-11-10T10:00:00",
    "fechaFin": "2026-11-10T12:00:00"
  }'
```

Respuesta: 409 `RESERVA_SUPERPUESTA`.

### Usuario inactivo

```bash
curl -s -X PATCH http://localhost:8080/usuarios/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado":"INACTIVO"}'
```

Luego intentar reservar: 409 `USUARIO_INACTIVO`.

### Recurso en mantenimiento

```bash
curl -s -X PATCH http://localhost:8080/recursos/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado":"MANTENIMIENTO"}'
```

Luego intentar reservar: 409 `RECURSO_NO_DISPONIBLE`.
