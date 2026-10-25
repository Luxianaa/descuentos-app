package bo.edu.usfx.descuentos;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Practica 10: los controles de seguridad de la API se prueban como cualquier otra regla de negocio. */
@SpringBootTest(properties = "app.admin-token=token-de-prueba-0123456789")
@AutoConfigureMockMvc
class SeguridadApiTest {

    private static final String TOKEN = "token-de-prueba-0123456789";

    @Autowired
    private MockMvc mvc;

    @Test
    void todasLasRespuestasLlevanLasCabecerasDeSeguridad() throws Exception {
        mvc.perform(get("/api/info"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'none'")))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("Strict-Transport-Security", containsString("max-age=")));
    }

    @Test
    void lasCabecerasTambienAparecenEnLosErrores() throws Exception {
        mvc.perform(get("/api/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void unCuerpoIlegibleDa400SinDetalleInterno() throws Exception {
        mvc.perform(post("/api/descuento").contentType(MediaType.APPLICATION_JSON).content("{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("La solicitud no es valida")))
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("bo.edu"))));
    }

    @Test
    void unaRutaInexistenteNoFiltraInformacion() throws Exception {
        mvc.perform(get("/api/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No encontrado")));
    }

    @Test
    void unMetodoNoPermitidoDa405ConMensajeGenerico() throws Exception {
        mvc.perform(get("/api/descuento"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(not(containsString("Request method"))));
    }

    @Test
    void rechazaUnPrecioEnormeYUnNumeroNoFinito() throws Exception {
        mvc.perform(post("/api/descuento").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":1e300,\"porcentaje\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El precio original no puede superar 1000000000")));
    }

    @Test
    void adminSinTokenDa401() throws Exception {
        mvc.perform(get("/api/admin/estadisticas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("No autorizado")));
    }

    @Test
    void adminConTokenIncorrectoDa401() throws Exception {
        mvc.perform(get("/api/admin/estadisticas").header("X-Admin-Token", "otro-token-0123456789"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminConTokenCorrectoCuentaLosCalculos() throws Exception {
        mvc.perform(post("/api/descuento").contentType(MediaType.APPLICATION_JSON)
                .content("{\"precio\":100,\"porcentaje\":10}"));
        mvc.perform(get("/api/admin/estadisticas").header("X-Admin-Token", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calculos").isNumber());
    }

    @Test
    void elActuatorSoloExponeHealthSinDetalles() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("diskSpace"))));
        mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
    }
}
