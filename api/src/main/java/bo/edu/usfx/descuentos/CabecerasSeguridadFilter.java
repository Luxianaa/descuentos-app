package bo.edu.usfx.descuentos;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Agrega a TODA respuesta de la API las cabeceras que piden OWASP Secure Headers y ZAP:
 * la API solo devuelve JSON, asi que la politica de contenido puede ser la mas estricta.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CabecerasSeguridadFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'");
        response.setHeader("Referrer-Policy", "no-referrer");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Permissions-Policy", "geolocation=(), camera=(), microphone=()");
        chain.doFilter(request, response);
    }
}
