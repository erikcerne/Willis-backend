package com.example.Backend.user;

import com.example.Backend.inventory.Inventory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userID;
    private String name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    public User() {
    }

    public User(UUID userID, String name, List<Inventory> inventories) {
        this.userID = userID;
        this.name = name;
        this.inventories = inventories;
    }
}
