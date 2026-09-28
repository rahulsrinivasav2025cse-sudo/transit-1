package com.example.transit.repository;

import com.example.transit.entity.Product;
import com.example.transit.entity.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findFirstByCategory(ProductCategory category);
}
