package com.priyasaxena.ecommerce.productservice.service;

import com.priyasaxena.ecommerce.productservice.dto.ProductRequest;
import com.priyasaxena.ecommerce.productservice.dto.ProductResponse;
import com.priyasaxena.ecommerce.productservice.exception.ProductNotFoundException;
import com.priyasaxena.ecommerce.productservice.model.Product;
import com.priyasaxena.ecommerce.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ProductResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request){
        Product product = mapToProduct(request);
        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request){
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        existingProduct.setProductName(request.getProductName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStockQuantity(request.getStockQuantity());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setSku(request.getSku());
        return mapToResponse(productRepository.save(existingProduct));
    }

    public void deleteProduct(Long id){
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(existingProduct);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory(),
                product.getSku(),
                product.getCreatedAt()
        );
    }

    private Product mapToProduct(ProductRequest request) {
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setSku(request.getSku());
        return product;
    }
}
