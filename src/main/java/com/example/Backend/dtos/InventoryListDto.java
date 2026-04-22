package com.example.Backend.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryListDto(LocalDateTime expiryDate, LocalDateTime produceDate, int Quantity, int DaysLeft, UUID InventoryId) {
}
