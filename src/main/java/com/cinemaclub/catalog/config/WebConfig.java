package com.cinemaclub.catalog.config;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final List<String> allowedOrigins;
    public WebConfig(@Value("${app.cors.allowed-origins}") List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(allowedOrigins.toArray(String[]::new)).allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").allowedHeaders("*").exposedHeaders("Location").allowCredentials(true).maxAge(3600);
    }
}
