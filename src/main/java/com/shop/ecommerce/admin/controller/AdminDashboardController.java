package com.shop.ecommerce.admin.controller;

import com.shop.ecommerce.admin.dto.DashboardStatsDto;
import com.shop.ecommerce.admin.service.AdminDashboardService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "Admin dashboard")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Get store statistics")
    public DashboardStatsDto getStats() {
        return dashboardService.getStats();
    }
}
