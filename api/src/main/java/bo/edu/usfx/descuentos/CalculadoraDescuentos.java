package bo.edu.usfx.descuentos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio (las mismas de la Practica 6):
 * porcentaje fuera de [0, 100] y precio menor o igual que cero son invalidos;
 * el precio final se redondea a dos decimales, la mitad hacia arriba.
 */
@Service
public class CalculadoraDescuentos {

    public double calcularPrecioFinal(double precioOriginal, double porcentajeDescuento) {
        if (precioOriginal <= 0) {
            throw new IllegalArgumentException("El precio original debe ser mayor que cero");
        }
        if (porcentajeDescuento < 0 || porcentajeDescuento >= 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
        BigDecimal precio = BigDecimal.valueOf(precioOriginal);
        BigDecimal factor = BigDecimal.valueOf(100 - porcentajeDescuento).divide(BigDecimal.valueOf(100));
        return precio.multiply(factor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
