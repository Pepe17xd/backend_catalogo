package com.cinemaclub.catalog.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;
@Configuration
public class OpenApiConfig {
    @Bean OpenAPI catalogOpenApi() { return new OpenAPI().info(new Info().title("Cinema Club Online - Catalog API").version("v2").description("Catálogo y metadatos de reproducción; no gestiona usuarios ni sesiones.")); }
}
