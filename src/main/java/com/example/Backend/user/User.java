package com.example.Backend.user;

import com.example.Backend.inventory.Inventory;
import com.example.Backend.shoppingList.ShoppingList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ShoppingList> shoppingLists;


}