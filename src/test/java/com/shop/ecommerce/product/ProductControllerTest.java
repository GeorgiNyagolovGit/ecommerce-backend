package com.shop.ecommerce.product;

import com.shop.ecommerce.common.exception.GlobalExceptionHandler;
import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.product.controller.ProductController;
import com.shop.ecommerce.product.dto.ProductDto;
import com.shop.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllProductsReturnsCatalog() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(product(1L, "Java Book")));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Book"))
                .andExpect(jsonPath("$[0].price").value(49.90));
    }

    @Test
    void getUnknownProductReturnsNotFound() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void searchPassesKeywordToService() throws Exception {
        when(productService.searchProducts("java"))
                .thenReturn(List.of(product(1L, "Java Book")));

        mockMvc.perform(get("/api/products/search")
                        .queryParam("keyword", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java Book"));

        verify(productService).searchProducts("java");
    }

    private ProductDto product(Long id, String name) {
        return new ProductDto(
                id,
                name,
                "Programming book",
                new BigDecimal("49.90"),
                10,
                null,
                1L,
                "Books",
                null
        );
    }
}
