package com.example.Product.controller;

import com.example.Product.dto.AddToWishList;
import com.example.Product.entity.Product;
import com.example.Product.service.WishListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishListController {

    private final WishListService wishlistService;

    public WishListController(WishListService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/{userId}")
    public Set<Product> getWishList(@PathVariable Long userId) {

        return wishlistService.getWishList(userId);
    }

    @PostMapping()
    public ResponseEntity<String> addToWishList(@RequestBody AddToWishList addToWishList) {
        wishlistService.addToWishList(addToWishList.getUserId(), addToWishList.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Added to wishlist");
    }

    @DeleteMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<String> removeFromWishList(@PathVariable Long userId, @PathVariable Long productId) {
        wishlistService.removeFromWishList(userId, productId);
        return ResponseEntity.status(HttpStatus.OK).body("Removed from wishlist");
    }

}
