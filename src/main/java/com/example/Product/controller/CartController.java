package com.example.Product.controller;

import com.example.Product.dto.AddToCartRequest;
import com.example.Product.dto.CartItemResponse;
import com.example.Product.dto.CartResponse;
import com.example.Product.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        CartResponse cart = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addToCart(
            Authentication authentication,
            @RequestBody AddToCartRequest request) {
        CartItemResponse item = cartService.addToCart(authentication.getName(), request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            Authentication authentication,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        CartItemResponse item = cartService.updateQuantity(authentication.getName(), productId, quantity);
        if (item == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            Authentication authentication,
            @PathVariable Long productId) {
        cartService.removeFromCart(authentication.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}

