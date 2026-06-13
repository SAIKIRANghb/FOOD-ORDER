package com.hungrezy.foodordering.service.impl;

import com.hungrezy.foodordering.entity.Recipe;
import com.hungrezy.foodordering.repository.RecipeRepository;
import com.hungrezy.foodordering.service.RecipeService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<Recipe> findRecipes(String query, String category, String area, int limit) {
        List<Recipe> recipes;
        if (hasText(category)) {
            recipes = recipeRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category);
        } else if (hasText(area)) {
            recipes = recipeRepository.findByAreaIgnoreCaseOrderByCreatedAtDesc(area);
        } else {
            recipes = recipeRepository.search(blankToNull(query));
        }
        return recipes.stream().limit(normalizedLimit(limit)).toList();
    }

    @Override
    public List<Recipe> today(int limit) {
        return recipeRepository.findAll().stream()
                .sorted(Comparator.comparing(Recipe::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(normalizedLimit(limit))
                .toList();
    }

    @Override
    public List<Map<String, String>> categories() {
        return recipeRepository.findAll().stream()
                .filter(recipe -> hasText(recipe.getCategory()))
                .collect(Collectors.toMap(
                        Recipe::getCategory,
                        recipe -> Map.of("strCategory", recipe.getCategory(), "image", nullToBlank(recipe.getCategoryImageUrl())),
                        (left, right) -> left
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(category -> category.get("strCategory")))
                .toList();
    }

    @Override
    public List<Map<String, String>> areas() {
        return recipeRepository.findAll().stream()
                .filter(recipe -> hasText(recipe.getArea()))
                .collect(Collectors.toMap(
                        Recipe::getArea,
                        recipe -> Map.of("strArea", recipe.getArea(), "Image", nullToBlank(recipe.getAreaImageUrl())),
                        (left, right) -> left
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(area -> area.get("strArea")))
                .toList();
    }

    private long normalizedLimit(int limit) {
        return Math.max(1, limit);
    }

    private String blankToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
