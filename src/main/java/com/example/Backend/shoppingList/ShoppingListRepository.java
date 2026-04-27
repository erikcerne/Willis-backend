package com.example.Backend.shoppingList;
import com.example.Backend.inventory.Inventory;
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
        if(jpa.findByProduct_ProductId(shoppingList.getProduct().getProductId()) == null){
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
