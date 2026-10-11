package com.hungrezy.foodordering.repository;

import com.hungrezy.foodordering.entity.Order;
import com.hungrezy.foodordering.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByOrderedAtDesc(UUID userId);
    List<Order> findByRestaurantIdOrderByOrderedAtDesc(UUID restaurantId);
    List<Order> findByRestaurantIdAndStatusOrderByOrderedAtDesc(UUID restaurantId, OrderStatus status);
    List<Order> findByRestaurantIdAndOrderedAtAfterOrderByOrderedAtDesc(UUID restaurantId, LocalDateTime orderedAfter);
    List<Order> findByRestaurantIdAndStatusAndOrderedAtAfterOrderByOrderedAtDesc(UUID restaurantId, OrderStatus status, LocalDateTime orderedAfter);

    @Query("select count(o) from Order o where o.status = :status")
    long findByStatus(@Param("status") OrderStatus status);
}
