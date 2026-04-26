package com.example.Backend.products;

import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
public class ProductRepository {
    JpaProductRepository repo;

    public ProductRepository(JpaProductRepository repo) {
        this.repo = repo;
    }

    public Product findById(UUID id){
        return repo.findById(id).orElseThrow(()-> new NoSuchElementException("artiken hittades inte!"));
    }
}
