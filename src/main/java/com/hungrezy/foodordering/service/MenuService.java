package com.hungrezy.foodordering.service;

import com.hungrezy.foodordering.dto.request.MenuItemRequest;
import com.hungrezy.foodordering.dto.response.MenuItemResponse;

import java.util.List;
import java.util.UUID;

public interface MenuService {
    MenuItemResponse create(UUID restaurantId, MenuItemRequest request);
    List<MenuItemResponse> restaurantMenu(UUID restaurantId);
}
