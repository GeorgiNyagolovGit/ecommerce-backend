package com.shop.ecommerce.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.cart.controller.CartController;
import com.shop.ecommerce.cart.dto.CartDto;
import com.shop.ecommerce.cart.service.CartService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CartController(
                        cartService,
                        userRepository
                ))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        User user = new User();
        user.setId(7L);
        user.setEmail("ivan@example.com");
        lenient().when(userRepository.findByEmail("ivan@example.com"))
                .thenReturn(Optional.of(user));
    }

    @Test
    void getCartReturnsCurrentUsersCart() throws Exception {
        when(cartService.getCart(7L)).thenReturn(cart());

        mockMvc.perform(get("/api/cart")
                        .principal(() -> "ivan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.items[0].productName")
                        .value("Java Book"))
                .andExpect(jsonPath("$.total").value(99.80));
    }

    @Test
    void addItemReturnsCreatedCart() throws Exception {
        when(cartService.addItem(7L, 1L, 2)).thenReturn(cart());

        CartController.AddCartItemRequest request =
                new CartController.AddCartItemRequest(1L, 2);

        mockMvc.perform(post("/api/cart/items")
                        .principal(() -> "ivan@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(99.80));
    }

    @Test
    void addItemRejectsNonPositiveQuantity() throws Exception {
        CartController.AddCartItemRequest request =
                new CartController.AddCartItemRequest(1L, 0);

        mockMvc.perform(post("/api/cart/items")
                        .principal(() -> "ivan@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clearCartReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/cart/items")
                        .principal(() -> "ivan@example.com"))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart(7L);
    }

    private CartDto cart() {
        CartDto.CartItemDto item = new CartDto.CartItemDto(
                11L,
                1L,
                "Java Book",
                new BigDecimal("49.90"),
                null,
                2,
                new BigDecimal("99.80")
        );
        return new CartDto(
                3L,
                7L,
                List.of(item),
                new BigDecimal("99.80")
        );
    }
}
