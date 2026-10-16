package com.shop.ecommerce.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCanAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void customerCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .with(user("customer@example.com")
                                .roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }
}
