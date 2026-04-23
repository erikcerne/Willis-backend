package com.example.Backend.inventory;

import com.example.Backend.products.Product;
import com.example.Backend.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID inventoryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Users_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Users users;

    private int quantity;
    private LocalDateTime expiryDate;
    private LocalDateTime produceDate;
}
