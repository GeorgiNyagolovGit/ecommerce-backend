package com.shop.ecommerce.cart.controller;

import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.cart.dto.CartDto;
import com.shop.ecommerce.cart.service.CartService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import com.shop.ecommerce.common.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Authenticated customer's shopping cart")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(
            CartService cartService,
            UserRepository userRepository
    ) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get the current user's cart")
    public CartDto getCart(Principal principal) {
        return cartService.getCart(currentUserId(principal));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create the current user's cart")
    public CartDto createCart(Principal principal) {
        return cartService.createCart(currentUserId(principal));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a product to the cart")
    public CartDto addItem(
            Principal principal,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItem(
                currentUserId(principal),
                request.productId(),
                request.quantity()
        );
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Set a product's quantity in the cart")
    public CartDto updateItem(
            Principal principal,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(
                currentUserId(principal),
                productId,
                request.quantity()
        );
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove a product from the cart")
    public CartDto removeItem(
            Principal principal,
            @PathVariable Long productId
    ) {
        return cartService.removeItem(
                currentUserId(principal),
                productId
        );
    }

    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove all products from the cart")
    public void clearCart(Principal principal) {
        cartService.clearCart(currentUserId(principal));
    }

    private Long currentUserId(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return user.getId();
    }

    public record AddCartItemRequest(
            @NotNull
            @Schema(example = "1")
            Long productId,

            @NotNull
            @Min(1)
            @Schema(example = "2", minimum = "1")
            Integer quantity
    ) {
    }

    public record UpdateCartItemRequest(
            @NotNull
            @Min(1)
            @Schema(example = "3", minimum = "1")
            Integer quantity
    ) {
    }
}
