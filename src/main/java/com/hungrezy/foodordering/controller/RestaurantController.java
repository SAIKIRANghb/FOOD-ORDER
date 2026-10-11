package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.dto.request.RestaurantRequest;
import com.hungrezy.foodordering.dto.response.MenuItemResponse;
import com.hungrezy.foodordering.dto.response.RestaurantResponse;
import com.hungrezy.foodordering.entity.Address;
import com.hungrezy.foodordering.entity.Restaurant;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.RestaurantRepository;
import com.hungrezy.foodordering.service.MenuService;
import com.hungrezy.foodordering.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/restaurants", "/api/restaurant"})
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final RestaurantRepository restaurantRepository;
    private final DtoMapper mapper;

    public RestaurantController(RestaurantService restaurantService, MenuService menuService,
                                RestaurantRepository restaurantRepository, DtoMapper mapper) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER','ADMIN')")
    ApiResponse<RestaurantResponse> create(Principal principal, @Valid @RequestBody RestaurantRequest request) {
        return ApiResponse.ok("Restaurant created", restaurantService.create(principal.getName(), request));
    }

    @GetMapping("/search")
    ApiResponse<List<RestaurantResponse>> search(@RequestParam(required = false) String query,
                                                 @RequestParam(required = false) String cuisine) {
        return ApiResponse.ok("Restaurants fetched", restaurantService.search(query, cuisine, null, null));
    }

    @GetMapping
    ApiResponse<List<RestaurantResponse>> list(@RequestParam(required = false) String city,
                                               @RequestParam(required = false) String area,
                                               @RequestParam(required = false) String query,
                                               @RequestParam(required = false) String cuisine) {
        return ApiResponse.ok("Restaurants fetched", restaurantService.search(query, cuisine, city, area));
    }

    @GetMapping("/all")
    ApiResponse<List<RestaurantResponse>> all() {
        return ApiResponse.ok("Restaurants fetched", restaurantService.search(null, null, null, null));
    }

    @GetMapping("/count")
    ApiResponse<Map<String, Object>> count() {
        List<Restaurant> all = restaurantRepository.findAll();
        long total = all.size();
        long approved = all.stream().filter(r -> Boolean.TRUE.equals(r.getActive())).count();
        Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("total", total);
        data.put("approved", approved);
        data.put("suspended", total - approved);
        data.put("rejected", 0L);
        data.put("inprogress", 0L);
        data.put("monthlyRestaurants", com.hungrezy.foodordering.util.MonthlyCounts.of(all, "restaurants"));
        return ApiResponse.ok("Restaurants counted", data);
    }

    @GetMapping("/locations")
    ApiResponse<Map<String, List<String>>> locations() {
        return ApiResponse.ok("Locations fetched", restaurantService.locations());
    }

    @GetMapping("/{id}")
    ApiResponse<RestaurantResponse> get(@PathVariable UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        return ApiResponse.ok("Restaurant fetched", mapper.toRestaurantResponse(restaurant));
    }

    @PutMapping("/{id}")
    @Transactional
    ApiResponse<RestaurantResponse> update(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        if (body != null) {
            if (body.get("name") != null) restaurant.setName(body.get("name"));
            if (body.get("restaurantName") != null) restaurant.setName(body.get("restaurantName"));
            if (body.get("description") != null) restaurant.setDescription(body.get("description"));
            if (body.get("cuisineType") != null) restaurant.setCuisineType(body.get("cuisineType"));
            if (body.get("cuisine") != null) restaurant.setCuisineType(body.get("cuisine"));

            Address address = restaurant.getAddress() == null ? new Address() : restaurant.getAddress();
            if (body.get("address") != null) address.setLine1(body.get("address"));
            if (body.get("area") != null) address.setLine2(body.get("area"));
            if (body.get("city") != null) address.setCity(body.get("city"));
            if (body.get("pincode") != null) address.setPostalCode(body.get("pincode"));
            restaurant.setAddress(address);
        }
        return ApiResponse.ok("Restaurant updated", mapper.toRestaurantResponse(restaurant));
    }

    @GetMapping("/{id}/menu")
    ApiResponse<List<MenuItemResponse>> menu(@PathVariable UUID id) {
        return ApiResponse.ok("Menu fetched", menuService.restaurantMenu(id));
    }

    @PutMapping("/{id}/updateStatus")
    @Transactional
    ApiResponse<RestaurantResponse> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        String status = body == null ? null : body.get("status");
        if (status != null) {
            restaurant.setActive("approved".equalsIgnoreCase(status) || "active".equalsIgnoreCase(status));
        }
        return ApiResponse.ok("Restaurant status updated", mapper.toRestaurantResponse(restaurant));
    }

    @PostMapping("/{id}/upload/image")
    @Transactional
    ApiResponse<RestaurantResponse> uploadImage(@PathVariable UUID id, @RequestParam("image") MultipartFile image) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurant.setImageUrl(image == null || image.isEmpty() ? restaurant.getImageUrl() : image.getOriginalFilename());
        return ApiResponse.ok("Image uploaded", mapper.toRestaurantResponse(restaurant));
    }
}
