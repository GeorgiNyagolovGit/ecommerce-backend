package com.shop.ecommerce.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Ivan")
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Ivanov")
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 150)
    @Schema(example = "ivan@example.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Schema(example = "password123")
    private String password;
}
