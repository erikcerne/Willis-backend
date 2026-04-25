package com.example.Backend;

import com.example.Backend.dtos.InventoryDto;
import com.example.Backend.dtos.QuantityRequest;
import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.inventory.Inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {
    InventoryService inventoryService;

    public Controller(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping()
    public ResponseEntity<Void> addCartToInventory(@RequestBody List<ReceiveInventoryDto> dto) {
        inventoryService.addCart(dto);
        return null;
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<List<InventoryDto>> getAllForUserId(@PathVariable UUID id){
        List<InventoryDto> inventoryDtos = inventoryService.findAllForUserId(id);
        return ResponseEntity.ok(inventoryDtos);
    }

    @DeleteMapping("/deleteAll/{userId}")
    public ResponseEntity<Void> deleteAllGoneBadItems(@PathVariable UUID userId){
        inventoryService.deleteGoneBadItems(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteInventoryById(@PathVariable UUID id){
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Inventory> updateQuantityById(@PathVariable UUID id, @RequestBody QuantityRequest request){
        Inventory inventory = inventoryService.updateQuantity(id, request.quantity());
        return ResponseEntity.accepted().body(inventory);
    }

}
