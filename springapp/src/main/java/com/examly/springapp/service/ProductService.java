package com.examly.springapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.examly.springapp.model.Product;
import com.examly.springapp.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product addProduct(Product product) {
        return repo.save(product);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product updateQuantity(Long id, Integer quantity) {
        Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if (quantity == null) {
            throw new RuntimeException("Quantity is required");
        }
        if (quantity < 0) {
            throw new RuntimeException("Quantity must be zero or positive");
        }
        product.setQuantity(quantity);
        return repo.save(product);
    }

    public void deleteProduct(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        repo.deleteById(id);
    }
}
