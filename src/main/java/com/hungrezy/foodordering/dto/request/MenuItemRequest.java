package com.hungrezy.foodordering.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hungrezy.foodordering.entity.enums.VegNonVeg;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    private String category;

    @JsonAlias("veg_or_non_veg")
    private VegNonVeg vegOrNonVeg;

    private String imageUrl;
    private Boolean available;
}
