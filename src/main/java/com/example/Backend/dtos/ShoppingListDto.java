package com.example.Backend.dtos;

import java.util.UUID;

public record ShoppingListDto(String name, String category, UUID id) {
}
