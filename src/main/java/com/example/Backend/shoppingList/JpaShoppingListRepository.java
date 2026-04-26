package com.example.Backend.shoppingList;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaShoppingListRepository extends JpaRepository<ShoppingList, UUID> {
}
