package bo.edu.usfx.descuentos;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DescuentoController {

    private final CalculadoraDescuentos calculadora;

    public DescuentoController(CalculadoraDescuentos calculadora) {
        this.calculadora = calculadora;
    }

    @PostMapping("/descuento")
    public RespuestaDescuento calcular(@RequestBody SolicitudDescuento solicitud) {
        double precioFinal = calculadora.calcularPrecioFinal(solicitud.precio(), solicitud.porcentaje());
        return new RespuestaDescuento(solicitud.precio(), solicitud.porcentaje(), precioFinal);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
}
