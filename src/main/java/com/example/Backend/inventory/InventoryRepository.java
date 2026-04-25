package com.example.Backend.inventory;

import com.example.Backend.dtos.ReceiveInventoryDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
public class InventoryRepository {

    JpaInventoryRepository repo;

    public InventoryRepository(JpaInventoryRepository repo) {
        this.repo = repo;
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

    public void deleteById(UUID id){
        repo.deleteById(id);
    }

    public Inventory findById(UUID id){
       return repo.findById(id).orElseThrow(()-> new NoSuchElementException("du har inte den i dig inventory"));
    }

}
