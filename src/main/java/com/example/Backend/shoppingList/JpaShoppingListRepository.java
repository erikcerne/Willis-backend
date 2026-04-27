package com.example.Backend.shoppingList;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaShoppingListRepository extends JpaRepository<ShoppingList, UUID> {
    List<ShoppingList> findAllByUser_UserId(String userId);

}
