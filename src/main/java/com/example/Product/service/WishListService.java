package com.example.Product.service;

import com.example.Product.entity.Product;
import com.example.Product.entity.User;
import com.example.Product.repository.ProductRepository;
import com.example.Product.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WishListService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishListService(UserRepository userRepository, ProductRepository productRepository){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Set<Product> getWishList(Long userId) {
        return userRepository.findById(userId).map(User::getWishList).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public Product addToWishList(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not Found"));
        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Product not Found"));

        user.getWishList().add(product);
        userRepository.save(user);
        return product;
    }

    public Product removeFromWishList(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not Found"));
        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Product not Found"));

        user.getWishList().remove(product);
        userRepository.save(user);
        return product;
    }

}
