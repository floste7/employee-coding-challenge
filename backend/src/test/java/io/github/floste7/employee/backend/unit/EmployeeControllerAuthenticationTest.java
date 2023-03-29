package io.github.floste7.employee.backend.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenGetRequestsReceived_noAuthenticationRequired() throws Exception {
        this.mockMvc.perform(get("/api/v1/employee"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void whenPostEmployee_authenticationIsRequired() throws Exception {
        this.mockMvc.perform(post("/api/v1/employee"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void whenPutEmployee_authenticationIsRequired() throws Exception {
        this.mockMvc.perform(put("/api/v1/employee"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void whenDeleteEmployee_authenticationIsRequired() throws Exception {
        this.mockMvc.perform(delete("/api/v1/employee"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
