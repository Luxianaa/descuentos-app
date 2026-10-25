package bo.edu.usfx.descuentos;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Ejercicio 1: Limita a 60 solicitudes por minuto por direccion IP.
 * Si se supera, responde 429 Too Many Requests con la cabecera Retry-After: 60.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class FiltroLimiteTasa extends OncePerRequestFilter {

    static final int LIMITE_SOLICITUDES = 60;
    static final long VENTANA_MILISEGUNDOS = 60_000L;
    static final int REINTENTO_SEGUNDOS = 60;

    private final Map<String, Deque<Long>> solicitudesPorIp = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String ip = obtenerIpCliente(req);

        if (!permitirSolicitud(ip)) {
            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setHeader("Retry-After", String.valueOf(REINTENTO_SEGUNDOS));
            res.getWriter().write("{\"error\":\"Demasiadas solicitudes. Limite excedido: 60 solicitudes por minuto.\"}");
            return;
        }

        chain.doFilter(req, res);
    }

    private synchronized boolean permitirSolicitud(String ip) {
        long ahora = Instant.now().toEpochMilli();
        Deque<Long> tiempos = solicitudesPorIp.computeIfAbsent(ip, k -> new ArrayDeque<>());

        while (!tiempos.isEmpty() && ahora - tiempos.peekFirst() > VENTANA_MILISEGUNDOS) {
            tiempos.pollFirst();
        }

        if (tiempos.size() >= LIMITE_SOLICITUDES) {
            return false;
        }

        tiempos.addLast(ahora);
        return true;
    }

    private String obtenerIpCliente(HttpServletRequest req) {
        String xForwardedFor = req.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return req.getRemoteAddr() != null ? req.getRemoteAddr() : "desconocido";
    }

    void reset() {
        solicitudesPorIp.clear();
    }
}
