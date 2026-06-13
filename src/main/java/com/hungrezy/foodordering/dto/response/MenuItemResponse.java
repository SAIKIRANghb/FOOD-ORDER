package com.hungrezy.foodordering.dto.response;

import com.hungrezy.foodordering.entity.enums.VegNonVeg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private VegNonVeg vegOrNonVeg;
    private String imageUrl;
    private Boolean available;

    @com.fasterxml.jackson.annotation.JsonProperty("_id")
    public UUID getIdAlias() {
        return id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("itemName")
    public String getItemNameAlias() {
        return name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("veg_or_non_veg")
    public VegNonVeg getVegOrNonVegAlias() {
        return vegOrNonVeg;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("image")
    public String getImageAlias() {
        return imageUrl;
    }
}
