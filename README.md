# Meeting Room API

API REST para reservar salas de reuniones.
**Java 17 · Spring Boot 3.3 · PostgreSQL · Flyway · Spring Security (Basic Auth) · Swagger UI**

## Cumplimiento del enunciado

| # | Requisito | Dónde |
|---|-----------|-------|
| 1 | Crear/listar salas; crear/listar/cancelar reservas (sala, responsable, inicio, fin) | `RoomController`, `ReservationController` |
| 2 | 409 si la sala está ocupada; 400 claros en validación | `ReservationServiceImpl`, `GlobalExceptionHandler` |
| 3 | Listado de reservas filtrado por sala y fecha, paginado | `GET /api/reservations` + `ReservationSpecifications` |
| 4 | PostgreSQL con migraciones Flyway | `src/main/resources/db/migration` |
| 5 | Escritura autenticada (Basic), consulta pública | `SecurityConfig` |
| 6 | 11 tests: 4 unitarios y 7 de integración (MockMvc) | `src/test` |
| 7 | README con ejecución, decisiones, mejoras y uso de IA | este documento |

## Cómo ejecutarlo

### Requisitos
- Java 17 o superior
- PostgreSQL 14+ escuchando en el puerto **5416** (configurable, ver más abajo)
- No hace falta instalar Maven: el repo incluye el wrapper (`mvnw`)

### 1. Crear la base de datos
Conéctate con un usuario administrador (por ejemplo desde DBeaver o `psql`) y ejecuta:

```sql
CREATE USER meeting_user WITH PASSWORD 'meeting_pass';
CREATE DATABASE meeting_room_db OWNER meeting_user;
```

### 2. Arrancar la aplicación

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows (PowerShell)
.\mvnw.cmd spring-boot:run
```

Flyway crea las tablas automáticamente en el primer arranque. La API queda en `http://localhost:8080`.

Configuración por variables de entorno (todas opcionales):

| Variable | Valor por defecto |
|----------|-------------------|
| `DB_URL` | `jdbc:postgresql://localhost:5416/meeting_room_db` |
| `DB_USERNAME` | `meeting_user` |
| `DB_PASSWORD` | `meeting_pass` |

Si el puerto 8080 está ocupado: `--server.port=8081` (por ejemplo `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`).

### 3. Probar la API
**Swagger UI:** http://localhost:8080/swagger-ui.html

1. Pulsa **Authorize** e introduce `admin` / `admin123`.
2. Crea una sala con `POST /api/rooms`.
3. Crea una reserva con `POST /api/reservations` usando el `id` de la sala.
4. Repite la misma reserva (o una que se solape) para ver el **409**.
5. Cancela con `PATCH /api/reservations/{id}/cancel`.

> Swagger envía las credenciales en todas las peticiones una vez autorizado. Para comprobar que
> las consultas son públicas, usa `curl` sin credenciales (ver ejemplos abajo).

### 4. Tests

```bash
./mvnw test        # Windows: .\mvnw.cmd test
```

Los tests usan **H2 en memoria**, así que no requieren PostgreSQL ni Docker.

- **Unitarios (4)** – `ReservationServiceTest` (Mockito): fechas invertidas, sala inexistente, solape y cancelación repetida.
- **Integración (7)** – `@SpringBootTest` + MockMvc con el contexto completo, migraciones y seguridad:
    - `RoomControllerTest`: crear sala (201), nombre vacío (400), crear sin credenciales (401), listar sin credenciales (200).
    - `ReservationControllerTest`: solape (409), cancelar autenticado (200 y estado `CANCELLED`), crear sin credenciales (401).

## API

### Autenticación
HTTP **Basic Auth** en los endpoints que crean o cancelan. Las consultas (`GET`) son públicas.
Usuario de demostración: `admin` / `admin123` (en memoria, solo para esta prueba).

### Endpoints

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/rooms` | No | Listar salas. Parámetros: `search`, `page`, `size`, `sortBy` (`name`, `capacity`, `createdAt`), `sortDir` |
| GET | `/api/rooms/{id}` | No | Detalle de una sala |
| POST | `/api/rooms` | Sí | Crear sala |
| GET | `/api/reservations` | No | Listar reservas. Parámetros: `roomId`, `startTime`, `endTime`, `search`, `page`, `size`, `sortBy` (`startTime`, `endTime`, `createdAt`, `responsible`), `sortDir` |
| GET | `/api/reservations/{id}` | No | Detalle de una reserva |
| POST | `/api/reservations` | Sí | Crear reserva |
| PATCH | `/api/reservations/{id}/cancel` | Sí | Cancelar reserva |

Paginación: `page` empieza en 0 y `size` va de 1 a 100 (por defecto 10).

### Reglas de negocio
- Una reserva tiene sala, responsable, `startTime` y `endTime` (instantes UTC en formato ISO-8601).
- `endTime` debe ser posterior a `startTime`.
- **Solape:** dos reservas activas de la misma sala se solapan si `inicioA < finB` y `finA > inicioB`.
  Reservas contiguas (una termina cuando empieza la otra) **no** se consideran solapadas.
- Las reservas canceladas no bloquean la sala: la misma franja puede reservarse de nuevo.
- Cancelar cambia el estado a `CANCELLED`; la fila no se borra.
- **Filtro por fecha:** `startTime` y `endTime` devuelven las reservas que se solapan con ese rango.
  Para un día completo: `startTime=2026-11-16T00:00:00Z&endTime=2026-11-17T00:00:00Z`.

### Códigos de error

| Código | Cuándo |
|--------|--------|
| 400 | Validación del cuerpo, JSON mal formado, UUID o parámetros inválidos (página negativa, `size` fuera de rango, `sortBy` no permitido), fechas invertidas, nombre de sala duplicado, cancelar una reserva ya cancelada |
| 401 | Endpoint de escritura sin credenciales válidas |
| 404 | La sala o la reserva no existe |
| 409 | La sala ya tiene una reserva activa en ese intervalo |
| 500 | Error inesperado (se registra en el log) |

Todas las respuestas, salvo el 401, usan el mismo formato:

```json
{ "success": false, "message": "La sala ya tiene una reserva activa en ese intervalo", "data": null, "timestamp": "2026-11-15T22:04:57Z" }
```

Un 400 de validación detalla el campo que falló:

```json
{
  "success": false,
  "message": "Error de validación",
  "data": {
    "name": "El nombre de la sala es obligatorio",
    "capacity": "La capacidad debe ser mayor que cero"
  },
  "timestamp": "2026-11-15T22:10:00Z"
}
```

### Ejemplos con curl (bash)

```bash
# Crear sala (requiere auth)
curl -u admin:admin123 -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -d '{"name":"Sala Principal","capacity":10}'

# Crear reserva (requiere auth)
curl -u admin:admin123 -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"roomId":"<uuid-de-la-sala>","responsible":"Ulises","startTime":"2026-11-16T14:00:00Z","endTime":"2026-11-16T15:00:00Z"}'

# Listar con filtros y paginación (público, sin credenciales)
curl "http://localhost:8080/api/reservations?roomId=<uuid-de-la-sala>&startTime=2026-11-16T00:00:00Z&endTime=2026-11-17T00:00:00Z&page=0&size=5&sortBy=startTime&sortDir=asc"

# Escritura sin credenciales -> 401
curl -i -X POST http://localhost:8080/api/rooms
```

En Windows PowerShell usa `curl.exe` en lugar de `curl`, o prueba directamente desde Swagger UI.

## Estructura del proyecto

```
src/main/java/com/yerandis/meetingRoom
├── config        SecurityConfig (Basic Auth), OpenApiConfig (Swagger)
├── controller    RoomController, ReservationController
├── dto           request / response (records) y envoltorios ApiResponse, PageResponse
├── entity        Room, Reservation, ReservationStatus
├── exception     BusinessException, ResourceNotFoundException, GlobalExceptionHandler
├── mapper        RoomMapper, ReservationMapper
├── repository    Repositorios JPA y ReservationSpecifications (filtros dinámicos)
└── service       Interfaces e implementaciones con la lógica de negocio
src/main/resources/db/migration   V1 (rooms), V2 (reservations)
src/test                          Tests y migraciones H2
```

## Decisiones técnicas

- **Basic Auth en lugar de JWT.** El enunciado acepta ambos y, con el tiempo disponible, Basic cubre el requisito con menos piezas que mantener. Las credenciales están en memoria y son solo de demostración.
- **Flyway con `ddl-auto=validate`.** El esquema solo cambia mediante migraciones versionadas; Hibernate únicamente lo valida.
- **Restricciones también en la base de datos:** `CHECK (end_time > start_time)`, `UNIQUE` en el nombre de la sala, `CHECK` del estado, clave foránea e índices por sala/estado/fecha. La base protege la integridad aunque alguien se salte la API.
- **Estado como enum de texto.** `ReservationStatus` se guarda como `VARCHAR` (`ACTIVE`/`CANCELLED`) con `CHECK`, no como ordinal. Cancelar es un cambio de estado, no un borrado, para conservar el historial.
- **Fechas como `Instant` / `TIMESTAMPTZ`.** Todo en UTC, sin ambigüedades de zona horaria.
- **Filtros dinámicos con `Specification`.** Las primeras consultas JPQL con `(:param IS NULL OR ...)` fallaron en PostgreSQL (`function lower(bytea) does not exist`) porque el parámetro nulo no tiene tipo. Con `Specification` el filtro solo se añade cuando el parámetro existe y nunca viaja un nulo a la base.
- **Validación en capas:** Bean Validation en los DTOs (400 con detalle por campo), reglas de negocio en el servicio y restricciones en la base de datos.
- **Paginación y orden seguros.** Tamaño de página máximo de 100 y lista blanca de campos de ordenamiento.
- **Respuesta uniforme** (`ApiResponse`) y manejador global de excepciones; los errores inesperados se registran en el log y no exponen detalles internos.
- **Tests con H2 y migraciones propias** para poder ejecutarlos sin Docker ni PostgreSQL.
- **DTOs como `record`**, inyección por constructor y `open-in-view=false`.
- **Swagger UI (springdoc)** para que cualquiera pueda explorar y probar la API sin herramientas externas.

## Qué haría con más tiempo

- **Concurrencia en las reservas.** La comprobación de solape y el guardado no son atómicos: dos peticiones simultáneas podrían crear reservas solapadas. La solución sería un bloqueo pesimista sobre la sala o una *exclusion constraint* de PostgreSQL (`tstzrange` + `btree_gist`).
- **Testcontainers con PostgreSQL real.** El fallo de `lower(bytea)` pasó los tests con H2 y solo apareció contra PostgreSQL. Además eliminaría las migraciones duplicadas de H2.
- **Códigos más precisos:** 409 para sala duplicada y para cancelar una reserva ya cancelada (hoy ambos devuelven 400), y 401/403 con el mismo formato JSON que el resto de errores.
- **Seguridad:** JWT con expiración, usuarios y roles en base de datos, contraseñas fuera del código y reglas de quién puede cancelar qué reserva.
- **Reglas de negocio:** no permitir reservas en el pasado, duración máxima y horario laboral.
- **Filtro por fecha más cómodo:** un parámetro `date=2026-11-16` que cubra el día completo.
- Dockerizar la aplicación, integración continua con GitHub Actions y limitación de peticiones en los endpoints de escritura.

## Uso de IA

Usé un asistente de IA (Claude) a lo largo de todo el trabajo. Detallo qué hizo cada parte.

**Qué le pedí**
- Una vez analizada y escogida la estructura base del proyecto a desarrollar le pedí que generara las migraciones de Flyway, a partir de las entidades ya desarrolladas.
- Proponer e implementar los tests y la documentación con Swagger.
- Ayudarme a diagnosticar los errores que fueron apareciendo.

**Qué acepté**
- El diseño de las migraciones y sus restricciones, y la lógica de detección de solapes.
- Basic Auth como mecanismo de autenticación.
- Los tests propuestos para el servicio y los controladores.

**Qué detecté, corregí o decidí yo**
- **La estructura base del proyecto**
- **Puerto de la base de datos:** la aplicación apuntaba al 5432 por defecto mientras mi PostgreSQL corre en el 5416; lo alineé.
- **Fallo de los listados en PostgreSQL:** lo encontré probando manualmente con Swagger (500 en `GET /api/rooms` y en `GET /api/reservations` con filtros). El log mostraba `function lower(bytea) does not exist`; los tests con H2 no lo detectaban. Lo resolví migrando los filtros a `Specification`.
- **Orden de reglas de seguridad:** al añadir Swagger, colocar la regla pública después de `anyRequest()` impedía arrancar la aplicación; lo corregí poniéndola antes.
- **Errores 500 que debían ser 400:** el manejador global convertía en 500 los JSON mal formados, los UUID inválidos y los parámetros fuera de rango; añadí handlers específicos.
- **Dependencias sin uso:** retiré las de JWT, ya que opté por Basic Auth.
- **Decidí no implementar** JWT, Testcontainers ni la protección frente a concurrencia, y lo dejé documentado en "Qué haría con más tiempo".

**Verificación:** ejecuté la batería de tests (`mvn test`) y probé a mano todos los endpoints desde Swagger UI contra PostgreSQL real, incluyendo 409, 401, 400, 404, filtros y paginación.

## Flujo de trabajo con Git

- `main`: versión estable y entregable (etiquetada `v1.0.0`).
- `develop`: rama de integración; todos los cambios entran mediante Pull Request.
- Ramas por cambio: `feature/*`, `fix/*`, `test/*`.
- Commits siguiendo *Conventional Commits* (`feat:`, `fix:`, `test:`).