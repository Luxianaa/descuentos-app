package bo.edu.usfx.descuentos;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS con lista explicita de origenes. Un "*" en CORS_ORIGINS hace fallar el arranque:
 * es mejor que la API no suba a que acepte llamadas desde cualquier sitio (OWASP A05).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origenes;

    public CorsConfig(@Value("${app.cors-origins}") String[] origenes) {
        if (Arrays.stream(origenes).anyMatch(o -> o.trim().equals("*"))) {
            throw new IllegalStateException("CORS_ORIGINS no puede ser '*': lista los origenes permitidos");
        }
        this.origenes = origenes;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(origenes)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type", "X-Admin-Token")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
