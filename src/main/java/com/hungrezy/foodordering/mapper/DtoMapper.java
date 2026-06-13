package com.hungrezy.foodordering.mapper;

import com.hungrezy.foodordering.dto.request.AddressRequest;
import com.hungrezy.foodordering.dto.response.MenuItemResponse;
import com.hungrezy.foodordering.dto.response.OrderResponse;
import com.hungrezy.foodordering.dto.response.RestaurantResponse;
import com.hungrezy.foodordering.dto.response.UserResponse;
import com.hungrezy.foodordering.entity.Address;
import com.hungrezy.foodordering.entity.MenuItem;
import com.hungrezy.foodordering.entity.Order;
import com.hungrezy.foodordering.entity.Restaurant;
import com.hungrezy.foodordering.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DtoMapper {
    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getMobileNumber(),
                user.getRole(), user.getStatus(), user.getImageUrl(), user.getCreatedAt());
    }

    public RestaurantResponse toRestaurantResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getCuisineType(),
                restaurant.getImageUrl(),
                restaurant.getAverageRating(),
                restaurant.getActive(),
                null
        );
    }

    public MenuItemResponse toMenuItemResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory(),
                item.getVegOrNonVeg(),
                item.getImageUrl(),
                item.getAvailable()
        );
    }

    public OrderResponse toOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getMenuItem().getId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getTotalAmount(),
                toApiStatus(order.getStatus()),
                order.getPaymentMethod(),
                order.getPaymentStatus() == null ? null : order.getPaymentStatus().name().toLowerCase(),
                order.getOrderedAt(),
                items,
                toUserResponse(order.getUser())
        );
    }

    private String toApiStatus(com.hungrezy.foodordering.entity.enums.OrderStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PREPARING -> "processing";
            default -> status.name().toLowerCase();
        };
    }

    public Address toAddress(AddressRequest request) {
        if (request == null) {
            return null;
        }
        Address address = new Address();
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        return address;
    }
}
