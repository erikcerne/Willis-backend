package com.example.Backend.products;

import jakarta.persistence.*;
import lombok.Data;

import java.net.URL;
import java.util.UUID;

@Entity
@Data
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productID;
    private String name;
    private String category;
    private URL pic;

    public Product() {
    }

    public Product(UUID productID, String name, String category, URL pic) {
        this.productID = productID;
        this.name = name;
        this.category = category;
        this.pic = pic;
    }
}
