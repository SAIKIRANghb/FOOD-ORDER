package com.hungrezy.foodordering.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private UUID restaurantId;
    private String restaurantName;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime orderedAt;
    private List<OrderItemResponse> items;
    private UserResponse user;

    @JsonProperty("_id")
    public UUID getIdAlias() {
        return id;
    }

    @JsonProperty("paymentDetails")
    public PaymentDetailsResponse getPaymentDetailsAlias() {
        return new PaymentDetailsResponse(paymentMethod, totalAmount);
    }

    @JsonProperty("userId")
    public UserResponse getUserIdAlias() {
        return user;
    }

    @JsonProperty("foodItems")
    public List<OrderItemResponse> getFoodItemsAlias() {
        return items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID menuItemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        @JsonProperty("name")
        public String getNameAlias() {
            return itemName;
        }

        @JsonProperty("price")
        public BigDecimal getPriceAlias() {
            return unitPrice;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetailsResponse {
        private String method;
        private BigDecimal amount;
    }
}
