package com.hungrezy.foodordering.repository;

import com.hungrezy.foodordering.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    @Query("""
            select r from Recipe r
            where :query is null
               or lower(r.name) like lower(concat('%', :query, '%'))
               or lower(r.category) like lower(concat('%', :query, '%'))
               or lower(r.area) like lower(concat('%', :query, '%'))
            order by r.createdAt desc
            """)
    List<Recipe> search(String query);

    List<Recipe> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);
    List<Recipe> findByAreaIgnoreCaseOrderByCreatedAtDesc(String area);
}
