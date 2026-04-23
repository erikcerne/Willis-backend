package com.example.Backend.inventory;

import com.example.Backend.dtos.ReceiveInventoryDto;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepository {

    JpaInventoryRepository repo;

    public InventoryRepository(JpaInventoryRepository repo) {
        this.repo = repo;
    }

    public ReceiveInventoryDto add(ReceiveInventoryDto dto){
        return null;
    }
}
