package com.example.Backend;
import com.example.Backend.dtos.ReceiveInventoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {

    UsersService usersService;
    InventoryService inventoryService;
    ProductService productService;

    public Controller(UsersService usersService, InventoryService inventoryService, ProductService productService) {
        this.usersService = usersService;
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<ReceiveInventoryDto> addCartToInventory(@RequestBody ReceiveInventoryDto dto){
        inventoryService.addCart(dto);
        return null;
    }



}
