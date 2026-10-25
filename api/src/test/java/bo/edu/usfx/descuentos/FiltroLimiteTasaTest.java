package bo.edu.usfx.descuentos;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Ejercicio 1: Pruebas del filtro de limitacion de tasa.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FiltroLimiteTasaTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FiltroLimiteTasa filtro;

    @BeforeEach
    void setUp() {
        filtro.reset();
    }

    @Test
    void llamada61Responde429ConRetryAfter() throws Exception {
        for (int i = 1; i <= 60; i++) {
            mvc.perform(get("/api/info"))
                    .andExpect(status().isOk());
        }

        mvc.perform(get("/api/info"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andExpect(jsonPath("$.error", containsString("Demasiadas solicitudes")));
    }
}
