package com.example.Backend;

import com.example.Backend.dtos.InventoryDto;
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
    @GetMapping("/all")
    public ResponseEntity<List<Inventory>> getAll(){
        List<Inventory> inventories = inventoryService.findAll();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<List<InventoryDto>> getAllForUserId(@PathVariable UUID id){
        List<InventoryDto> inventoryDtos = inventoryService.findAllForUserId(id);
        return ResponseEntity.ok(inventoryDtos);
    }

}
