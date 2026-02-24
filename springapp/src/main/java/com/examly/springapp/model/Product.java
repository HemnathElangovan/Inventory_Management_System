package com.examly.springapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 50, message = "Product name must not exceed 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be zero or positive")
    private Integer quantity;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
