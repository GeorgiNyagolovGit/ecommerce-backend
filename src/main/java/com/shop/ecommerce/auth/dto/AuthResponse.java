package com.shop.ecommerce.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
    private Set<String> roles;
}