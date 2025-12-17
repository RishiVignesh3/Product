package com.example.Product.service;

import com.example.Product.entity.User;
import com.example.Product.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @CacheEvict(value = "users", key = "#user.username")
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @CacheEvict(value = "users", key = "#username")
    public void evictUserCache(String username) {
        // Method to manually evict user from cache when needed
    }
}

