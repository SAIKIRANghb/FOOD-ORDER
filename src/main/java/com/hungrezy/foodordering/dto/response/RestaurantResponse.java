package com.hungrezy.foodordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    private UUID id;
    private String name;
    private String description;
    private String cuisineType;
    private String imageUrl;
    private Double averageRating;
    private Boolean active;
    private String email;

    @com.fasterxml.jackson.annotation.JsonProperty("_id")
    public UUID getIdAlias() {
        return id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("menu_id")
    public UUID getMenuIdAlias() {
        return id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("cuisine")
    public String getCuisineAlias() {
        return cuisineType;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("image")
    public String getImageAlias() {
        return imageUrl;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("rating")
    public Double getRatingAlias() {
        return averageRating;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("rating_count")
    public String getRatingCountAlias() {
        return "0+ ratings";
    }

    @com.fasterxml.jackson.annotation.JsonProperty("cost")
    public String getCostAlias() {
        return "Rs. 200 for two";
    }

    @com.fasterxml.jackson.annotation.JsonProperty("restaurantName")
    public String getRestaurantNameAlias() {
        return name;
    }
}
