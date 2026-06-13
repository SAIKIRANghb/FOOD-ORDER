package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.dto.request.ReviewRequest;
import com.hungrezy.foodordering.entity.Review;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.repository.RestaurantRepository;
import com.hungrezy.foodordering.repository.ReviewRepository;
import com.hungrezy.foodordering.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping("/api/reviews")
    ApiResponse<Review> create(Principal principal, @Valid @RequestBody ReviewRequest request) {
        Review review = new Review();
        review.setUser(userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        review.setRestaurant(restaurantRepository.findById(request.getRestaurantId()).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return ApiResponse.ok("Review created", reviewRepository.save(review));
    }

    @PostMapping("/api/review/restaurant/{restaurantId}")
    ApiResponse<Review> createForFrontend(@PathVariable UUID restaurantId, Principal principal,
                                          @RequestBody Map<String, Object> request) {
        Review review = new Review();
        review.setUser(userRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        review.setRestaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")));
        String rating = request == null || request.get("rating") == null ? null : request.get("rating").toString();
        review.setRating(rating == null || rating.isBlank() ? 5 : Integer.parseInt(rating));
        review.setComment(request == null ? null : firstNonBlank(stringValue(request.get("review")), stringValue(request.get("comment"))));
        return ApiResponse.ok("Review created", reviewRepository.save(review));
    }

    @GetMapping("/api/reviews/restaurant/{restaurantId}")
    ApiResponse<List<Review>> byRestaurant(@PathVariable UUID restaurantId) {
        return ApiResponse.ok("Reviews fetched", reviewRepository.findByRestaurantId(restaurantId));
    }

    @GetMapping({"/api/review", "/api/review/"})
    ApiResponse<List<Review>> all() {
        return ApiResponse.ok("Reviews fetched", reviewRepository.findAll());
    }

    @GetMapping("/api/review/top")
    ApiResponse<List<Review>> top() {
        return ApiResponse.ok("Reviews fetched", reviewRepository.findAll().stream().limit(6).toList());
    }

    @GetMapping("/api/review/restaurant/{restaurantId}")
    ApiResponse<List<Review>> frontendByRestaurant(@PathVariable UUID restaurantId) {
        return ApiResponse.ok("Reviews fetched", reviewRepository.findByRestaurantId(restaurantId));
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }
}
