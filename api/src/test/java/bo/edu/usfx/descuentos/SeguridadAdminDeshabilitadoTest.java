package bo.edu.usfx.descuentos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** Sin ADMIN_TOKEN (o con un valor corto) el endpoint de administracion no se puede usar: falla cerrado. */
@SpringBootTest(properties = "app.admin-token=corto")
@AutoConfigureMockMvc
class SeguridadAdminDeshabilitadoTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void sinTokenConfiguradoDa403AunqueSeEnvieUno() throws Exception {
        mvc.perform(get("/api/admin/estadisticas").header("X-Admin-Token", "corto")).andExpect(status().isForbidden());
    }
}
