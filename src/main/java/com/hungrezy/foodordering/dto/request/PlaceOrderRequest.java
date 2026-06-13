package com.hungrezy.foodordering.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {
    @NotNull
    private UUID restaurantId;

    private List<OrderLineRequest> items;
    private List<OrderLineRequest> foodItems;

    @Valid
    private AddressRequest deliveryAddress;

    private String address;
    private String paymentMethod;
    private PaymentDetailsRequest paymentDetails;

    public List<OrderLineRequest> getOrderItems() {
        return items != null ? items : foodItems;
    }

    public AddressRequest getOrderAddress() {
        return deliveryAddress != null ? deliveryAddress : new AddressRequest(address, null, null, null, null, null, null, null);
    }

    public String getOrderPaymentMethod() {
        return paymentMethod != null ? paymentMethod : paymentDetails == null ? null : paymentDetails.getMethod();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLineRequest {
        private UUID menuItemId;

        @JsonAlias("_id")
        private UUID id;

        private String name;
        private String category;

        @Positive
        private Integer quantity;

        private BigDecimal price;
        private String vegOrNonVeg;

        public UUID getMenuItemIdOrId() {
            return menuItemId != null ? menuItemId : id;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetailsRequest {
        private String method;
        private BigDecimal amount;
    }
}
