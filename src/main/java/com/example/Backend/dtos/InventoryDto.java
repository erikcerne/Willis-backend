package com.example.Backend.dtos;

import java.net.URL;
import java.util.List;
public record InventoryDto(String name, String category, List<InventoryListDto> Specifications, URL Pic) {
}
