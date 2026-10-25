package bo.edu.usfx.descuentos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VerificadorTokenTest {

    @Test
    void aceptaSoloElTokenExacto() {
        VerificadorToken v = new VerificadorToken("token-de-prueba-0123456789");
        assertTrue(v.habilitado());
        assertTrue(v.autoriza("token-de-prueba-0123456789"));
        assertFalse(v.autoriza("token-de-prueba-012345678"));
        assertFalse(v.autoriza("TOKEN-DE-PRUEBA-0123456789"));
        assertFalse(v.autoriza(""));
        assertFalse(v.autoriza(null));
    }

    @Test
    void sinTokenOCortoQuedaDeshabilitado() {
        assertFalse(new VerificadorToken("").habilitado());
        assertFalse(new VerificadorToken("corto").habilitado());
        assertFalse(new VerificadorToken("").autoriza(""));
        assertFalse(new VerificadorToken("corto").autoriza("corto"));
    }
}
