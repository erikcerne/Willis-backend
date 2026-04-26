package com.example.Backend.shoppingList;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ShoppingListRepository {
    JpaShoppingListRepository jpa;

    public ShoppingListRepository(JpaShoppingListRepository jpa) {
        this.jpa = jpa;
    }

    public void save(ShoppingList shoppingList) {
        jpa.save(shoppingList);
    }

    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

}
