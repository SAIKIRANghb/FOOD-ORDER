package com.hungrezy.foodordering.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class MenuCategoryItemResponse {
    @JsonProperty("_id")
    private UUID id;

    private UUID menuItemId;
    private BigDecimal price;
    private String category;
    private String description;
    private String image;

    @JsonProperty("veg_or_non_veg")
    private VegNonVeg vegOrNonVeg;

    private int count;
    private Boolean availability;
}
