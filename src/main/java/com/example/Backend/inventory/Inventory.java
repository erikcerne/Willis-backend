package com.example.Backend.inventory;

import com.example.Backend.products.Product;
import com.example.Backend.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID inventoryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "productID")
    private Product productID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "UserID")
    private User user;

    private int quantity;
    private LocalDateTime expiryDate;
    private LocalDateTime produceDate;
}
