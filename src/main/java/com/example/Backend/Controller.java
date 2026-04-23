package com.example.Backend;
import com.example.Backend.dtos.ReceiveInventoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {


    InventoryService inventoryService;

    public Controller(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PostMapping()
    public ResponseEntity<ReceiveInventoryDto> addCartToInventory(@RequestBody ReceiveInventoryDto dto){
        inventoryService.addCart(dto);
        return null;
    }



}
