package com.hungrezy.foodordering.repository;

import com.hungrezy.foodordering.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    @Query("""
            select r from Restaurant r
            where r.active = true
              and (:query is null or lower(r.name) like lower(concat('%', :query, '%'))
                   or lower(r.description) like lower(concat('%', :query, '%')))
              and (:cuisine is null or lower(r.cuisineType) = lower(:cuisine))
              and (:city is null or lower(r.address.city) = lower(:city))
              and (:area is null or lower(r.address.line2) = lower(:area))
            """)
    List<Restaurant> search(String query, String cuisine, String city, String area);

    Optional<Restaurant> findFirstByOwnerId(UUID ownerId);
}
