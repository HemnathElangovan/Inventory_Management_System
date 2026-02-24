package com.examly.springapp.controller;

import com.examly.springapp.model.Product;
import com.examly.springapp.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clean DB between tests
        productRepository.deleteAll();
    }

    @Test
    void controller_addProductSuccess() throws Exception {
        Product product = new Product();
        product.setName("Notebook");
        product.setDescription("A5 ruled");
        product.setQuantity(100);
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Notebook"))
            .andExpect(jsonPath("$.description").value("A5 ruled"))
            .andExpect(jsonPath("$.quantity").value(100));
    }

    @Test
    void exception_addProductValidationFailure() throws Exception {
        Product product = new Product();
        product.setName(""); // Missing required name
        product.setDescription("desc");
        product.setQuantity(10);
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Product name is required")));
    }

    @Test
    void controller_getAllProducts() throws Exception {
        Product p1 = new Product();
        p1.setName("Notebook"); p1.setDescription("A5 ruled"); p1.setQuantity(100);
        Product p2 = new Product();
        p2.setName("Pen"); p2.setDescription("Blue ink"); p2.setQuantity(250);
        productRepository.save(p1);
        productRepository.save(p2);
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", anyOf(is("Notebook"), is("Pen"))))
            .andExpect(jsonPath("$[1].name", anyOf(is("Notebook"), is("Pen"))));
    }

    @Test
    void controller_updateProductQuantitySuccess() throws Exception {
        Product product = new Product();
        product.setName("Marker");
        product.setDescription("Black");
        product.setQuantity(50);
        product = productRepository.save(product);

        mockMvc.perform(put("/products/" + product.getId() + "/quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":120}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(product.getId()))
            .andExpect(jsonPath("$.quantity").value(120));
    }

    @Test
    void exception_updateProductQuantityValidationFailure_negativeQuantity() throws Exception {
        Product product = new Product();
        product.setName("Marker");
        product.setDescription("Black");
        product.setQuantity(50);
        product = productRepository.save(product);
        mockMvc.perform(put("/products/"+product.getId()+"/quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":-10}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Quantity must be zero or positive")));
    }

    @Test
    void exception_updateProductQuantityNotFound() throws Exception {
        mockMvc.perform(put("/products/999/quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":5}"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", containsString("Product not found")));
    }

    // Additional Test Cases to reach 12 total

    @Test
    void controller_addProductWithNullQuantity() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        // quantity is null (not set)
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Quantity is required")));
    }

    @Test
    void controller_addProductWithExcessivelyLongName() throws Exception {
        Product product = new Product();
        product.setName("a".repeat(51)); // Exceeds 50 character limit
        product.setDescription("Test Description");
        product.setQuantity(10);
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Product name must not exceed 50 characters")));
    }

    @Test
    void controller_addProductWithExcessivelyLongDescription() throws Exception {
        Product product = new Product();
        product.setName("Valid Name");
        product.setDescription("a".repeat(201)); // Exceeds 200 character limit
        product.setQuantity(10);
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Description must not exceed 200 characters")));
    }

    @Test
    void controller_addProductWithZeroQuantity() throws Exception {
        Product product = new Product();
        product.setName("Zero Quantity Product");
        product.setDescription("Product with zero quantity");
        product.setQuantity(0);
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Zero Quantity Product"))
            .andExpect(jsonPath("$.quantity").value(0));
    }

    @Test
    void repository_getAllProductsWhenEmpty() throws Exception {
        // Ensure database is empty
        productRepository.deleteAll();
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void controller_updateProductQuantityWithMissingQuantityField() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setQuantity(10);
        product = productRepository.save(product);

        mockMvc.perform(put("/products/" + product.getId() + "/quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) // Empty JSON, missing quantity field
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Quantity is required")));
    }
}