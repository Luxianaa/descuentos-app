package bo.edu.usfx.descuentos;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DescuentoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void calculaElPrecioFinal() throws Exception {
        mvc.perform(post("/api/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":200,\"porcentaje\":25}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioOriginal", is(200.0)))
                .andExpect(jsonPath("$.porcentaje", is(25.0)))
                .andExpect(jsonPath("$.precioFinal", is(150.0)));
    }

    @Test
    void responde400ConElMensajeDeLaRegla() throws Exception {
        mvc.perform(post("/api/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":0,\"porcentaje\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El precio original debe ser mayor que cero")));
    }

    @Test
    void responde400SiElPorcentajeEsInvalido() throws Exception {
        mvc.perform(post("/api/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":100,\"porcentaje\":101}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void infoIndicaAplicacionYCommit() throws Exception {
        mvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app", is("descuentos-api")))
                .andExpect(jsonPath("$.commit", is("desconocido")));
    }

    @Test
    void healthEstaArriba() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void corsPermiteElFrontEndLocalEnElPreflightDelPost() throws Exception {
        mvc.perform(options("/api/descuento")
                        .header("Origin", "http://localhost:4173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4173"));
    }

    @Test
    void corsRechazaUnOrigenDesconocido() throws Exception {
        mvc.perform(options("/api/descuento")
                        .header("Origin", "https://sitio-malicioso.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
