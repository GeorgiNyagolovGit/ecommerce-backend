package com.shop.ecommerce.cart.service;

import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.cart.dto.CartDto;
import com.shop.ecommerce.cart.entity.Cart;
import com.shop.ecommerce.cart.entity.CartItem;
import com.shop.ecommerce.cart.repository.CartRepository;
import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.product.entity.Product;
import com.shop.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        return toDto(cart);
    }

    public CartDto createCart(Long userId) {
        if (cartRepository.existsByUserId(userId)) {
            return getCart(userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        return toDto(cartRepository.save(cart));
    }

    public CartDto addItem(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("User not found"));

                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setItems(new ArrayList<>());

                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough product stock");
        }

        CartItem existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;

            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("Not enough product stock");
            }

            existingItem.setQuantity(newQuantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);

            cart.getItems().add(item);
        }

        return toDto(cartRepository.save(cart));
    }

    public CartDto updateItem(
            Long userId,
            Long productId,
            Integer quantity
    ) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItem item = cart.getItems()
                .stream()
                .filter(cartItem ->
                        cartItem.getProduct().getId().equals(productId)
                )
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (item.getProduct().getStock() < quantity) {
            throw new IllegalArgumentException("Not enough product stock");
        }

        item.setQuantity(quantity);

        return toDto(cartRepository.save(cart));
    }

    public CartDto removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItem item = cart.getItems()
                .stream()
                .filter(cartItem ->
                        cartItem.getProduct().getId().equals(productId)
                )
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        cart.getItems().remove(item);

        return toDto(cartRepository.save(cart));
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartDto toDto(Cart cart) {
        var items = cart.getItems()
                .stream()
                .map(item -> {
                    Product product = item.getProduct();

                    BigDecimal subtotal = product.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartDto.CartItemDto(
                            item.getId(),
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getImageUrl(),
                            item.getQuantity(),
                            subtotal
                    );
                })
                .toList();

        BigDecimal total = items.stream()
                .map(CartDto.CartItemDto::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDto(
                cart.getId(),
                cart.getUser().getId(),
                items,
                total
        );
    }
}
