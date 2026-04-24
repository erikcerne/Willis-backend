package com.example.Backend;

import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.inventory.Inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
