package com.shop.ecommerce.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.ecommerce.auth.controller.AuthController;
import com.shop.ecommerce.auth.dto.AuthResponse;
import com.shop.ecommerce.auth.dto.LoginRequest;
import com.shop.ecommerce.auth.dto.RegisterRequest;
import com.shop.ecommerce.auth.service.AuthService;
import com.shop.ecommerce.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerReturnsCreatedForValidRequest() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse(
                        1L,
                        "Ivan",
                        "Ivanov",
                        "ivan@example.com",
                        "jwt-token",
                        Set.of("ROLE_CUSTOMER")
                ));

        RegisterRequest request = registrationRequest(
                "Ivan",
                "Ivanov",
                "ivan@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ivan@example.com"))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_CUSTOMER"));
    }

    @Test
    void registerRejectsInvalidEmailAndShortPassword() throws Exception {
        RegisterRequest request = registrationRequest(
                "Ivan",
                "Ivanov",
                "not-an-email",
                "123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerRejectsDuplicateEmail() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException(
                        "Email is already registered"
                ));

        RegisterRequest request = registrationRequest(
                "Ivan",
                "Ivanov",
                "ivan@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Email is already registered"));
    }

    @Test
    void loginReturnsTokenForValidCredentials() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse(
                        1L,
                        "Ivan",
                        "Ivanov",
                        "ivan@example.com",
                        "jwt-token",
                        Set.of("ROLE_CUSTOMER")
                ));

        LoginRequest request = new LoginRequest();
        request.setEmail("ivan@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    private RegisterRequest registrationRequest(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}
