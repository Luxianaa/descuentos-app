package bo.edu.usfx.descuentos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Compara el token de administracion en tiempo constante. El valor viene SOLO de la variable de entorno
 * ADMIN_TOKEN: si no esta definida (o es muy corta) el endpoint de administracion queda deshabilitado.
 */
@Component
public class VerificadorToken {

    static final int LONGITUD_MINIMA = 16;

    private final byte[] esperado;

    public VerificadorToken(@Value("${app.admin-token:}") String token) {
        this.esperado = token.length() >= LONGITUD_MINIMA ? token.getBytes(StandardCharsets.UTF_8) : null;
    }

    public boolean habilitado() {
        return esperado != null;
    }

    public boolean autoriza(String recibido) {
        if (esperado == null || recibido == null) {
            return false;
        }
        return MessageDigest.isEqual(esperado, recibido.getBytes(StandardCharsets.UTF_8));
    }
}
