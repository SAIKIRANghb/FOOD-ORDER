package com.hungrezy.foodordering.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequest {
    @NotBlank
    private String name;

    private String description;
    private String cuisineType;
    private AddressRequest address;
    private String imageUrl;
}
