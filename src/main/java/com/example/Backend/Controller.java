package com.example.Backend;

import com.example.Backend.dtos.InventoryDto;
import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.dtos.ShoppingListDto;
import com.example.Backend.inventory.Inventory;

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
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryDto>> getUserInventory(@AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();
        List<InventoryDto> inventoryDto = inventoryService.findAllForUserId(auth0Id);
        return ResponseEntity.ok(inventoryDto);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<Inventory> updateItemQuantity(@PathVariable UUID id, @RequestBody int quantity) {
        Inventory inventory = inventoryService.updateQuantity(id, quantity);
        return ResponseEntity.accepted().body(inventory);
    }


    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable UUID id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/inventory/expired")
    public ResponseEntity<Void> clearExpiredItems(@AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();
        inventoryService.deleteGoneBadItems(auth0Id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shopping")
    public ResponseEntity<Void> syncCartToInventory(@AuthenticationPrincipal Jwt jwt,@RequestParam UUID inventoryId) {
        String auth0Id = jwt.getSubject();
        inventoryService.saveToShoppingList(inventoryId, auth0Id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/shopping")
    public ResponseEntity<Void> deleteShoppingList(UUID id) {
        inventoryService.deleteShoppingList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shopping")
    public ResponseEntity<List<ShoppingListDto>> getUserShoppingList(@AuthenticationPrincipal Jwt jwt){
        String auth0Id = jwt.getSubject();
        List<ShoppingListDto> shoppingListDto = inventoryService.getALlShoppingListDtoByUserId(auth0Id);
        return ResponseEntity.ok(shoppingListDto);
    }

}
