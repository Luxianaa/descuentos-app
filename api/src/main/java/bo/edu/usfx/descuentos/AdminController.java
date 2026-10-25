package bo.edu.usfx.descuentos;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/admin/estadisticas: solo con la cabecera X-Admin-Token correcta.
 * Sin token o con un token incorrecto: 401. Si el servidor no tiene ADMIN_TOKEN configurado: 403.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final VerificadorToken verificador;
    private final Estadisticas estadisticas;

    public AdminController(VerificadorToken verificador, Estadisticas estadisticas) {
        this.verificador = verificador;
        this.estadisticas = estadisticas;
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> estadisticas(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (!verificador.habilitado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Administracion deshabilitada"));
        }
        if (!verificador.autoriza(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
        }
        return ResponseEntity.ok(Map.of("calculos", estadisticas.calculos()));
    }
}
