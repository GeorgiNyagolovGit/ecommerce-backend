package com.shop.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDocumentContainsMainEndpointsAndJwtSecurity() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['paths']['/api/auth/register']").exists())
                .andExpect(jsonPath("$['paths']['/api/auth/login']").exists())
                .andExpect(jsonPath("$['paths']['/api/products']").exists())
                .andExpect(jsonPath("$['paths']['/api/categories']").exists())
                .andExpect(jsonPath("$['paths']['/api/orders']").exists())
                .andExpect(jsonPath("$['paths']['/api/payments']").exists())
                .andExpect(jsonPath("$['paths']['/api/cart']").exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/cart/items']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/cart/items/{productId}']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/admin/dashboard']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/admin/products']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/admin/categories']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/admin/orders']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/admin/payments']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/admin/users']"
                ).exists())
                .andExpect(jsonPath(
                        "$['components']['securitySchemes']['bearerAuth']"
                                + "['scheme']"
                ).value("bearer"));
    }
}
