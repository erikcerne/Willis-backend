package com.example.Backend.products;

import jakarta.persistence.*;
import lombok.Data;

import java.net.URL;
import java.util.UUID;

@Entity
@Data
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID ProductID;
    String Name;
    String Category;
    URL Pic;
}
