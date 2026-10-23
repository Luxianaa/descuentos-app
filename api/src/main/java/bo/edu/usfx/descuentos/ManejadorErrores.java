package bo.edu.usfx.descuentos;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Los errores de la API nunca revelan clases, rutas, versiones ni trazas: el detalle va al registro del servidor
 * y el cliente recibe un mensaje generico (OWASP A05: Security Misconfiguration / fuga de informacion).
 * Extiende ResponseEntityExceptionHandler para que tambien los errores de Spring MVC (ruta inexistente,
 * metodo no permitido, cuerpo ilegible) salgan con el mismo formato.
 */
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> inesperado(Exception e) {
        LOG.error("Error inesperado", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno"));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        LOG.warn("Solicitud rechazada ({}): {}", statusCode.value(), ex.getMessage());
        String mensaje = statusCode.value() == 404
                ? "No encontrado"
                : statusCode.is4xxClientError() ? "La solicitud no es valida" : "Error interno";
        return ResponseEntity.status(statusCode).body(Map.of("error", mensaje));
    }
}
