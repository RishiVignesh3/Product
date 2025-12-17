package com.example.Product.service;

import com.example.Product.entity.Product;
import com.example.Product.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products", key = "'all-' + (#sortBy ?: 'name')")
    public List<Product> getAllProducts(String sortBy) {
        Sort sort = switch (sortBy) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "stock" -> Sort.by(Sort.Direction.ASC, "stockQuantity");
            default -> Sort.by(Sort.Direction.ASC, "name");
        };
        return productRepository.findAll(sort);
    }

    @Cacheable(value = "product", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @CachePut(value = "product", key = "#result.id")
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @CachePut(value = "product", key = "#product.id")
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}

