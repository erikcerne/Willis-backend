package com.example.Backend;

import com.example.Backend.dtos.ReceiveInventoryDto;
import com.example.Backend.inventory.InventoryRepository;
import com.example.Backend.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    public ReceiveInventoryDto addCart(ReceiveInventoryDto dto) {
        return repo.add(dto);
    }

}
