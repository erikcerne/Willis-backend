package com.example.Backend.user;

import com.example.Backend.inventory.Inventory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID UserID;
    String Name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    public User(){
    }

    public User(UUID userID, String name, List<Inventory> inventories) {
        UserID = userID;
        Name = name;
        this.inventories = inventories;
    }
}
