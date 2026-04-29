package com.example.Backend.shoppingList;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ShoppingListRepository {
    JpaShoppingListRepository jpa;

    public ShoppingListRepository(JpaShoppingListRepository jpa) {
        this.jpa = jpa;
    }

    public void save(ShoppingList shoppingList) {
        // Kontrollera nu både productId och userId
        UUID productId = shoppingList.getProduct().getProductId();
        String userId = shoppingList.getUser().getUserId();

        if(jpa.findByProduct_ProductIdAndUser_UserId(productId, userId) == null){
            jpa.save(shoppingList);
        }
    }

    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    public List<ShoppingList> getALlShoppingListByUserId(String id) {
        return jpa.findAllByUser_UserId(id);
    }
}