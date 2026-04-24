package com.example.Backend;

import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.inventory.Inventory;
import com.example.Backend.inventory.InventoryRepository;
import com.example.Backend.products.Product;
import com.example.Backend.products.ProductRepository;
import com.example.Backend.user.UserRepository;
import com.example.Backend.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class InventoryService {

    InventoryRepository inventoryRepo;
    UserRepository userRepo;
    ProductRepository productRepo;

    public InventoryService(InventoryRepository inventoryRepo, UserRepository userRepo, ProductRepository productRepo) {
        this.inventoryRepo = inventoryRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public void addCart(List<ReceiveInventoryDto> dto) {
        List<Inventory> data = new ArrayList<>(dto.stream().map(i -> new Inventory(
                null,
                findProductById(i.productId()),
                findUserByID(i.UserId()),
                i.Quantity(),
                i.expiryDate(),
                i.produceDate()
        )).toList());

        List<Inventory> cleanData = checkIfExistingInDb(data);
        inventoryRepo.saveAll(cleanData);
    }

    private List<Inventory> checkIfExistingInDb(List<Inventory> data) {

        UUID userId = data.get(0).getUser().getUserId();

        List<Inventory> existingData = inventoryRepo.getALlInventoryByUserId(userId);

        List<Inventory> newData = new ArrayList<>();

        for (Inventory inventory : data) {
            Boolean wasFound = false;
            for (Inventory existingInventory : existingData) {
                if (equals(inventory, existingInventory)) {
                    updateQuantity(existingInventory, inventory.getQuantity());
                    wasFound = true;
                    break;
                }
            }
            if (!wasFound) {
                newData.add(inventory);
            }
        }
        return newData;
    }

    private Boolean equals(Inventory a, Inventory b) {
        return Objects.equals(a.getExpiryDate(), b.getExpiryDate()) &&
                Objects.equals(a.getProduceDate(), b.getProduceDate()) &&
                a.getProduct().getProductId().equals(b.getProduct().getProductId());
    }

    protected void updateQuantity(Inventory inventory, int amount) {
        int newAmount = inventory.getQuantity() + amount;
        inventory.setQuantity(newAmount);
        inventoryRepo.save(inventory);
    }

    public User findUserByID(UUID id) {
        return userRepo.getUserById(id);
    }

    public Product findProductById(UUID id) {
        return productRepo.getProductById(id);
    }

    public List<Inventory> findAll() {
        return inventoryRepo.findAll();
    }

}
