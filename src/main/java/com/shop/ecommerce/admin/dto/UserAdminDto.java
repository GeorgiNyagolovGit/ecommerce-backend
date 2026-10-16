package com.shop.ecommerce.admin.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserAdminDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        LocalDateTime createdAt
) {
}
