package com.example.Backend.inventory;

import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class InventoryRepository {

    JpaInventoryRepository repo;

    public InventoryRepository(JpaInventoryRepository repo) {
        this.repo = repo;
    }

    public ReceiveInventoryDto add(ReceiveInventoryDto dto){
        return null;
    }

    public List<Inventory> getALlInventoryByUserId(UUID id){
        return repo.findAllByUser_UserId(id);
    }

    public void save(Inventory inventory) {
        repo.save(inventory);
    }

    public void saveAll(List<Inventory> inventories){
        repo.saveAll(inventories);
    }


}
