package com.example.Backend.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReceiveInventoryDto(LocalDateTime expiryDate, LocalDateTime produceDate, int quantity, UUID productId, String userId) {
}
