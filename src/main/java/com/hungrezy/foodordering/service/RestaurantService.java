package com.hungrezy.foodordering.service;

import com.hungrezy.foodordering.dto.request.RestaurantRequest;
import com.hungrezy.foodordering.dto.response.RestaurantResponse;

import java.util.List;
import java.util.Map;

public interface RestaurantService {
    RestaurantResponse create(String ownerEmail, RestaurantRequest request);
    List<RestaurantResponse> search(String query, String cuisine, String city, String area);
    Map<String, List<String>> locations();
}
