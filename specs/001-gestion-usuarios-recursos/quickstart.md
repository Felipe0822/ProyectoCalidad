# Quickstart: Gestión de usuarios y recursos

## Requisitos

- JDK 21
- Gradle wrapper incluido

## Ejecutar

```bash
export JAVA_HOME=/ruta/al/jdk-21
./gradlew bootRun
```

La API queda en `http://localhost:8080`.

Consola H2: `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:reservas`  
Usuario: `sa`  
Contraseña: vacía

## Crear usuario

```bash
curl -i -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana Pérez",
    "documento": "12345678",
    "correo": "ana.perez@institucion.edu"
  }'
```

## Listar usuarios

```bash
curl http://localhost:8080/usuarios
```

## Consultar usuario

```bash
curl http://localhost:8080/usuarios/1
```

## Inactivar usuario

```bash
curl -i -X PATCH http://localhost:8080/usuarios/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado":"INACTIVO"}'
```

## Crear recurso

```bash
curl -i -X POST http://localhost:8080/recursos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laboratorio de Redes",
    "descripcion": "Laboratorio con 20 equipos",
    "tipo": "LABORATORIO",
    "capacidad": 20
  }'
```

## Modificar recurso

```bash
curl -i -X PUT http://localhost:8080/recursos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laboratorio de Redes y Comunicaciones",
    "descripcion": "Laboratorio actualizado",
    "tipo": "LABORATORIO",
    "capacidad": 25
  }'
```

## Cambiar estado de recurso

```bash
curl -i -X PATCH http://localhost:8080/recursos/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado":"MANTENIMIENTO"}'
```

## Error esperado por duplicado

```bash
curl -i -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Otra Ana",
    "documento": "12345678",
    "correo": "otra@institucion.edu"
  }'
```

Respuesta esperada: HTTP 409 con `error: "DATOS_DUPLICADOS"`.
