package io.github.floste7.employee.backend.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SwaggerUiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenGetSwaggerUi_returnSwaggerUi() throws Exception {
        this.mockMvc.perform(get("/swagger-ui/index.html"))
                .andDo(print())
                .andExpect(header().stringValues("Content-Type","text/html"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void whenGetSwaggerConfig_returnSwaggerConfig() throws Exception {
        this.mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andDo(print())
                .andExpect(header().stringValues("Content-Type","application/json"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void whenGetApiDocs_returnApiDocs() throws Exception {
        this.mockMvc.perform(get("/v3/api-docs"))
                .andDo(print())
                .andExpect(header().stringValues("Content-Type","application/json"))
                .andExpect(status().isOk())
                .andReturn();
    }
}
