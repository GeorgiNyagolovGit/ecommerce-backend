package com.shop.ecommerce.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.ecommerce.admin.controller.AdminProductController;
import com.shop.ecommerce.admin.dto.AdminProductDto;
import com.shop.ecommerce.admin.dto.AdminProductRequest;
import com.shop.ecommerce.admin.service.AdminProductService;
import com.shop.ecommerce.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminProductControllerTest {

    @Mock
    private AdminProductService productService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createProductReturnsCreated() throws Exception {
        AdminProductRequest request = validRequest();
        when(productService.createProduct(any(AdminProductRequest.class)))
                .thenReturn(new AdminProductDto(
                        1L,
                        request.name(),
                        request.description(),
                        request.price(),
                        request.stock(),
                        request.imageUrl(),
                        true,
                        1L,
                        "Books",
                        null,
                        null
                ));

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Java Programming Book"));
    }

    @Test
    void createProductRejectsInvalidPriceAndStock() throws Exception {
        AdminProductRequest request = new AdminProductRequest(
                "Java Programming Book",
                "A practical Java guide",
                BigDecimal.ZERO,
                -1,
                null,
                1L,
                true
        );

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProductDeactivatesIt() throws Exception {
        mockMvc.perform(delete("/api/admin/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deactivateProduct(1L);
    }

    private AdminProductRequest validRequest() {
        return new AdminProductRequest(
                "Java Programming Book",
                "A practical Java guide",
                new BigDecimal("49.90"),
                25,
                "https://example.com/java-book.jpg",
                1L,
                true
        );
    }
}
