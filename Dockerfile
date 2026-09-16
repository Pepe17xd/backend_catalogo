# syntax=docker/dockerfile:1
# Build the artifact inside Docker so a clean checkout can build the image.
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Descargar todas las dependencias primero (esta capa se cachea).
# Solo se vuelve a ejecutar si pom.xml cambia, NO cuando cambia el código fuente.
RUN ./mvnw -B dependency:go-offline -q

# Ahora sí copiar el código fuente (esta capa se invalida con cada cambio de código)
COPY src/ src/
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

# curl is used by the container healthcheck. Run the service without root.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring

COPY --from=build /workspace/target/catalog-service-0.0.1-SNAPSHOT.jar app.jar

USER spring
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
