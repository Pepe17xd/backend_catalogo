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

## Arquitectura

`catalog-service` es un microservicio REST construido con Spring Boot. Su arquitectura sigue las capas controller, service, repository y model: los controladores exponen el contrato HTTP, `MovieService` concentra los casos de uso del catálogo y Spring Data JPA persiste las entidades en PostgreSQL. Las migraciones Flyway inicializan y versionan el esquema.

El servicio administra metadatos de películas, géneros, artistas, fuentes de video y subtítulos. Los archivos multimedia no se almacenan en esta aplicación; se guardan las URL de las fuentes externas. CORS aplica a `/api/**` y se configura mediante `CORS_ALLOWED_ORIGINS`.

## Requisitos

- Java 17.
- Maven Wrapper incluido (`mvnw` / `mvnw.cmd`).
- Spring Boot 3.3.5.
- PostgreSQL (la configuración y los ejemplos usan PostgreSQL 16).
- Docker Engine y Docker Compose, si se ejecuta con contenedores.

## Ejecución del proyecto

Los comandos disponibles para ejecutar con Docker o directamente con Spring Boot se describen en [Ejecución local](#ejecución-local). Antes de iniciar, copie `.env.example` a `.env` y configure `DB_URL`, `DB_USER` y `DB_PASSWORD` según el modo de despliegue. La aplicación escucha en el puerto `8081` por defecto.

## API Documentation

Base URL: `http://localhost:8081` en la configuración local predeterminada. Todas las respuestas de errores de la API tienen la forma `timestamp`, `status`, `message` y `path`.

### Health

#### GET /actuator/health

Descripción: informa el estado operativo de la aplicación y de sus comprobaciones configuradas. Autenticación: no requerida. Body: no aplica.

### Catalog

#### GET /api/catalog/movies

Descripción: devuelve el listado de películas como resumen de catálogo. Autenticación: no requerida. Body: no aplica.

#### GET /api/catalog/movies/{publicId}

Descripción: devuelve el detalle de una película por su UUID público. Autenticación: no requerida. Parámetros path: `publicId` (UUID). Body: no aplica.

#### GET /api/catalog/movies/{publicId}/session-info

Descripción: devuelve el contrato de reproducción para Cinema Session Service: película, duración, fuente de video prioritaria y subtítulos. Si la película no tiene una fuente de video configurada, responde `422`. Autenticación: no requerida. Parámetros path: `publicId` (UUID). Body: no aplica.

#### POST /api/catalog/movies

Descripción: registra una película y devuelve su detalle con estado `201 Created` y cabecera `Location`. Autenticación: no requerida. Headers: `Content-Type: application/json`.

Body:

```json
{
  "title": "Nueva pelicula",
  "description": "Descripcion de ejemplo.",
  "releaseYear": 2026,
  "durationMinutes": 100,
  "posterUrl": "https://example.com/poster.jpg",
  "backdropUrl": "https://example.com/backdrop.jpg",
  "rating": 8.1,
  "status": "READY",
  "genreSlugs": ["action"],
  "artistIds": [1],
  "videoSources": [
    {
      "quality": "720p",
      "type": "MP4",
      "url": "https://example.com/video.mp4",
      "priority": 0
    }
  ],
  "subtitles": [
    {
      "language": "es",
      "url": "https://example.com/es.vtt"
    }
  ]
}
```

Los campos obligatorios son `title` y `durationMinutes` (mayor que cero). Si se incluyen, `releaseYear` debe estar entre 1888 y 2100 y `rating` entre 0.0 y 10.0. Cada fuente de video requiere `quality` (`auto`, `360p`, `720p` o `1080p`) y `url`; `type` acepta `MP4`, `HLS` o `DASH` y, si se omite, es `MP4`. `priority` es `0` si se omite. `status` acepta `READY`, `OFFLINE` o `PROCESSING` y es `READY` si se omite. Los géneros y artistas solo se relacionan si los slugs e IDs indicados ya existen.

#### GET /api/catalog/home

Descripción: devuelve la película destacada y las secciones `Trending` y `Action`. Autenticación: no requerida. Body: no aplica.

#### GET /api/catalog/search?q={texto}

Descripción: busca coincidencias por título de película, nombre de género o nombre de artista. Autenticación: no requerida. Parámetros query: `q` obligatorio y no vacío. Body: no aplica.

### Otros recursos expuestos

#### GET /v3/api-docs

Descripción: documento OpenAPI generado por springdoc. Autenticación: no requerida. Body: no aplica.

La interfaz Swagger UI está disponible en `GET /swagger-ui.html`.

## Authentication

La aplicación no implementa Spring Security, mecanismos de login, tokens Bearer, roles ni permisos. Por tanto, ninguno de los endpoints documentados requiere la cabecera `Authorization`. La sintaxis `Authorization: Bearer {{token}}` no debe configurarse para esta versión de la API.

## Postman Collection

La colección importable se encuentra en [docs/postman/Backend_Catalogo_API.postman_collection.json](docs/postman/Backend_Catalogo_API.postman_collection.json).

Al importarla, actualice la variable de colección `{{base_url}}` con la URL de la instancia que vaya a consultar, por ejemplo `http://IP_DE_LA_MAQUINA_VIRTUAL:PUERTO`. La variable `{{movie_id}}` contiene inicialmente el UUID de la película de ejemplo Sintel y puede sustituirse por cualquier UUID público retornado por el catálogo.

## Limitaciones conocidas

- No existen endpoints para usuarios, autenticación, roles ni permisos.
- No hay endpoints para crear o administrar géneros y artistas; al crear una película, sus `genreSlugs` y `artistIds` deben existir previamente.
- No existen operaciones `PUT`, `PATCH` ni `DELETE` para películas.
- El servicio registra referencias URL a multimedia; no almacena ni sirve los archivos de video, imágenes o subtítulos.
