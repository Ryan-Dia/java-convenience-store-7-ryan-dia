package store.dto;

import store.model.order.OrderItem;

public record OrderItemDto(String name, int totalOrderQuantity, int price, int promotionAppliedQuantity) {
    public OrderItemDto(OrderItem orderItem) {
        this(orderItem.getName(), orderItem.getTotalOrderQuantity(), orderItem.getPrice(),
                orderItem.getPromotionAppliedQuantity());
    }
}
