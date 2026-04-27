package com.example.Backend;

import com.example.Backend.dtos.InventoryDto;
import com.example.Backend.dtos.QuantityRequest;
import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.inventory.Inventory;
import com.example.Backend.shoppingList.ShoppingList;
import com.example.Backend.user.User;
import com.example.Backend.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class Controller {
    InventoryService inventoryService;

    UserService userService;

    public Controller(InventoryService inventoryService, UserService userService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public ResponseEntity<User> registerUser(@AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("https://willis-api/email");
        if (email == null) {
            email = jwt.getClaimAsString("email");
        }
        return ResponseEntity.ok(userService.registerIfNew(auth0Id, email));
    }

    @PostMapping("/inventory")
    public ResponseEntity<Void> syncCartToInventory(@RequestBody List<ReceiveInventoryDto> dto) {
        inventoryService.addCart(dto);
        return null;
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryDto>> getUserInventory(@AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();
        List<InventoryDto> inventoryDto = inventoryService.findAllForUserId(auth0Id);
        return ResponseEntity.ok(inventoryDto);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<Inventory> updateItemQuantity(@PathVariable UUID id, @RequestBody QuantityRequest request) {
        Inventory inventory = inventoryService.updateQuantity(id, request.quantity());
        return ResponseEntity.accepted().body(inventory);
    }


    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable UUID id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/inventory/expired")
    public ResponseEntity<Void> clearExpiredItems(String userId) {
        inventoryService.deleteGoneBadItems(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shopping")
    public ResponseEntity<Void> syncCartToInventory(UUID productId, String userId) {
        inventoryService.saveShoppingList(productId, userId);
        return null;
    }

    @DeleteMapping("/shopping/{id}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable UUID id) {
        inventoryService.deleteShoppingList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shopping")
    public ResponseEntity<List<ShoppingList>> getUserShoppingList(String id){
        List<ShoppingList> shoppingLists = inventoryService.getALlShoppingListByUserId(id);
        return ResponseEntity.ok(shoppingLists);
    }


}
