package com.hungrezy.foodordering.service;

import com.hungrezy.foodordering.dto.request.PlaceOrderRequest;
import com.hungrezy.foodordering.dto.response.OrderResponse;
import com.hungrezy.foodordering.entity.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse placeOrder(String userEmail, PlaceOrderRequest request);
    List<OrderResponse> myOrders(String userEmail);
    OrderResponse track(UUID orderId);
    OrderResponse updateStatus(UUID orderId, OrderStatus status);
}
