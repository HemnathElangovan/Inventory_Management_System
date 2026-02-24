package com.examly.springapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examly.springapp.model.Product;
import com.examly.springapp.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        Product created = service.addProduct(product);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAllProducts();
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<Product> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        if (!body.containsKey("quantity")) {
            throw new RuntimeException("Quantity is required");
        }
        Integer quantity = body.get("quantity");
        Product updated = service.updateQuantity(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}