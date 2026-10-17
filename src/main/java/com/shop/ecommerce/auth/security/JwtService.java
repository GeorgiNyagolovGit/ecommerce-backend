package com.shop.ecommerce.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(resolveKeyBytes(secret));
        this.expiration = expiration;
    }

    private static byte[] resolveKeyBytes(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT_SECRET must be configured");
        }

        String value = secret.trim();
        byte[] rawBytes = value.getBytes(StandardCharsets.UTF_8);

        try {
            byte[] decodedBytes = Decoders.BASE64.decode(value);
            if (decodedBytes.length >= 32) {
                return decodedBytes;
            }
        } catch (DecodingException ignored) {
            // Render-generated secrets can be secure random text instead of Base64.
        }

        if (rawBytes.length >= 32) {
            return rawBytes;
        }

        throw new IllegalArgumentException(
                "JWT_SECRET must be Base64-encoded with at least 32 decoded bytes " +
                        "or plain text with at least 32 UTF-8 bytes"
        );
    }

    public String generateToken(String email) {
        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        Claims claims = extractClaims(token);

        return email.equals(claims.getSubject())
                && claims.getExpiration().after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
