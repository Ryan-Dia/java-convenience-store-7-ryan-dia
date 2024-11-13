package store.error;

import store.model.order.OrderItem;

public class PurchaseConfirmationWithoutPromotionException extends RuntimeException {
    private final String itemName;
    private final int remainingQuantity;
    private final OrderItem orderItem;
    private final int remainingPromotionQuantity;

    public PurchaseConfirmationWithoutPromotionException(String itemName, int remainingQuantity,
                                                         int remainingPromotionQuantity,
                                                         OrderItem orderItem) {
        super();
        this.itemName = itemName;
        this.remainingQuantity = remainingQuantity;
        this.remainingPromotionQuantity = remainingPromotionQuantity;
        this.orderItem = orderItem;
    }

    public String getItemName() {
        return itemName;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public int getRemainingPromotionQuantity() {
        return remainingPromotionQuantity;
    }
}
