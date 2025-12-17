package com.example.Product.service;

import com.example.Product.entity.Product;
import com.example.Product.entity.User;
import com.example.Product.repository.ProductRepository;
import com.example.Product.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class WishListService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishListService(UserRepository userRepository, ProductRepository productRepository){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Cacheable(value = "wishlist", key = "#userId")
    @Transactional(readOnly = true)
    public Set<Product> getWishList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        // Force initialization of lazy collection before caching
        user.getWishList().size();
        return user.getWishList();
    }

    @CacheEvict(value = "wishlist", key = "#userId")
    @Transactional
    public Product addToWishList(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not Found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not Found"));

        user.getWishList().add(product);
        userRepository.save(user);
        return product;
    }

    @CacheEvict(value = "wishlist", key = "#userId")
    @Transactional
    public Product removeFromWishList(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not Found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not Found"));

        user.getWishList().remove(product);
        userRepository.save(user);
        return product;
    }
}
