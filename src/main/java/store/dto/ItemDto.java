package store.dto;

import store.model.item.Item;

public record ItemDto(String name, int price, String promotionName, int quantity) {
    public ItemDto(Item item) {
        this(item.getName(), item.getPrice(), item.getPromotionName(), item.getQuantity());
    }
}
