package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.entity.Recipe;
import com.hungrezy.foodordering.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    ApiResponse<List<Recipe>> recipes(@RequestParam(required = false) String query,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) String area,
                                      @RequestParam(defaultValue = "16") int limit) {
        return ApiResponse.ok("Recipes fetched", recipeService.findRecipes(query, category, area, limit));
    }

    @GetMapping("/today")
    ApiResponse<List<Recipe>> today(@RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.ok("Recipes fetched", recipeService.today(limit));
    }

    @GetMapping("/categories")
    ApiResponse<List<Map<String, String>>> categories() {
        return ApiResponse.ok("Recipe categories fetched", recipeService.categories());
    }

    @GetMapping("/areas")
    ApiResponse<List<Map<String, String>>> areas() {
        return ApiResponse.ok("Recipe areas fetched", recipeService.areas());
    }
}
