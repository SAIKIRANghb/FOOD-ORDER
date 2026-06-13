package com.hungrezy.foodordering.service.impl;

import com.hungrezy.foodordering.dto.request.RestaurantRequest;
import com.hungrezy.foodordering.dto.response.RestaurantResponse;
import com.hungrezy.foodordering.entity.Restaurant;
import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.RestaurantRepository;
import com.hungrezy.foodordering.repository.UserRepository;
import com.hungrezy.foodordering.service.RestaurantService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, UserRepository userRepository, DtoMapper mapper) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public RestaurantResponse create(String ownerEmail, RestaurantRequest request) {
        User owner = userRepository.findByEmail(ownerEmail).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        Restaurant restaurant = new Restaurant();
        restaurant.setOwner(owner);
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setAddress(mapper.toAddress(request.getAddress()));
        restaurant.setImageUrl(request.getImageUrl());
        return mapper.toRestaurantResponse(restaurantRepository.save(restaurant));
    }

    @Override
    @Cacheable(value = "restaurant-search", key = "{#query, #cuisine, #city, #area}")
    public List<RestaurantResponse> search(String query, String cuisine, String city, String area) {
        return restaurantRepository.search(blankToNull(query), blankToNull(cuisine), blankToNull(city), blankToNull(area)).stream()
                .map(mapper::toRestaurantResponse)
                .toList();
    }

    @Override
    public Map<String, List<String>> locations() {
        return restaurantRepository.findAll().stream()
                .filter(restaurant -> Boolean.TRUE.equals(restaurant.getActive()))
                .map(Restaurant::getAddress)
                .filter(address -> address != null && hasText(address.getCity()) && hasText(address.getLine2()))
                .collect(Collectors.groupingBy(
                        address -> address.getCity().trim(),
                        LinkedHashMap::new,
                        Collectors.mapping(address -> address.getLine2().trim(),
                                Collectors.collectingAndThen(Collectors.toCollection(TreeSet::new), List::copyOf))
                ));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
