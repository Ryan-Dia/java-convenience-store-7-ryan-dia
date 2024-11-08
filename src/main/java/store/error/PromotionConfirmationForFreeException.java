package store.error;

import store.model.Item;

public class PromotionConfirmationForFreeException extends RuntimeException {
    private Item item;
    private int orderQuantity;
    private int shortfall;

    public PromotionConfirmationForFreeException(Item item, int orderQuantity, int shortfall) {
        super();
        this.item = item;
        this.orderQuantity = orderQuantity;
        this.shortfall = shortfall;
    }


    public Item getItem() {
        return item;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public int getShortfall() {
        return shortfall;
    }
}
