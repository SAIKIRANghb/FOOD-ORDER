package com.hungrezy.foodordering.service.impl;

import com.hungrezy.foodordering.dto.request.MenuItemRequest;
import com.hungrezy.foodordering.dto.response.MenuItemResponse;
import com.hungrezy.foodordering.entity.MenuItem;
import com.hungrezy.foodordering.entity.Restaurant;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.MenuItemRepository;
import com.hungrezy.foodordering.repository.RestaurantRepository;
import com.hungrezy.foodordering.service.MenuService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final DtoMapper mapper;

    public MenuServiceImpl(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository, DtoMapper mapper) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurant-menu", key = "#restaurantId")
    public MenuItemResponse create(UUID restaurantId, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setVegOrNonVeg(request.getVegOrNonVeg());
        item.setImageUrl(request.getImageUrl());
        item.setAvailable(request.getAvailable() == null || request.getAvailable());
        return mapper.toMenuItemResponse(menuItemRepository.save(item));
    }

    @Override
    @Cacheable(value = "restaurant-menu", key = "#restaurantId")
    public List<MenuItemResponse> restaurantMenu(UUID restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId).stream()
                .map(mapper::toMenuItemResponse)
                .toList();
    }
}
