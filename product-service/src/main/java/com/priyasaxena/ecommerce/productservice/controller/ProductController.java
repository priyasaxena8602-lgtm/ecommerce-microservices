package com.priyasaxena.ecommerce.productservice.controller;

import com.priyasaxena.ecommerce.productservice.dto.ProductRequest;
import com.priyasaxena.ecommerce.productservice.dto.ProductResponse;
import com.priyasaxena.ecommerce.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest){
        ProductResponse response = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("productId") Long id, @Valid @RequestBody ProductRequest productRequest){
        ProductResponse response = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable("productId") Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
