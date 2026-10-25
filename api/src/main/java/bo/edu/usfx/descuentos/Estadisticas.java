package bo.edu.usfx.descuentos;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/** Contador de calculos atendidos: el dato "sensible" que protege el endpoint de administracion. */
@Component
public class Estadisticas {

    private final AtomicLong calculos = new AtomicLong();

    public void registrarCalculo() {
        calculos.incrementAndGet();
    }

    public long calculos() {
        return calculos.get();
    }
}
