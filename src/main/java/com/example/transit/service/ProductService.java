package com.example.transit.service;

import com.example.transit.entity.Product;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    @Transactional
    public Product updateProduct(Long id, Product updated) {
        Product product = getProductById(id);
        product.setName(updated.getName());
        product.setType(updated.getType());
        product.setCategory(updated.getCategory());
        product.setUnitPrice(updated.getUnitPrice());
        product.setLinkedAccount(updated.getLinkedAccount());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(getProductById(id));
    }
}
