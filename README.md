# Cinema Club Online — Catalog Service

Microservicio REST de catálogo multimedia con Spring Boot 3, Java 17, PostgreSQL 16 y Flyway. Las imágenes y vídeos se almacenan fuera del servicio; la base de datos guarda las referencias.

## Arquitectura de despliegue

Docker Compose ejecuta dos contenedores separados en una red privada:

- `catalog-service`: API publicada en el puerto `APP_PORT` (8081 por defecto).
- `postgres`: base de datos sin puerto publicado al host y con volumen persistente `catalog_data`.

La API espera a que PostgreSQL esté sano antes de iniciar. Ambos contenedores tienen healthcheck: PostgreSQL usa `pg_isready` y la aplicación usa `GET /actuator/health`.

## Variables de entorno

Copia el ejemplo antes de arrancar:

```powershell
Copy-Item .env.example .env
```

```bash
cp .env.example .env
```

| Variable | Requerida | Uso |
| --- | --- | --- |
| `DB_NAME` | Sí | Nombre de la base que inicializa PostgreSQL. |
| `DB_USER` | Sí | Usuario de PostgreSQL y de la aplicación. |
| `DB_PASSWORD` | Sí | Contraseña de PostgreSQL. No subir al repositorio. |
| `DB_URL` | Solo al ejecutar Spring Boot fuera de Docker | URL JDBC de la base accesible desde el host. |
| `CORS_ALLOWED_ORIGINS` | Recomendado | Orígenes permitidos, separados por coma. En producción debe definirse explícitamente. |
| `APP_PORT` | No | Puerto expuesto de la API; por defecto `8081`. |

Dentro de Compose, `DB_URL` se construye con el hostname de servicio `postgres`; nunca depende de `localhost`. La aplicación recibe siempre `DB_URL`, `DB_USER` y `DB_PASSWORD` por entorno. Para una VM o plataforma, inyecta esas variables mediante su gestor de secretos, sin crear `.env` en la imagen.

## Ejecución local

Para ejecutar todo con contenedores:

```bash
docker compose up --build -d
docker compose ps
curl http://localhost:8081/actuator/health
```

Los datos sobreviven a `docker compose down`. Para eliminarlos deliberadamente, usa `docker compose down -v`.

Para ejecutar solo Spring Boot desde el host, primero proporciona una instancia PostgreSQL accesible según `DB_URL` de `.env` y después:

```powershell
.\mvnw.cmd spring-boot:run
```

```bash
./mvnw spring-boot:run
```

La URL JDBC no está fijada en `application.properties`; se configura con `DB_URL`. La aplicación valida el esquema y Flyway aplica las migraciones al iniciar.

## Imagen Docker

El Dockerfile es multi-stage: compila el JAR desde un checkout limpio con Maven Wrapper y ejecuta una imagen JRE como usuario no privilegiado. Para construir y etiquetar la imagen que se publicará:

```bash
docker build -t antony17xd/catalog-service:v1 .
```

Publicación en Docker Hub (con una sesión autenticada):

```bash
docker login
docker push antony17xd/catalog-service:v1
```

`docker-compose.yml` conserva esa misma etiqueta en `image:` y también declara `build:` para desarrollo o CI. En una VM que deba usar únicamente la imagen publicada:

```bash
docker compose pull --ignore-buildable
docker compose up -d --no-build
```

Instala Docker Engine y Docker Compose en la VM, copia `docker-compose.yml` y un `.env` seguro (o configura las variables en el entorno), abre solamente `APP_PORT` en el firewall y usa un proxy TLS delante de la API. No expongas PostgreSQL públicamente.

## Migraciones e inicialización

`src/main/resources/db/migration/V1__create_catalog_schema.sql` crea las tablas `genre`, `artist`, `movie`, sus relaciones, fuentes de vídeo y subtítulos. También carga géneros, artistas y tres películas de muestra. Flyway registra la migración en su tabla de historial y no la ejecuta de nuevo en bases ya inicializadas; Hibernate usa `ddl-auto=validate`, por lo que no modifica el esquema.

Para cambios futuros, añade una nueva migración versionada (`V2__...sql`); no alteres una migración que ya haya llegado a producción.

## API y gestión de catálogo

Endpoints disponibles:

- `GET /api/catalog/movies`
- `GET /api/catalog/movies/{publicId}`
- `GET /api/catalog/movies/{publicId}/session-info`
- `GET /api/catalog/home`
- `GET /api/catalog/search?q=Animation`
- `POST /api/catalog/movies`

Swagger UI está en `/swagger-ui.html`, OpenAPI en `/v3/api-docs` y el estado operativo en `/actuator/health`.

Para cargar una película usa el endpoint `POST` (actualmente no tiene autenticación ni rol administrativo):

```bash
curl -X POST http://localhost:8081/api/catalog/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nueva película",
    "description": "Descripción",
    "releaseYear": 2026,
    "durationMinutes": 100,
    "rating": 8.1,
    "status": "READY",
    "genreSlugs": ["action"],
    "artistIds": [1],
    "videoSources": [{"quality":"720p","type":"MP4","url":"https://example.com/video.mp4","priority":0}],
    "subtitles": [{"language":"es","url":"https://example.com/es.vtt"}]
  }'
```

Los `genreSlugs` y `artistIds` deben existir previamente; la migración inicial proporciona los géneros y artistas de muestra. No hay controlador ni endpoints `/admin`, ni operaciones `PUT`, `PATCH` o `DELETE`. Para una administración real faltan autenticación/autorización, gestión de géneros/artistas y edición/eliminación de películas. Esas capacidades no se agregaron para no cambiar la lógica de negocio.
