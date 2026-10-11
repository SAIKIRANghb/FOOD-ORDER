package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.dto.request.OrderStatusUpdateRequest;
import com.hungrezy.foodordering.dto.request.PlaceOrderRequest;
import com.hungrezy.foodordering.dto.response.OrderResponse;
import com.hungrezy.foodordering.entity.enums.OrderStatus;
import com.hungrezy.foodordering.repository.OrderRepository;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final DtoMapper mapper;

    public OrderController(OrderService orderService, OrderRepository orderRepository, DtoMapper mapper) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @PostMapping("/api/orders")
    ApiResponse<OrderResponse> placeOrder(Principal principal, @Valid @RequestBody PlaceOrderRequest request) {
        return ApiResponse.ok("Order placed", orderService.placeOrder(principal.getName(), request));
    }

    @GetMapping("/api/orders/my-orders")
    ApiResponse<List<OrderResponse>> myOrders(Principal principal) {
        return ApiResponse.ok("Orders fetched", orderService.myOrders(principal.getName()));
    }

    @GetMapping("/api/orders/{id}/track")
    ApiResponse<OrderResponse> track(@PathVariable UUID id) {
        return ApiResponse.ok("Order fetched", orderService.track(id));
    }

    @PatchMapping("/api/orders/{id}/status")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER','ADMIN')")
    ApiResponse<OrderResponse> updateStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {
        return ApiResponse.ok("Order status updated", orderService.updateStatus(id, status));
    }

    @PostMapping("/api/order/place")
    ApiResponse<OrderResponse> placeOrderAlias(Principal principal, @Valid @RequestBody PlaceOrderRequest request) {
        return ApiResponse.ok("Order placed", orderService.placeOrder(principal.getName(), request));
    }

    @GetMapping("/api/order/count")
    ApiResponse<Map<String, Object>> count() {
        long totalOrders = orderRepository.count();
        long deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);
        return ApiResponse.ok("Orders counted", Map.of(
                "totalOrders", totalOrders,
                "deliveredOrders", deliveredOrders
        ));
    }

    @GetMapping("/api/order/{id}")
    ApiResponse<OrderResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok("Order fetched", orderService.track(id));
    }

    @GetMapping("/api/order/user/{userId}")
    ApiResponse<List<OrderResponse>> byUser(@PathVariable UUID userId, @RequestParam(required = false) String status) {
        List<OrderResponse> orders = orderRepository.findByUserIdOrderByOrderedAtDesc(userId).stream()
                .filter(order -> statusMatches(order.getStatus(), status))
                .map(mapper::toOrderResponse)
                .toList();
        return ApiResponse.ok("Orders fetched", orders);
    }

    @GetMapping("/api/order/restaurant/{restaurantId}")
    ApiResponse<List<OrderResponse>> byRestaurant(@PathVariable UUID restaurantId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String customerId,
                                                  @RequestParam(required = false, name = "cutomerId") String misspelledCustomerId) {
        UUID selectedCustomer = parseOptionalUuid(customerId != null ? customerId : misspelledCustomerId);
        List<OrderResponse> orders = orderRepository.findByRestaurantIdOrderByOrderedAtDesc(restaurantId).stream()
                .filter(order -> statusMatches(order.getStatus(), status))
                .filter(order -> selectedCustomer == null || order.getUser().getId().equals(selectedCustomer))
                .map(mapper::toOrderResponse)
                .toList();
        return ApiResponse.ok("Orders fetched", orders);
    }

    @GetMapping("/api/order/restaurant/stats/{restaurantId}")
    ApiResponse<Map<String, Object>> restaurantStats(@PathVariable UUID restaurantId) {
        return ApiResponse.ok("Restaurant stats fetched", buildStats(orderRepository.findByRestaurantIdOrderByOrderedAtDesc(restaurantId)));
    }

    @GetMapping("/api/order/restaurant/stats/filters/{restaurantId}")
    ApiResponse<Map<String, Object>> restaurantStatsWithFilters(@PathVariable UUID restaurantId,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) String date,
                                                                @RequestParam(required = false) String customerId) {
        LocalDateTime start = startDate(date);
        UUID selectedCustomer = parseOptionalUuid(customerId);
        List<com.hungrezy.foodordering.entity.Order> orders = orderRepository.findByRestaurantIdOrderByOrderedAtDesc(restaurantId).stream()
                .filter(order -> statusMatches(order.getStatus(), status))
                .filter(order -> start == null || (order.getOrderedAt() != null && !order.getOrderedAt().isBefore(start)))
                .filter(order -> selectedCustomer == null || order.getUser().getId().equals(selectedCustomer))
                .toList();
        return ApiResponse.ok("Restaurant stats fetched", buildStats(orders));
    }

    @PatchMapping("/api/order/status/{orderId}")
    ApiResponse<OrderResponse> updateStatusAlias(@PathVariable UUID orderId, @RequestBody OrderStatusUpdateRequest request) {
        return ApiResponse.ok("Order status updated", orderService.updateStatus(orderId, request.toOrderStatus()));
    }

    @PostMapping("/api/order/status/{orderId}")
    ApiResponse<OrderResponse> postStatusAlias(@PathVariable UUID orderId, @RequestBody OrderStatusUpdateRequest request) {
        return ApiResponse.ok("Order status updated", orderService.updateStatus(orderId, request.toOrderStatus()));
    }

    @PostMapping("/api/order/user/cancel/{orderId}")
    ApiResponse<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        return ApiResponse.ok("Order cancelled", orderService.updateStatus(orderId, OrderStatus.CANCELLED));
    }

    private boolean statusMatches(OrderStatus orderStatus, String requestedStatus) {
        if (requestedStatus == null || requestedStatus.isBlank() || "all".equalsIgnoreCase(requestedStatus)) {
            return true;
        }
        return orderStatus == new OrderStatusUpdateRequest(requestedStatus).toOrderStatus();
    }

    private LocalDateTime startDate(String date) {
        if (date == null || date.isBlank() || "all".equalsIgnoreCase(date)) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (date.toLowerCase()) {
            case "today" -> now.toLocalDate().atStartOfDay();
            case "last-week" -> now.minusWeeks(1);
            case "last-month" -> now.minusMonths(1);
            case "last-year" -> now.minusYears(1);
            default -> null;
        };
    }

    private UUID parseOptionalUuid(String value) {
        if (value == null || value.isBlank() || "all".equalsIgnoreCase(value)) {
            return null;
        }
        return UUID.fromString(value);
    }

    private Map<String, Object> buildStats(List<com.hungrezy.foodordering.entity.Order> orders) {
        BigDecimal totalRevenue = orders.stream()
                .map(com.hungrezy.foodordering.entity.Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageRevenue = orders.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP);
        return Map.of(
                "totalOrders", orders.size(),
                "totalRevenue", totalRevenue,
                "averageRevenuePerOrder", averageRevenue,
                "mostFrequentCategory", mostFrequent(orders, true),
                "mostFrequentFoodItem", mostFrequent(orders, false)
        );
    }

    private List<String> mostFrequent(List<com.hungrezy.foodordering.entity.Order> orders, boolean category) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(item -> category ? item.getMenuItem().getCategory() : item.getItemName())
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(entry -> List.of(entry.getKey()))
                .orElse(List.of("none"));
    }
}
