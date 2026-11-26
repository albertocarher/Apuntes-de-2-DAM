package ies.puesto.peliculas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogoTest {
    @Autowired MockMvc mvc;

    @Test
    void consultasDelCatalogo() throws Exception {
        for (String recurso : new String[]{"peliculas", "actores", "categorias"}) {
            mvc.perform(get("/api/orm/" + recurso))
                    .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        }
        mvc.perform(get("/api/orm/actores/PAS000001"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Alba Vega"));
        mvc.perform(get("/api/orm/categorias/1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Animación"));
        mvc.perform(get("/api/orm/peliculas/PEL001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PEL001"))
                .andExpect(jsonPath("$.titulo").value("El viaje de Chihiro"))
                .andExpect(jsonPath("$.anioEstreno").value(2001))
                .andExpect(jsonPath("$.duracionMinutos").value(125))
                .andExpect(jsonPath("$.reparto").isArray())
                .andExpect(jsonPath("$.reparto").isEmpty())
                .andExpect(jsonPath("$.categorias").isArray())
                .andExpect(jsonPath("$.categorias").isEmpty());
        mvc.perform(get("/api/orm/peliculas/PEL999")).andExpect(status().isNotFound());
    }
}
