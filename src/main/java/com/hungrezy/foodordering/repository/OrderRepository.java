package com.hungrezy.foodordering.repository;

import com.hungrezy.foodordering.entity.Order;
import com.hungrezy.foodordering.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByOrderedAtDesc(UUID userId);
    List<Order> findByRestaurantIdOrderByOrderedAtDesc(UUID restaurantId);
    List<Order> findByRestaurantIdAndStatusOrderByOrderedAtDesc(UUID restaurantId, OrderStatus status);
    List<Order> findByRestaurantIdAndOrderedAtAfterOrderByOrderedAtDesc(UUID restaurantId, LocalDateTime orderedAfter);
    List<Order> findByRestaurantIdAndStatusAndOrderedAtAfterOrderByOrderedAtDesc(UUID restaurantId, OrderStatus status, LocalDateTime orderedAfter);
}
