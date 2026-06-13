package com.hungrezy.foodordering.dto.request;

import com.hungrezy.foodordering.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    private String status;

    public OrderStatus toOrderStatus() {
        return switch (status == null ? "" : status.toLowerCase()) {
            case "placed" -> OrderStatus.PLACED;
            case "processing" -> OrderStatus.PREPARING;
            case "confirmed" -> OrderStatus.CONFIRMED;
            case "out_for_delivery", "out-for-delivery" -> OrderStatus.OUT_FOR_DELIVERY;
            case "delivered" -> OrderStatus.DELIVERED;
            case "cancelled", "canceled" -> OrderStatus.CANCELLED;
            default -> OrderStatus.valueOf(status.toUpperCase());
        };
    }
}
