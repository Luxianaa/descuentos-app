package bo.edu.usfx.descuentos;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DescuentoController {

    private final CalculadoraDescuentos calculadora;
    private final Estadisticas estadisticas;

    public DescuentoController(CalculadoraDescuentos calculadora, Estadisticas estadisticas) {
        this.calculadora = calculadora;
        this.estadisticas = estadisticas;
    }

    @PostMapping("/descuento")
    public RespuestaDescuento calcular(@RequestBody SolicitudDescuento solicitud) {
        double precioFinal = calculadora.calcularPrecioFinal(solicitud.precio(), solicitud.porcentaje());
        estadisticas.registrarCalculo();
        return new RespuestaDescuento(solicitud.precio(), solicitud.porcentaje(), precioFinal);
    }
}
