# Cinema Club Online — Catalog Service v2

Servicio REST de catálogo multimedia construido con Spring Boot y PostgreSQL.

Incluye catálogo inicial, CRUD REST, búsqueda por género y metadatos de reproducción. Los videos e imágenes permanecen en almacenamiento externo; la base de datos guarda sus referencias.

## Requisitos

- Java 17 o superior
- Docker con Docker Compose

No es necesario instalar Maven: el repositorio incluye Maven Wrapper.

## Configuración local

Después de clonar el repositorio, crea tu archivo de variables desde el ejemplo:

**Windows PowerShell**

```powershell
Copy-Item .env.example .env
```

**Linux/macOS**

```bash
cp .env.example .env
```

El archivo `.env` está ignorado por Git. Puedes cambiar sus valores locales; `DB_NAME` dentro de `DB_URL` debe coincidir con la variable `DB_NAME`.

| Variable | Uso |
| --- | --- |
| `DB_NAME` | Base de datos que crea PostgreSQL |
| `DB_USER` | Usuario compartido por PostgreSQL y Spring Boot |
| `DB_PASSWORD` | Contraseña compartida por PostgreSQL y Spring Boot |
| `DB_URL` | URL JDBC usada por Spring Boot |

## Ejecución

Levanta PostgreSQL sin cambiar el puerto existente (`5433` en el host):

```bash
docker compose up -d
```

Luego inicia la aplicación en otra terminal. Spring Boot importa automáticamente el `.env` local y continúa escuchando en el puerto `8081`.

**Windows PowerShell**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS**

```bash
./mvnw spring-boot:run
```

Para detener PostgreSQL sin eliminar sus datos:

```bash
docker compose stop
```

## Endpoints principales

- `GET /api/catalog/movies`
- `GET /api/catalog/movies/{id}`
- `GET /api/catalog/movies/{id}/play`
- `GET /api/catalog/movies/search?genre=Animation`
- `POST /api/catalog/movies`

Ejemplos de verificación:

```bash
curl http://localhost:8081/api/catalog/movies
curl http://localhost:8081/api/catalog/movies/1/play
```

## Compilación

```bash
./mvnw clean package
```

En Windows usa `.\mvnw.cmd clean package`.

## Despliegue

No publiques el archivo `.env`. En cada entorno define `DB_NAME`, `DB_USER`, `DB_PASSWORD` y `DB_URL` mediante el gestor de secretos o variables de entorno de la plataforma.
