package bo.edu.usfx.descuentos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CorsConfigTest {

    @Test
    void elComodinHaceFallarElArranque() {
        assertThrows(IllegalStateException.class, () -> new CorsConfig(new String[] {"*"}));
        assertThrows(IllegalStateException.class, () -> new CorsConfig(new String[] {"https://a.example", " * "}));
    }

    @Test
    void unaListaExplicitaEsValida() {
        assertDoesNotThrow(() -> new CorsConfig(new String[] {"https://app.vercel.app", "http://localhost:5173"}));
    }
}
