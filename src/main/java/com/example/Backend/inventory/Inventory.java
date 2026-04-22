package com.example.Backend.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID InventoryId;
    UUID ProductID;
    int Quantity;
    LocalDateTime ExpiryDate;
    LocalDateTime ProduceDate;
}
