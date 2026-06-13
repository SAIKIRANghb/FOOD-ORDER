package com.hungrezy.foodordering.repository;

import com.hungrezy.foodordering.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByRestaurantId(UUID restaurantId);
}
