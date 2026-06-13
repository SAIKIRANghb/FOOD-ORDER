package com.hungrezy.foodordering.service;

import com.hungrezy.foodordering.entity.Recipe;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    List<Recipe> findRecipes(String query, String category, String area, int limit);
    List<Recipe> today(int limit);
    List<Map<String, String>> categories();
    List<Map<String, String>> areas();
}
