package com.example.Backend;

import com.example.Backend.dtos.InventoryDto;
import com.example.Backend.dtos.InventoryListDto;
import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.dtos.ShoppingListDto;
import com.example.Backend.inventory.Inventory;
import com.example.Backend.inventory.InventoryRepository;
import com.example.Backend.products.Product;
import com.example.Backend.products.ProductRepository;
import com.example.Backend.shoppingList.ShoppingList;
import com.example.Backend.shoppingList.ShoppingListRepository;
import com.example.Backend.user.UserRepository;
import com.example.Backend.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class InventoryService {

    InventoryRepository inventoryRepo;
    UserRepository userRepo;
    ProductRepository productRepo;

    ShoppingListRepository shoppingListRepo;

    public InventoryService(InventoryRepository inventoryRepo, UserRepository userRepo, ProductRepository productRepo, ShoppingListRepository shoppingListRepo) {
        this.inventoryRepo = inventoryRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.shoppingListRepo = shoppingListRepo;
    }

    //add

    @Transactional
    public void addCart(List<ReceiveInventoryDto> dto) {
        List<Inventory> data = new ArrayList<>(dto.stream().
                map(i -> new Inventory(null, findProductById(i.productId()),
                        findUserByID(i.userId()), i.quantity(), i.expiryDate(), i.produceDate())).toList());

        List<Inventory> cleanData = checkIfExistingInDb(data);
        inventoryRepo.saveAll(cleanData);
    }

    private List<Inventory> checkIfExistingInDb(List<Inventory> data) {

        String userId = data.get(0).getUser().getUserId();

        List<Inventory> existingData = inventoryRepo.getALlInventoryByUserId(userId);

        List<Inventory> newData = new ArrayList<>();

        for (Inventory inventory : data) {
            boolean wasFound = false;
            for (Inventory existingInventory : existingData) {
                if (equals(inventory, existingInventory)) {
                    updateQuantityUpp(existingInventory, inventory.getQuantity());
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

    protected void updateQuantityUpp(Inventory inventory, int amount) {
        int newAmount = inventory.getQuantity() + amount;
        inventory.setQuantity(newAmount);
        inventoryRepo.save(inventory);
    }

    public User findUserByID(String id) {
        return userRepo.findById(id);
    }

    public Product findProductById(UUID id) {
        return productRepo.findById(id);
    }

    //get

    public List<InventoryDto> findAllForUserId(String id) {

        List<Inventory> inventoriesForUser = inventoryRepo.getALlInventoryByUserId(id);
        List<InventoryDto> inventoryDos = new ArrayList<>();

        while (!inventoriesForUser.isEmpty()) {
            Inventory newInventory = inventoriesForUser.remove(0);

            List<Inventory> tempInventory = inventoriesForUser.stream()
                    .filter(inventory -> inventory.getProduct().getProductId()
                            .equals(newInventory.getProduct().getProductId()))
                    .toList();

            inventoriesForUser.removeAll(tempInventory);
            List<InventoryListDto> combinedSpecs = new ArrayList<>();
            combinedSpecs.add(mapToInventoryListDto(newInventory));
            combinedSpecs.addAll(tempInventory.stream().map(this::mapToInventoryListDto).toList());
            InventoryDto tempDto = mapToInventoryDto(combinedSpecs, newInventory);
            inventoryDos.add(tempDto);
        }
        return inventoryDos;

    }

    private InventoryDto mapToInventoryDto(List<InventoryListDto> listDto, Inventory inventory) {
        return new InventoryDto(
                inventory.getProduct().getName(),
                inventory.getProduct().getCategory(),
                listDto,
                inventory.getProduct().getPic());
    }

    private InventoryListDto mapToInventoryListDto(Inventory inventory) {
        double expiryProgress = calculateExpiryProgress(inventory.getProduceDate(), inventory.getExpiryDate());
        int daysLeft = calculateDaysLeft(inventory.getExpiryDate());
        return new InventoryListDto(inventory.getExpiryDate(),
                expiryProgress,
                inventory.getQuantity(),
                daysLeft,
                inventory.getInventoryId());
    }

    private double calculateExpiryProgress(LocalDateTime produceDate, LocalDateTime expiryDate) {
        if (produceDate == null || expiryDate == null) return 0.0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiryDate) || now.isEqual(expiryDate)) {
            return 1.0;
        }
        long start = produceDate.toEpochSecond(ZoneOffset.UTC);
        long end = expiryDate.toEpochSecond(ZoneOffset.UTC);
        long current = now.toEpochSecond(ZoneOffset.UTC);
        double totalLife = (double) end - start;
        double elapsed = (double) current - start;
        if (totalLife <= 0) return 1.0;
        double ratio = elapsed / totalLife;
        return Math.min(Math.max(ratio, 0.0), 1.0);
    }

    private int calculateDaysLeft(LocalDateTime expiryDate) {
        if (expiryDate == null) return 0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiryDate)) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(now, expiryDate);
        return (int) Math.max(0, days);
    }

    //delete
    public void deleteGoneBadItems(String userId) {
        List<Inventory> inventoriesForUser = inventoryRepo.getALlInventoryByUserId(userId);
        List<UUID> idsToDelete = inventoriesForUser.stream()
                .filter(inventory -> calculateDaysLeft(inventory.getExpiryDate()) == 0)
                .map(Inventory::getInventoryId)
                .toList();
        idsToDelete.forEach(uuid -> inventoryRepo.deleteById(uuid));
    }

    //Consumed
    @Transactional
    public Inventory updateQuantity(UUID id, int quantity) {
        Inventory inventory = inventoryRepo.findById(id);
        int newAmount = inventory.getQuantity() - quantity;
        inventory.setQuantity(newAmount);
        inventoryRepo.save(inventory);
        return inventory;
    }

    public void deleteInventory(UUID id) {
        inventoryRepo.deleteById(id);
    }

    //ShoppingList

    public void saveToShoppingList(UUID inventoryId, String userId) {
        User user = userRepo.findById(userId);
        Inventory inventory = inventoryRepo.findById(inventoryId);
        Product product = inventory.getProduct();
        ShoppingList shoppingList = new ShoppingList(null, product, user);
        shoppingListRepo.save(shoppingList);
    }

    public void deleteShoppingList(UUID id) {
        shoppingListRepo.deleteById(id);
    }

    public List<ShoppingListDto> getALlShoppingListDtoByUserId(String id){
        return shoppingListRepo.getALlShoppingListByUserId(id).stream()
                .map(i -> new ShoppingListDto(
                        i.getProduct().getName(),
                        i.getProduct().getCategory(),
                        i.getId())).toList();
    }
}
