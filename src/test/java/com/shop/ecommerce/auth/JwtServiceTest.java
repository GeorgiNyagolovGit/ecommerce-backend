package com.shop.ecommerce.auth;

import com.shop.ecommerce.auth.security.JwtService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    @Test
    void acceptsBase64EncodedSecret() {
        String secret = Base64.getEncoder().encodeToString(
                "a-secure-secret-with-at-least-32-bytes".getBytes(StandardCharsets.UTF_8)
        );
        JwtService jwtService = new JwtService(secret, 60_000);

        String token = jwtService.generateToken("user@example.com");

        assertEquals("user@example.com", jwtService.extractUsername(token));
    }

    @Test
    void acceptsRenderGeneratedPlainTextSecret() {
        JwtService jwtService = new JwtService(
                "render-generated-random-secret-1234567890",
                60_000
        );

        String token = jwtService.generateToken("user@example.com");

        assertEquals("user@example.com", jwtService.extractUsername(token));
    }

    @Test
    void rejectsShortSecret() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService("too-short", 60_000)
        );
    }
}
