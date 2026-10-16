package com.shop.ecommerce.admin.controller;

import com.shop.ecommerce.admin.dto.UserAdminDto;
import com.shop.ecommerce.admin.service.AdminUserService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin users")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminUserController {

    private final AdminUserService userService;

    public AdminUserController(AdminUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List all users")
    public List<UserAdminDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public UserAdminDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
