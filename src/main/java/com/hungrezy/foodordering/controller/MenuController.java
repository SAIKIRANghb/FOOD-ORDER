package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.dto.request.MenuItemRequest;
import com.hungrezy.foodordering.dto.response.MenuCategoryItemResponse;
import com.hungrezy.foodordering.dto.response.MenuItemResponse;
import com.hungrezy.foodordering.entity.MenuItem;
import com.hungrezy.foodordering.entity.enums.VegNonVeg;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.repository.MenuItemRepository;
import com.hungrezy.foodordering.service.MenuService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class MenuController {
    private final MenuService menuService;
    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuService menuService, MenuItemRepository menuItemRepository) {
        this.menuService = menuService;
        this.menuItemRepository = menuItemRepository;
    }

    @PostMapping("/api/restaurants/{restaurantId}/menu")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER','ADMIN')")
    ApiResponse<MenuItemResponse> create(@PathVariable UUID restaurantId, @Valid @RequestBody MenuItemRequest request) {
        return ApiResponse.ok("Menu item created", menuService.create(restaurantId, request));
    }

    @GetMapping({"/api/menu/{restaurantId}", "/api/restaurant/menu/{restaurantId}"})
    ApiResponse<Map<String, Map<String, MenuCategoryItemResponse>>> groupedMenu(
            @PathVariable UUID restaurantId,
            @RequestParam(required = false) Boolean availability) {
        return ApiResponse.ok("Menu fetched", buildGroupedMenu(restaurantId, availability));
    }

    @PostMapping("/api/menu/addMenu")
    @Transactional
    ApiResponse<MenuCategoryItemResponse> addMenu(@RequestBody LegacyMenuRequest request) {
        MenuItemRequest menuItem = request.toMenuItemRequest();
        MenuItemResponse created = menuService.create(request.getRestaurantId(), menuItem);
        MenuItem saved = menuItemRepository.findById(created.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        return ApiResponse.ok("Menu item added", toCategoryItem(saved));
    }

    @PutMapping("/api/menu/updateMenu")
    @Transactional
    ApiResponse<MenuCategoryItemResponse> updateMenu(@RequestBody LegacyMenuRequest request) {
        MenuItem item = resolveMenuItem(request);
        LegacyMenuItem update = request.getMenuItem();
        item.setPrice(update.getPrice() == null ? item.getPrice() : update.getPrice());
        item.setDescription(update.getDescription() == null ? item.getDescription() : update.getDescription());
        item.setVegOrNonVeg(update.getVegOrNonVegValue() == null ? item.getVegOrNonVeg() : update.getVegOrNonVegValue());
        item.setImageUrl(update.getImageUrl() == null ? item.getImageUrl() : update.getImageUrl());
        item.setAvailable(update.getAvailable() == null ? item.getAvailable() : update.getAvailable());
        return ApiResponse.ok("Menu item updated", toCategoryItem(menuItemRepository.save(item)));
    }

    @PutMapping("/api/menu/deleteMenu")
    @Transactional
    ApiResponse<Map<String, Boolean>> deleteMenu(@RequestBody LegacyMenuRequest request) {
        MenuItem item = resolveMenuItem(request);
        menuItemRepository.delete(item);
        return ApiResponse.ok("Menu item deleted", Map.of("deleted", true));
    }

    private Map<String, Map<String, MenuCategoryItemResponse>> buildGroupedMenu(UUID restaurantId, Boolean availability) {
        List<MenuItem> items = Boolean.TRUE.equals(availability)
                ? menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId)
                : menuItemRepository.findByRestaurantId(restaurantId);
        return items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory() == null || item.getCategory().isBlank() ? "Menu" : item.getCategory(),
                        LinkedHashMap::new,
                        Collectors.toMap(MenuItem::getName, this::toCategoryItem, (left, right) -> left, LinkedHashMap::new)
                ));
    }

    private MenuCategoryItemResponse toCategoryItem(MenuItem item) {
        return new MenuCategoryItemResponse(
                item.getId(),
                item.getId(),
                item.getPrice(),
                item.getCategory(),
                item.getDescription(),
                item.getImageUrl(),
                item.getVegOrNonVeg(),
                0,
                item.getAvailable()
        );
    }

    private MenuItem resolveMenuItem(LegacyMenuRequest request) {
        LegacyMenuItem menuItem = request.getMenuItem();
        return menuItemRepository.findByRestaurantIdAndNameIgnoreCaseAndCategoryIgnoreCase(
                        request.getRestaurantId(),
                        menuItem.getName(),
                        menuItem.getCategory() == null ? "" : menuItem.getCategory())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
    }

    @Getter
    @Setter
    private static class LegacyMenuRequest {
        private UUID restaurantId;
        private LegacyMenuItem menuItem;

        MenuItemRequest toMenuItemRequest() {
            return new MenuItemRequest(
                    menuItem.getName(),
                    menuItem.getDescription(),
                    menuItem.getPrice(),
                    menuItem.getCategory(),
                    menuItem.getVegOrNonVegValue(),
                    menuItem.getImageUrl(),
                    menuItem.getAvailable()
            );
        }
    }

    @Getter
    @Setter
    private static class LegacyMenuItem {
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
        private String veg_or_non_veg;
        private String vegOrNonVeg;
        private String imageUrl;
        private Boolean available;

        VegNonVeg getVegOrNonVegValue() {
            String value = vegOrNonVeg != null ? vegOrNonVeg : veg_or_non_veg;
            if (value == null || value.isBlank()) {
                return null;
            }
            return switch (value.trim().toLowerCase()) {
                case "veg", "vegetarian" -> VegNonVeg.VEG;
                default -> VegNonVeg.NON_VEG;
            };
        }
    }
}
