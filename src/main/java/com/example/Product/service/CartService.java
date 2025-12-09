package com.example.Product.service;

import com.example.Product.dto.AddToCartRequest;
import com.example.Product.dto.CartItemResponse;
import com.example.Product.dto.CartResponse;
import com.example.Product.entity.CartItem;
import com.example.Product.entity.Product;
import com.example.Product.entity.User;
import com.example.Product.repository.CartItemRepository;
import com.example.Product.repository.ProductRepository;
import com.example.Product.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, 
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public CartResponse getCart(String username) {
        User user = getUserByUsername(username);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        List<CartItemResponse> items = cartItems.stream()
                .map(this::toCartItemResponse)
                .toList();
        return new CartResponse(items);
    }

    @Transactional
    public CartItemResponse addToCart(String username, AddToCartRequest request) {
        User user = getUserByUsername(username);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(user, product);

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Create new cart item
            cartItem = new CartItem(user, product, request.getQuantity());
        }

        cartItem = cartItemRepository.save(cartItem);
        return toCartItemResponse(cartItem);
    }

    @Transactional
    public CartItemResponse updateQuantity(String username, Long productId, Integer quantity) {
        User user = getUserByUsername(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Item not in cart"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        cartItem.setQuantity(quantity);
        cartItem = cartItemRepository.save(cartItem);
        return toCartItemResponse(cartItem);
    }

    @Transactional
    public void removeFromCart(String username, Long productId) {
        User user = getUserByUsername(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cartItemRepository.deleteByUserAndProduct(user, product);
    }

    @Transactional
    public void clearCart(String username) {
        User user = getUserByUsername(username);
        cartItemRepository.deleteByUser(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        return new CartItemResponse(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity()
        );
    }
}

