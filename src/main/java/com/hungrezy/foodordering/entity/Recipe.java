package com.hungrezy.foodordering.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "recipes")
public class Recipe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String category;
    private String area;
    private String imageUrl;
    private String categoryImageUrl;
    private String areaImageUrl;
    private String tags;
    private String youtubeUrl;
    private String sourceUrl;

    @Column(columnDefinition = "text")
    private String instructions;

    private String ingredient1;
    private String ingredient2;
    private String ingredient3;
    private String ingredient4;
    private String ingredient5;
    private String ingredient6;
    private String ingredient7;
    private String ingredient8;
    private String ingredient9;
    private String ingredient10;
    private String measure1;
    private String measure2;
    private String measure3;
    private String measure4;
    private String measure5;
    private String measure6;
    private String measure7;
    private String measure8;
    private String measure9;
    private String measure10;

    @JsonProperty("idMeal")
    public String mealIdAlias() {
        return id == null ? null : id.toString();
    }

    @JsonProperty("strMeal")
    public String mealAlias() {
        return name;
    }

    @JsonProperty("strCategory")
    public String categoryAlias() {
        return category;
    }

    @JsonProperty("strArea")
    public String areaAlias() {
        return area;
    }

    @JsonProperty("strMealThumb")
    public String mealThumbAlias() {
        return imageUrl;
    }

    @JsonProperty("strInstructions")
    public String instructionsAlias() {
        return instructions;
    }

    @JsonProperty("strTags")
    public String tagsAlias() {
        return tags;
    }

    @JsonProperty("strYoutube")
    public String youtubeAlias() {
        return youtubeUrl;
    }

    @JsonProperty("strSource")
    public String sourceAlias() {
        return sourceUrl;
    }

    @JsonProperty("strIngredient1") public String ingredient1Alias() { return ingredient1; }
    @JsonProperty("strIngredient2") public String ingredient2Alias() { return ingredient2; }
    @JsonProperty("strIngredient3") public String ingredient3Alias() { return ingredient3; }
    @JsonProperty("strIngredient4") public String ingredient4Alias() { return ingredient4; }
    @JsonProperty("strIngredient5") public String ingredient5Alias() { return ingredient5; }
    @JsonProperty("strIngredient6") public String ingredient6Alias() { return ingredient6; }
    @JsonProperty("strIngredient7") public String ingredient7Alias() { return ingredient7; }
    @JsonProperty("strIngredient8") public String ingredient8Alias() { return ingredient8; }
    @JsonProperty("strIngredient9") public String ingredient9Alias() { return ingredient9; }
    @JsonProperty("strIngredient10") public String ingredient10Alias() { return ingredient10; }
    @JsonProperty("strMeasure1") public String measure1Alias() { return measure1; }
    @JsonProperty("strMeasure2") public String measure2Alias() { return measure2; }
    @JsonProperty("strMeasure3") public String measure3Alias() { return measure3; }
    @JsonProperty("strMeasure4") public String measure4Alias() { return measure4; }
    @JsonProperty("strMeasure5") public String measure5Alias() { return measure5; }
    @JsonProperty("strMeasure6") public String measure6Alias() { return measure6; }
    @JsonProperty("strMeasure7") public String measure7Alias() { return measure7; }
    @JsonProperty("strMeasure8") public String measure8Alias() { return measure8; }
    @JsonProperty("strMeasure9") public String measure9Alias() { return measure9; }
    @JsonProperty("strMeasure10") public String measure10Alias() { return measure10; }
}
