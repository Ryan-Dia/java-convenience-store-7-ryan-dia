package store.error;

import store.model.Item;
import store.model.order.OrderItem;

public class PromotionConfirmationForFreeException extends RuntimeException {
    private Item item;
    private OrderItem orderItem;
    private int shortfall;

    public PromotionConfirmationForFreeException(Item item, OrderItem orderItem, int shortfall) {
        super();
        this.item = item;
        this.orderItem = orderItem;
        this.shortfall = shortfall;
    }


    public Item getItem() {
        return item;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public int getOrderQuantity() {
        return orderItem.getQuantity();
    }

    public int getShortfall() {
        return shortfall;
    }
}
