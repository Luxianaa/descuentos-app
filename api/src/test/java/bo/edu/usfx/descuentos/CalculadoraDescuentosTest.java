package bo.edu.usfx.descuentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculadoraDescuentosTest {

    private final CalculadoraDescuentos calculadora = new CalculadoraDescuentos();

    @ParameterizedTest(name = "{0} con {1} % -> {2}")
    @CsvSource({
        "100.00, 10, 90.00",
        "200.00, 25, 150.00",
        "50.00, 50, 25.00",
        "33.33, 10, 30.00",
        "19.99, 15, 16.99"
    })
    void aplicaElDescuentoYRedondeaADosDecimales(double precio, double porcentaje, double esperado) {
        assertEquals(esperado, calculadora.calcularPrecioFinal(precio, porcentaje), 0.0001);
    }

    @Test
    void descuentoCeroDejaElPrecioIgual() {
        assertEquals(80.0, calculadora.calcularPrecioFinal(80.0, 0), 0.0001);
    }

    @Test
    void descuentoTotalDejaElPrecioEnCero() {
        assertEquals(0.0, calculadora.calcularPrecioFinal(80.0, 100), 0.0001);
    }

    @ParameterizedTest(name = "porcentaje {0} es invalido")
    @CsvSource({"-1", "100.01", "150"})
    void rechazaPorcentajesFueraDeRango(double porcentaje) {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcularPrecioFinal(100, porcentaje));
    }

    @ParameterizedTest(name = "precio {0} es invalido")
    @CsvSource({"0", "-5", "-0.01"})
    void rechazaPreciosNoPositivos(double precio) {
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> calculadora.calcularPrecioFinal(precio, 10));
        assertEquals("El precio original debe ser mayor que cero", e.getMessage());
    }
}
